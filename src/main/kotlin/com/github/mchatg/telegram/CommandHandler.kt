package com.github.mchatg.telegram

import com.github.mchatg.Context
import com.github.mchatg.telegram.command.Execute
import com.github.mchatg.telegram.command.Help
import com.github.mchatg.telegram.command.List
import com.github.mchatg.telegram.command.Ping
import org.telegram.telegrambots.meta.api.objects.Update

class CommandHandler private constructor(context: Context) : Context by context {

    val commands: HashMap<String, TelegramCommand?> = HashMap()
    val help = Help(this)

    companion object {
        private var handler: CommandHandler? = null

        fun getInstance(context: Context): CommandHandler {
            if (handler == null) {
                handler = CommandHandler(context)
                registerTelegramCommand(handler!!.help)
                registerTelegramCommand(Ping(handler!!))
                registerTelegramCommand(List(handler!!))
                registerTelegramCommand(Execute(handler!!))
            }
            return handler!!
        }
    }


    fun handle(update: Update) {
        if (!update.message.isUserMessage &&
            update.message.chatId != (config.bot.groupId)
        )
            return

        val index = update.message.text.indexOf(" ")
        if (index > 0) {
            commands.getOrDefault(update.message.text.substring(0, index), null)?.execute(update)
        } else {
            commands.getOrDefault(update.message.text, null)?.execute(update)
        }


    }


}





