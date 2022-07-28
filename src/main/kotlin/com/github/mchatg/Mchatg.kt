package com.github.mchatg

import com.github.mchatg.message.event.OnChat
import com.github.mchatg.message.event.OnDeath
import com.github.mchatg.message.event.OnJoin
import com.github.mchatg.message.event.OnQuit
import com.github.mchatg.telegram.TelegramBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import java.util.logging.Logger


class Mchatg(
    override val serverApis: ServerApis,
    override val logger: Logger,
    override val unregisterEventHandler: (() -> Unit)?,
    override val config: Configuration,
) : Context {


    override val eventHandler: EventHandler = EventHandler()


    override val telegramBot: TelegramBot by lazy { TelegramBot(this) }


    fun onEnable() {

        telegramBot.init()
        if (config.message.serverUp) {
            telegramBot.send(
                SendMessage(
                    config.bot.groupId.toString(), "服务器已启动"
                )
            )
        }


        eventHandler.onChat = OnChat(this)::onCall
        if (config.message.onJoinEnable) {
            eventHandler.onJoin = OnJoin(this)::onCall
        }
        if (config.message.onDeathEnable) {
            eventHandler.onDeath = OnDeath(this)::onCall
        }
        if (config.message.onQuitEnable) {
            eventHandler.onQuit = OnQuit(this)::onCall

        }


//        if (config.message.enable) { //, false
//            messageManager.init()
//        }


    }

    fun onDisable() {
        unregisterEventHandler?.invoke()





        telegramBot.stop()


    }


}