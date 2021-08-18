package com.github.mchatg.account.command.telegram

import com.github.mchatg.Context
import com.github.mchatg.LogMe
import com.github.mchatg.account.DatabaseAccessor
import com.github.mchatg.account.TelegramUID
import com.github.mchatg.telegram.TelegramCommand
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.*

class KickMe(context: Context) : TelegramCommand, Context by context {
    override val name = this.javaClass.simpleName.lowercase(Locale.getDefault())
    override fun execute(update: Update) {
        LogMe.i(update)
        if (!config.command.kickMe)
            return

        //TODO：转换
        val uid = TelegramUID(update.message?.from?.id)
        val account = DatabaseAccessor.getAccount(uid)
        val players = DatabaseAccessor.getPlayers(account)

        serverApis.kickPlayer(players)


    }

    override fun getHelp(): String {
        return "/$name   --从服务器中踢出自己的所有帐号"
    }


}