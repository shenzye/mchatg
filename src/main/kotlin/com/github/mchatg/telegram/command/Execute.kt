package com.github.mchatg.telegram.command

import com.github.mchatg.Context
import com.github.mchatg.account.TelegramUID
import com.github.mchatg.telegram.TelegramCommand
import com.github.mchatg.telegram.replyOnGroup
import org.telegram.telegrambots.meta.api.objects.Update

class Execute(context: Context) : TelegramCommand, Context by context {
    override val name = this.javaClass.simpleName.toLowerCase()
    override fun execute(update: Update) {

        if (!config.command.execute.get())
            return


        val isAdmin by lazy { accountManager.isAdmin(accountManager.getAccount(TelegramUID(update.message.from.id))) }
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

                log.i(com.github.mchatg.until.getUserNameUrl(update.message.from) + "执行命令：$command")

                plugin.server.scheduler.scheduleSyncDelayedTask(plugin) {
                    plugin.server.dispatchCommand(
                        ConsoleCommandSenderWapper(
                            plugin.server.consoleSender,
                            this,
                            update
                        ),
                        command
                    )
                }
                println()

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