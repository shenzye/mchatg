package com.github.mchatg.account.command.telegram

import com.github.mchatg.Context
import com.github.mchatg.account.TelegramUID
import com.github.mchatg.telegram.TelegramCommand
import org.telegram.telegrambots.meta.api.objects.Update

class Kickme(context: Context) : TelegramCommand, Context by context {
    override val name = this.javaClass.simpleName.toLowerCase()
    override fun execute(update: Update) {
        log.i(update)
        if (!config.command.kickme.get())
            return

        val uid = TelegramUID(update.message?.from?.id)
        val account = accountManager.getAccount(uid)
        val names = accountManager.getPlayers(account)

        plugin.server.scheduler.scheduleSyncDelayedTask(plugin) {
            names.forEach {
                plugin.server.getPlayer(it)?.kickPlayer(it)
            }
        }
    }

    override fun getHelp(): String {
        return "/$name   --从服务器中踢出自己的所有帐号"
    }


}