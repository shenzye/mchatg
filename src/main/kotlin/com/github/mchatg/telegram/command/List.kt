package com.github.mchatg.telegram.command

import com.github.mchatg.Context
import com.github.mchatg.telegram.TelegramCommand
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.*

class List(context: Context) : TelegramCommand, Context by context {
    override val name = this.javaClass.simpleName.lowercase(Locale.ENGLISH)
    override fun execute(update: Update) {
        if (!config.command.list)
            return

        var players: String = ""
        val onlinePlayers = serverApis.getOnlinePlayers()

        for (player in onlinePlayers) {
            if (players != "")
                players = "$players,"
            players += player
        }
        val list = if (players != "") "目前在线的玩家有：$players"
        else "目前没有在线玩家"
        telegramBot.send(
            SendMessage(
                update.message.chatId.toString(),
                list
            )


        )

    }

    override fun getHelp(): String {

        return "/$name   --显示在线玩家"
    }
}