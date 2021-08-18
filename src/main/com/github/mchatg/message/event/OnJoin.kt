package com.github.mchatg.message.event

import com.github.mchatg.Context
import com.github.mchatg.until.EventMessage

class OnJoin(context: Context) :  Context by context {

    private val eventMessage = EventMessage(
        telegramBot,
        config.message.onJoin.enable,
        config.message.onJoin.interval,
        config.global.bot.groupId.toLongOrNull()?:0
    )

     fun onCall(player: String) {
        eventMessage.send(player, "$player 已上线")

    }

}