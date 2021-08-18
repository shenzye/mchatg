package com.github.mchatg.message

import com.github.mchatg.Context
import com.github.mchatg.message.event.OnChat
import com.github.mchatg.message.event.OnDeath
import com.github.mchatg.message.event.OnJoin
import com.github.mchatg.message.event.OnQuit

class MessageManager(context: Context) : Context by context {


    val telegramMessagesHandler by lazy { TelegramMessagesHandler(this) }


    fun init() {


        eventHandler.onChat = OnChat(this)::onCall
        eventHandler.onJoin = OnJoin(this)::onCall
        eventHandler.onDeath = OnDeath(this)::onCall
        eventHandler.onQuit = OnQuit(this)::onCall
    }


}
