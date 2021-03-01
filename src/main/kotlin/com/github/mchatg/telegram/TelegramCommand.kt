package com.github.mchatg.telegram

import com.github.mchatg.Context
import org.telegram.telegrambots.meta.api.objects.Update

interface TelegramCommand : Context {

    fun execute(update: Update)
    val name: String
    fun getHelp(): String {
        return "/$name   --此方法暂无介绍"
    }
}

fun registerTelegramCommand(command: TelegramCommand) {
    command.telegramBot.commandHandler.run {
        commands["/" + command.name] = command
        commands["/" + command.name + "@" + telegramBot.username] = command
        help.registerHelpInfo(command)
    }
}