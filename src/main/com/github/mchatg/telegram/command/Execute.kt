package com.github.mchatg.telegram.command

import com.github.mchatg.Context
import com.github.mchatg.LogMe
import com.github.mchatg.account.DatabaseAccessor
import com.github.mchatg.account.TelegramUID
import com.github.mchatg.telegram.TelegramBot
import com.github.mchatg.telegram.TelegramCommand
import com.github.mchatg.telegram.replyOnGroup
import org.telegram.telegrambots.meta.api.objects.Update

class Execute(context: Context) : TelegramCommand, Context by context {
    override val name = this.javaClass.simpleName.toLowerCase()
    override fun execute(update: Update) {

        if (!config.command.execute)
            return


        val isAdmin by lazy {
            DatabaseAccessor.getAccount(TelegramUID(update.message.from.id)).isAdmin()
        }
        val args by lazy { update.message.text.split("\\s+".toRegex()) }


        when {
            !isAdmin -> telegramBot.send(
                update.message.chatId,
                "您缺乏执行此命令的权限",
                reply = replyOnGroup(update)
            )
            args.size <= 1 -> telegramBot.send(
                update.message.chatId,
                "请输入您想要执行的命令",
                reply = replyOnGroup(update)
            )
            else -> {
                var command = ""
                for (i in 1 until args.size) {
                    command = if (command == "") {
                        args[i]
                    } else {
                        "$command " + args[i]
                    }
                }

                LogMe.i(com.github.mchatg.until.getUserNameUrl(update.message.from) + "执行命令：$command")


                val session = TelegramBot.TelegramMessageSession(
                    telegramBot,
                    update.message.chatId,
                    800L,
                    replyOnGroup(update)
                )
                serverApis.dispatchCommand(
                    command
                ) {
                    session.send(it)
                }
                telegramBot.send(
                    update.message.chatId,
                    "已尝试执行您的命令",
                    reply = replyOnGroup(update)
                )
            }


        }


    }

    override fun getHelp(): String {
        return "/$name   --在服务器后台中执行minecraft命令"
    }


}