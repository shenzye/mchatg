package com.github.mchatg.telegram.command

import com.github.mchatg.Context
import com.github.mchatg.telegram.replyOnGroup
import com.github.mchatg.until.styleFiller
import org.bukkit.command.ConsoleCommandSender
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.*

class ConsoleCommandSenderWapper(val consoleSender: ConsoleCommandSender, context: Context, val update: Update) :
    ConsoleCommandSender by consoleSender, Context by context {

    override fun sendMessage(var1: String) {
        reply(var1)
//        consoleSender.sendMessage(var1)
    }

    override fun sendMessage(var1: Array<String?>) {
        reply(var1)
//        consoleSender.sendMessage(var1)
    }

    override fun sendMessage(var1: UUID?, var2: String) {
        reply(var2)
//        consoleSender.sendMessage(var1, var2)
    }

    override fun sendMessage(var1: UUID?, var2: Array<String?>) {
        reply(var2)
//        consoleSender.sendMessage(var1, var2)
    }

    override fun sendRawMessage(var1: String) {
        println("SRW:sendRawMessage($var1)")
        consoleSender.sendRawMessage(var1)
    }

    override fun sendRawMessage(var1: UUID?, var2: String) {
        println("SRW:sendRawMessage($var2)")
        consoleSender.sendRawMessage(var1, var2)
    }
    

    private fun reply(message: String) {


        telegramBot.send(
            update.message.chatId,
            styleFiller(message),
            reply = replyOnGroup(update)
        )
    }


    private fun reply(messages: Array<String?>) {
        var message: String = ""
        for (line in messages) {
            if (line != null) {
                if (message.length + "\n".length + line.length < 4096) {
                    message = "$message\n$line"
                } else {
                    reply(message)
                    message = line
                }
            }
        }
        reply(message)
    }

}