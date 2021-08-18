package com.github.mchatg.account.command.telegram

import com.github.mchatg.Context
import com.github.mchatg.LogMe
import com.github.mchatg.account.Checkable
import com.github.mchatg.account.DatabaseAccessor
import com.github.mchatg.account.TelegramUID
import com.github.mchatg.account.TokenManager
import com.github.mchatg.telegram.TelegramCommand
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.*

class Bind(context: Context) : TelegramCommand, Context by context {
    override val name = this.javaClass.simpleName.lowercase(Locale.getDefault())
    override fun execute(update: Update) {
        LogMe.i(update)


        if (!update.message.isUserMessage) {
            telegramBot.send(
                update.message.chatId,
                "请私聊我开始使用",
                reply = update.message.messageId
            )
            return
        }


        val args = update.message.text.split("\\s+".toRegex())

        val uid = TelegramUID(update.message?.from?.id)
        var success = false


        TokenManager.decodeToken(args.getOrNull(1))?.let {

            success = when {
                Checkable.hasInvalid(it) -> false
                DatabaseAccessor.getTelegramUID(it.account).isValid() -> false
                else -> DatabaseAccessor.setTelegram(it.account, uid)

            }


        }

        telegramBot.send(
            update.message.chatId,
            if (success) "成功绑定"
            else "绑定失败,参数错误或目标账户已被绑定"
        )


    }


    override fun getHelp(): String {
        return "/$name key   --将电报帐号绑定到指定游戏帐号"
    }


}