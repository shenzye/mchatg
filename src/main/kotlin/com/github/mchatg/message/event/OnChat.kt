package com.github.mchatg.message.event

import com.github.mchatg.Context
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

class OnChat(context: Context) : Context by context {
    private val group
        get() = config.bot.groupId //, 0
    private val forwardChats
        get() = config.message.forwardChatEnable

     fun onCall(player: String, message: String) {
        if (!forwardChats)
            return

        telegramBot.send(
            SendMessage(group.toString(), "$player : $message")
                .apply {
                    disableNotification()

                }


        )






    }


}