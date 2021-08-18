package com.github.mchatg.message.event

import com.github.mchatg.Context
import com.github.mchatg.account.DatabaseAccessor
import com.github.mchatg.account.Player
import com.github.mchatg.until.getUserNameUrl

class OnChat(context: Context) : Context by context {
    private val conversionName
        get() = config.message.forwardChat.conversionName
    private val group
        get() = config.global.bot.groupId.toLongOrNull() ?: 0 //, 0
    private val forwardChats
        get() = config.message.forwardChat.enable

    fun onCall(player: String, message: String) {
        if (!forwardChats)//TODO:conversion-name_better
            return


        var name: String = player
        var notification = true

        val id by lazy { DatabaseAccessor.getTelegramUID(DatabaseAccessor.getAccount(Player(player))) }
        val user by lazy { telegramBot.usersCache[id.value] }
        if (conversionName && id.isValid() && user != null) {


            name = getUserNameUrl(user!!)
            notification = false

        }
        telegramBot.send(group, "$name: $message", markdown = true, notification = notification)
    }


}