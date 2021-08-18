package com.github.mchatg.message.event

import com.github.mchatg.Context
import com.github.mchatg.until.EventMessage

class OnQuit(context: Context) : Context by context {
    private val eventMessage = EventMessage(
        telegramBot,
        config.message.onQuit.enable,
        config.message.onQuit.interval,
        config.global.bot.groupId.toLongOrNull()?:0
    )


    fun onCall(player: String) {
        eventMessage.send(player, "$player 已退出")
    }
}