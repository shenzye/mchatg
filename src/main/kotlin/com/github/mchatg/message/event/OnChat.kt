package com.github.mchatg.message.event

import com.github.mchatg.BukkitEvent
import com.github.mchatg.Context
import com.github.mchatg.account.Player
import com.github.mchatg.until.getUserNameUrl
import org.bukkit.event.EventHandler
import org.bukkit.event.player.AsyncPlayerChatEvent

class OnChat(context: Context) : BukkitEvent<AsyncPlayerChatEvent>, Context by context {
    private val conversion_name = config.message.forward_chat.conversion_name
    private val group = config.global.bot.group_id //, 0
    private val forwardChats = config.message.forward_chat.enable

    @EventHandler
    override fun onCall(event: AsyncPlayerChatEvent) {
        if (!forwardChats.get())//TODO:conversion-name_better
            return


        var name: String = event.player.name
        var notification = true

        val id by lazy { accountManager.getTelegramUID(accountManager.getAccount(Player(event.player.name))) }
        val user by lazy { telegramBot.usersCache[id.value] }
        if (conversion_name.get() && id.isValid() && user != null) {


            name = getUserNameUrl(user!!)
            notification = false

        }
        telegramBot.send(group.get(), "$name: " + event.message, markdown = true, notification = notification)
    }


}