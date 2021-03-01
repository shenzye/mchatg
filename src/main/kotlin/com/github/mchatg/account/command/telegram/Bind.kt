package com.github.mchatg.account.command.telegram

import com.github.mchatg.Context
import com.github.mchatg.account.Checkable
import com.github.mchatg.account.TelegramUID
import com.github.mchatg.account.Token
import com.github.mchatg.account.TokenManager
import com.github.mchatg.telegram.TelegramCommand
import org.telegram.telegrambots.meta.api.objects.Update

class Bind(context: Context) : TelegramCommand, Context by context {
    override val name = this.javaClass.simpleName.toLowerCase()
    override fun execute(update: Update) {
        log.i(update)


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


        TokenManager.getToken(args.getOrNull(1))?.let {
            success = when {
                Checkable.hasInvalid(it) -> false
                accountManager.getTelegramUID(it.account).isValid() -> false
                else -> accountManager.setTelegram(Pair(it.account, uid))

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