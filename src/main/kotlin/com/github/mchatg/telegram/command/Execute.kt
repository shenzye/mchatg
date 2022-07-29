package com.github.mchatg.telegram.command

import com.github.mchatg.Context
import com.github.mchatg.telegram.TelegramBot.Companion.replyOnGroup
import com.github.mchatg.telegram.TelegramCommand
import com.github.mchatg.telegram.TelegramMessageSession
import com.github.mchatg.until.styleFilter

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.*

class Execute(context: Context) : TelegramCommand, Context by context {
    override val name = this.javaClass.simpleName.lowercase(Locale.ENGLISH)
    override fun execute(update: Update) {

        if (!config.command.execute)
            return


        val isAdmin by lazy {

            config.command.admins.contains(
                update.message.from.id
            )
        }
        val args by lazy { update.message.text.split("\\s+".toRegex()) }


        when {
            !isAdmin -> telegramBot.send(
                SendMessage(
                    update.message.chatId.toString(),
                    "您缺乏执行此命令的权限",
                ).apply {
                    replyToMessageId = replyOnGroup(update)
                }
            )
            args.size <= 1 -> telegramBot.send(
                SendMessage(
                    update.message.chatId.toString(),
                    "请输入您想要执行的命令",
                ).apply {
                    replyToMessageId = replyOnGroup(update)
                }
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
                logger.info(update.message.from.let { it.userName?:it.id.toString() }   + " 执行命令：$command")
                val session = TelegramMessageSession(
                    telegramBot,
                    300,
                    update.message.chatId,
                    replyOnGroup(update)
                )
                serverApis.dispatchCommand(
                    command
                ) {
                    session.send(styleFilter(it))
                }
                telegramBot.send(
                    SendMessage(
                        update.message.chatId.toString(),
                        "已尝试执行您的命令",
                        ).apply {
                        replyToMessageId = replyOnGroup(update)
                    }
                )


            }


        }


    }




    override fun getHelp(): String {
        return "/$name   --在服务器后台中执行minecraft命令"
    }


}