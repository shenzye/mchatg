package com.github.mchatg.message

import com.github.mchatg.Context
import com.github.mchatg.message.event.*
import com.github.mchatg.registerBukkitEvent

class MessageManager(context: Context) : Context by context {


    val telegramMessagesHandler by lazy { TelegramMessagesHandler(this) }


    fun init() {


        registerBukkitEvent(OnServerLoad(this))
        registerBukkitEvent(OnChat(this))

        registerBukkitEvent(OnJoin(this))
        registerBukkitEvent(OnQuit(this))
        registerBukkitEvent(OnDeath(this))
    }


}
