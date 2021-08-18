package com.github.mchatg.message.event

import com.github.mchatg.Context
import com.github.mchatg.until.EventMessage

class OnDeath(context: Context) : Context by context {

    private val eventMessage = EventMessage(
        telegramBot,
        config.message.onDeath.enable,
        config.message.onDeath.interval,
        config.global.bot.groupId.toLongOrNull() ?: 0
    )


    fun onCall(player: String, message: String) {
        eventMessage.send(player, message)
    }

}