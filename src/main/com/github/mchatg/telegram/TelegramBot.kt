package com.github.mchatg.telegram

import com.github.mchatg.Context
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.selects.select
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import java.util.*

class TelegramBot(private val context: Context) : Context by context {

    private var bot: Bot? = null
    val commandHandler by lazy { CommandHandler.getInstance(context) }
    val messagesHandler by lazy { MessagesHandler(context) }


    fun init() {

        //  botSession =

        try {
            val b = Bot()
            TelegramBotsApi(DefaultBotSession::class.java).apply { registerBot(b) }
            bot = b
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    val usersCache: HashMap<Long, User> = HashMap()

    val token by lazy { config.global.bot.token }
    val username by lazy { config.global.bot.username }
    fun send(
        chatId: Long,
        message: String,
        markdown: Boolean = false,
        notification: Boolean = true,
        reply: Int? = null
    ) {

        if (message.length > 4096) {
            //TODO:切割数组
        }


        bot?.executeAsync(
            SendMessage(chatId.toString(), message)
                .apply {
                    enableMarkdown(markdown)
                    if (!notification) disableNotification()
                    replyToMessageId = reply
                })


        //    public SendMessage(@NonNull String chatId, @NonNull String text, String parseMode, Boolean disableWebPagePreview, Boolean disableNotification, Integer replyToMessageId, ReplyKeyboard replyMarkup, List<MessageEntity> entities, Boolean allowSendingWithoutReply) {


    }


    @OptIn(DelicateCoroutinesApi::class)
    class TelegramMessageSession(
        val telegramBot: TelegramBot,
        val chatId: Long,
        val flushTime: Long,
        val reply: Int? = null
    ) {
        private var messagesChannel = Channel<String>(8)

        init {
            go()
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        fun send(message: String) {
            if (messagesChannel.isClosedForSend) {
                messagesChannel = Channel<String>(8)
                go()
            }
            //试三次
            when {
                messagesChannel.trySend(message).isFailure -> {
                    return
                }
                messagesChannel.trySend(message).isFailure -> {
                    return
                }
                messagesChannel.trySend(message).isFailure -> {
                    return
                }
            }


        }

        private fun go() {
            GlobalScope.launch {
                var messageBuffer: StringBuffer = StringBuffer()
                //TODO:test!!!!!!!!!!!!!!!!
                var loop = true
                while (loop) {
                    loop = select {
                        messagesChannel.onReceiveCatching { result ->
                            val message = result.getOrNull() ?: return@onReceiveCatching false
                            when {
                                messageBuffer.isBlank() -> {
                                    messageBuffer = StringBuffer(message)
                                }
                                messageBuffer.length + "\n".length + message.length < 4096 -> {
                                    messageBuffer.append("\n").append(message)
                                }
                                message.length >= 4096 -> {
                                    telegramBot.send(chatId, messageBuffer.toString(), reply = reply)
                                    //TODO:分割
                                    //TODO:检查！！！！！！！！！！！！！！！！！！！！
                                    messageBuffer = StringBuffer()
                                    val lines = message.split("\n")
                                    for (line in lines) {
                                        if (messageBuffer.length + line.length >= 4096) {
                                            telegramBot.send(chatId, messageBuffer.toString(), reply = reply)
                                            messageBuffer = if (line.length >= 4096) {
                                                telegramBot.send(chatId, line, reply = reply)
                                                StringBuffer()
                                            }else{
                                                StringBuffer(line)
                                            }
                                        } else {
                                            messageBuffer.append(line)
                                        }
                                    }
                                }
                                else -> {
                                    telegramBot.send(chatId, messageBuffer.toString(), reply = reply)
                                    messageBuffer = StringBuffer(message)
                                }
                            }
                            true
                        }
                        async {
                            delay(flushTime)
                            messagesChannel.close()//提前close，避免问题
                            false
                        }
                    }
                }
                telegramBot.send(chatId, messageBuffer.toString(), reply = reply)
            }
        }
    }


    private val timeOut: Int = 30//s
    fun onReceived(update: Update) {

        if (Date().after(Date((update.message.date + timeOut) * 1000L))) {
            println("debug:have message time out")
            return
        }


//        try {
//            log.d(update.message.chat.id)
//            log.d(update.message.chatId)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

        try {
            update.message?.from?.let { usersCache[it.id] = it }


            when {
                update.message == null -> return
                !update.message.isCommand -> messagesHandler.handle(update)
                config.command.enable -> commandHandler.handle(update)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    inner class Bot : TelegramLongPollingBot(DefaultBotOptions().apply {
        if (config.global.proxy.enable) {
            proxyType = when (config.global.proxy.type) {
                "http" -> DefaultBotOptions.ProxyType.HTTP
                "socks4" -> DefaultBotOptions.ProxyType.SOCKS4
                "socks5" -> DefaultBotOptions.ProxyType.SOCKS5
                else -> return@apply
            }
            proxyHost = config.global.proxy.host
            proxyPort = config.global.proxy.port.toInt()
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


fun replyOnGroup(update: Update): Int? {
    return if (!update.message.isUserMessage) update.message.messageId else null


}





