package com.github.mchatg.telegram.command

import com.github.mchatg.Context
import com.github.mchatg.telegram.TelegramCommand
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.*

class Help(context: Context) : TelegramCommand, Context by context {
    override val name = this.javaClass.simpleName.lowercase(Locale.ENGLISH)
    private var helpInfo: String = ""
    override fun execute( update: Update) {

        telegramBot.send(
            SendMessage(
                update.message.chatId.toString(),
                helpInfo
            )

        )

    }

    fun registerHelpInfo(command: TelegramCommand) {
        helpInfo = helpInfo + command.getHelp() + "\n"
    }

    override fun getHelp(): String {
        return "/$name   --查看命令信息"
    }


}