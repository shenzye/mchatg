package com.github.mchatg.telegram

import com.github.mchatg.Context
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.BotSession
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import java.util.*
import java.util.concurrent.CompletableFuture

class TelegramBot(private val context: Context) : Context by context {

    private var bot: Bot? = null
    private var session: BotSession? = null
    val commandHandler by lazy { CommandHandler.getInstance(context) }
    val messagesHandler by lazy { MessagesHandler(context) }
    private val chan: Channel<SendMessage> = Channel(64)


    @OptIn(ExperimentalCoroutinesApi::class)
    fun stop() {

        try {
            chan.close()
            while (!chan.isClosedForReceive) {
                Thread.sleep(10)
            }
            session?.stop()
            bot?.onClosing()

        } catch (e: Exception) {
            logger.warning("Close bot failed .")
            logger.warning(e.toString())
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    fun init() {
        try {
            val b = Bot()
            val tgBotApi = TelegramBotsApi(DefaultBotSession::class.java)
            session = tgBotApi.registerBot(b)
            bot = b

            CompletableFuture.runAsync {
                runBlocking {
                    while (!chan.isClosedForReceive) {
                        select {
                            chan.onReceiveCatching {
                                val message: SendMessage? = it.getOrNull()
                                if (message != null) {
                                    processMessages(message).forEach {
                                        var retry = 0
                                        while (bot != null && retry < 3) {
                                            try {
                                                bot?.execute(it)
                                                break
                                            } catch (e: Exception) {
                                                retry += 1
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                }

            }
        } catch (e: Exception) {
            logger.severe(e.toString())
        }

    }


    private val token by lazy { config.bot.token }
    val username by lazy { config.bot.username }

    fun send(message: SendMessage) {
        if (chan.trySend(message).isFailure) {
            logger.info("send message fail , it is too quickly")
        }
    }


    companion object {
        fun cloneSendMessage(message: SendMessage, replaceText: String? = null): SendMessage {
            return SendMessage().apply {
                this.chatId = message.chatId;
                this.text = replaceText ?: text
                this.parseMode = message.parseMode;
                this.disableWebPagePreview = message.disableWebPagePreview;
                this.disableNotification = message.disableNotification;
                this.replyToMessageId = message.replyToMessageId;
                this.replyMarkup = message.replyMarkup;
                this.entities = message.entities;
                this.allowSendingWithoutReply = message.allowSendingWithoutReply;
                this.protectContent = message.protectContent;
            }
        }

        fun processMessages(sendMessage: SendMessage): Iterable<SendMessage> {

            val processedMessages = arrayListOf<SendMessage>()
            var messageBuffer: StringBuffer = StringBuffer()

            for (message in sendMessage.text.split("\n")) {
                when {
                    messageBuffer.isBlank() && message.length < 4096 -> {
                        messageBuffer = StringBuffer(message)
                    }

                    messageBuffer.length + "\n".length + message.length < 4096 -> {
                        messageBuffer.append("\n").append(message)
                    }

                    message.length < 4096 -> {
                        processedMessages.add(
                            cloneSendMessage(sendMessage, messageBuffer.toString())
                        )
                        messageBuffer = StringBuffer(message)
                    }
                    //单行长度大于4096的处理
                    else -> {
                        processedMessages.add(
                            cloneSendMessage(sendMessage, messageBuffer.toString())
                        )
                        messageBuffer = StringBuffer()
                        for (word in message.split(" ")) {
                            when {
                                word.isBlank() -> {
                                    messageBuffer.append(" ")
                                }
                                messageBuffer.length + " ".length + word.length < 4096 -> {
                                    messageBuffer.append(" ").append(word)
                                }

                                word.length < 96 -> {
                                    processedMessages.add(
                                        cloneSendMessage(sendMessage, messageBuffer.toString())
                                    )
                                    messageBuffer = StringBuffer(word)
                                }
                                //word.length >= 96
                                else -> {
                                    //TODO:test

                                    messageBuffer.append(
                                        word.subSequence(
                                            0..(4095 - messageBuffer.length)
                                        )
                                    )
                                    processedMessages.add(
                                        cloneSendMessage(sendMessage, messageBuffer.toString())
                                    )
                                    messageBuffer = StringBuffer(
                                        word.subSequence(
                                            (4095 - messageBuffer.length)..word.length - 1
                                        )
                                    )
                                }
                            }


                        }

                        processedMessages.add(
                            cloneSendMessage(sendMessage, messageBuffer.toString())
                        )
                        messageBuffer = StringBuffer()

                    }
                }


            }

            if (messageBuffer.isNotBlank()) {
                processedMessages.add(
                    cloneSendMessage(sendMessage, messageBuffer.toString())
                )
            }
            return processedMessages
        }

        fun replyOnGroup(update: Update): Int? {
            return if (!update.message.isUserMessage) update.message.messageId else null


        }

    }


    private val timeOut: Int = 30//s
    fun onReceived(update: Update) {
        try {
            when {
                update.message == null -> return
                Date().after(Date((update.message.date + timeOut) * 1000L)) -> {
                    logger.info("have message time out")
                }
                !update.message.isCommand -> messagesHandler.handle(update)
                config.command.enable -> commandHandler.handle(update)
            }
        } catch (e: Exception) {
            logger.warning(e.toString())
        }
    }


    inner class Bot : TelegramLongPollingBot(DefaultBotOptions().apply {
        if (config.proxy.enable) {
            proxyType = when (config.proxy.type) {
                "http" -> DefaultBotOptions.ProxyType.HTTP
                "socks4" -> DefaultBotOptions.ProxyType.SOCKS4
                "socks5" -> DefaultBotOptions.ProxyType.SOCKS5
                else -> return@apply
            }
            proxyHost = config.proxy.host
            proxyPort = config.proxy.port.toInt()
        }
    }) {
        override fun onUpdateReceived(update: Update) {
            onReceived(update)
        }

        override fun getBotToken(): String {
            return token
        }

        override fun getBotUsername(): String {
            return username
        }


    }


}







