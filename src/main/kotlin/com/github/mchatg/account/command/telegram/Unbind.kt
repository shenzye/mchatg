package com.github.mchatg.account.command.telegram

import com.github.mchatg.Context
import com.github.mchatg.account.TelegramUID
import com.github.mchatg.account.Token
import com.github.mchatg.telegram.TelegramCommand
import org.telegram.telegrambots.meta.api.objects.Update

class Unbind(context: Context) : TelegramCommand, Context by context {
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

        var success = false

        val uid = TelegramUID(update.message?.from?.id)
        val account = accountManager.getAccount(uid)
        if (account.isValid()) {
            val token = Token.TelegramToken(this, uid)
            success = accountManager.setTelegram(Pair(token.account, TelegramUID(null)))

        }






        telegramBot.send(
            update.message.chatId,
            if (success) "成功解绑"
            else "解绑失败，您的电报帐号并未绑定任何游戏帐号"
        )


    }


    override fun getHelp(): String {
        return "/$name   --解除电报帐号与游戏帐号的绑定"
    }


}