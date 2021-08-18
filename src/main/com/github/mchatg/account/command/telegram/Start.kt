package com.github.mchatg.account.command.telegram

import com.github.mchatg.Context
import com.github.mchatg.LogMe
import com.github.mchatg.account.*
import com.github.mchatg.telegram.TelegramCommand
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.*

class Start(context: Context) : TelegramCommand, Context by context {
    override val name = this.javaClass.simpleName.lowercase(Locale.getDefault())
    override fun execute(update: Update) {
        LogMe.i(update)
        val token by lazy {
            Token.TelegramToken(TelegramUID(update.message?.from?.id))
        }
        if (update.message.isUserMessage &&
            Checkable.allValid(token)
        ) {

            //TODO:解锁失败，失败列表
            val list = DatabaseAccessor.getPlayers(token.account)
            for (player in list) {
                if (
                    !Player(player).unlock(
                        Calendar.getInstance().also { it.add(Calendar.MINUTE, 2) }.timeInMillis
                    )
                ) {
                    list.remove(player)
                }
            }

            var names = ""
            for (name in list) {
                names = if (names == "") name
                else "$names,$name"
            }
            telegramBot.send(
                update.message.chatId,
                if (names == "") "您未注册过任何用户名，请使用以下命令注册 /register your_user_name\n注意区分大小写！！！"
                else "用户名：$names 已解锁"
            )
        } else {
            telegramBot.send(
                update.message.chatId,
                "请私聊我开始使用",
                reply = update.message.messageId
            )

        }

    }

    override fun getHelp(): String {
        return "/$name   --解锁游戏名登录限制，请私聊使用"
    }


}