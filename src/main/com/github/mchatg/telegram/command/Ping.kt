package com.github.mchatg.telegram.command

import com.github.mchatg.Context
import com.github.mchatg.telegram.TelegramCommand
import com.github.mchatg.telegram.replyOnGroup
import org.telegram.telegrambots.meta.api.objects.Update

class Ping(context: Context) : TelegramCommand, Context by context {
    override val name = this.javaClass.simpleName.toLowerCase()
    override fun execute(update: Update) {
        if (!config.command.ping)
            return

        telegramBot.send(
            update.message.chatId,
            "pong!",
            reply = replyOnGroup(update)
        )


    }

    override fun getHelp(): String {
        return "/$name   --pong!"
    }
}