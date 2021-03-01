package com.github.mchatg.telegram

import com.github.mchatg.Context
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.ChatMember
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


    //TODO
    val usersCache: HashMap<Int, User> = HashMap()
    var admins: HashMap<Int, ChatMember> = HashMap()
        get() {
            bot?.apply {
                if (field.size == 0 || adminsCacheMeter > Calendar.getInstance()) {
                    field.clear()
                    execute(GetChatAdministrators().apply {
                        chatId = config.global.bot.group_id.get().toString()
                    }).forEach {
                        field[it.user.id] = it
                    }
                    adminsCacheMeter =//10分钟的缓存
                        Calendar.getInstance().apply { add(Calendar.MINUTE/*timeOutField*/, 10/*amount*/) }
                }
            }


            return field
        }


    var adminsCacheMeter: Calendar = Calendar.getInstance()
    val token by lazy { context.config.global.bot.token.get() }
    val username by lazy { context.config.global.bot.username.get() }
    fun send(
        chatId: Long,
        message: String,
        markdown: Boolean = false,
        notification: Boolean = true,
        reply: Int? = null
    ) {
        bot?.executeAsync(
            SendMessage(chatId.toString(), message)
                .apply {
                    enableMarkdown(markdown)
                    if (!notification) disableNotification()
                    replyToMessageId = reply
                })


        //    public SendMessage(@NonNull String chatId, @NonNull String text, String parseMode, Boolean disableWebPagePreview, Boolean disableNotification, Integer replyToMessageId, ReplyKeyboard replyMarkup, List<MessageEntity> entities, Boolean allowSendingWithoutReply) {


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
                config.command.enable.get() -> commandHandler.handle(update)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    inner class Bot : TelegramLongPollingBot(DefaultBotOptions().apply {
        if (config.global.proxy.enable.get()) {
            proxyType = when (config.global.proxy.type.get()) {
                "http" -> DefaultBotOptions.ProxyType.HTTP
                "socks4" -> DefaultBotOptions.ProxyType.SOCKS4
                "socks5" -> DefaultBotOptions.ProxyType.SOCKS5
                else -> return@apply
            }
            proxyHost = config.global.proxy.host.get()
            proxyPort = config.global.proxy.port.get()
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





