package com.github.mchatg.telegram

import com.github.mchatg.Context
import org.telegram.telegrambots.meta.api.objects.Update

class MessagesHandler(context: Context) : Context by context {

    fun handle(update: Update) {

        try {
            if (update.message.isSuperGroupMessage &&
                update.message.chatId == config.global.bot.groupId.toLongOrNull() ?: 0
            )
                handleGroup(update)
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }


    private fun handleGroup(update: Update) {
        messageManager.telegramMessagesHandler.handler(update.message)
    }


}