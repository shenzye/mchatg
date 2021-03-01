package com.github.mchatg.telegram.command

import com.github.mchatg.Context
import com.github.mchatg.telegram.TelegramCommand
import org.telegram.telegrambots.meta.api.objects.Update

class Help(context: Context) : TelegramCommand, Context by context {
    override val name = this.javaClass.simpleName.toLowerCase()
    private var helpInfo: String = ""
    override fun execute( update: Update) {

        telegramBot.send(
            update.message.chatId,
            helpInfo
        )

    }

    fun registerHelpInfo(command: TelegramCommand) {
        helpInfo = helpInfo + command.getHelp() + "\n"
    }

    override fun getHelp(): String {
        return "/$name   --查看命令信息"
    }


}