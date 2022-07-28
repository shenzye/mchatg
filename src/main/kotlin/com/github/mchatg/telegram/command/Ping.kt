package com.github.mchatg.telegram.command

import com.github.mchatg.Context
import com.github.mchatg.telegram.TelegramBot.Companion.replyOnGroup
import com.github.mchatg.telegram.TelegramCommand
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.*

class Ping(context: Context) : TelegramCommand, Context by context {
    override val name = this.javaClass.simpleName.lowercase(Locale.ENGLISH)
    override fun execute(update: Update) {
        if (!config.command.ping)
            return

        telegramBot.send(
            SendMessage(
                update.message.chatId.toString(),
                "pong!",
            ).apply {
                replyToMessageId = replyOnGroup(update)
            }
        )


    }

    override fun getHelp(): String {
        return "/$name   --pong!"
    }
}