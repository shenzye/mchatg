package com.github.mchatg.message.event

import com.github.mchatg.BukkitEvent
import com.github.mchatg.Context
import com.github.mchatg.until.EventMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerJoinEvent

class OnJoin(context: Context) : BukkitEvent<PlayerJoinEvent>, Context by context {

    private val eventMessage = EventMessage(
        telegramBot,
        config.message.onJoin.enable,
        config.message.onJoin.interval,
        config.global.bot.group_id
    )

    @EventHandler(priority = EventPriority.MONITOR)
    override fun onCall(event: PlayerJoinEvent) {
        eventMessage.send(event.player.name, event.player.name + " 已上线")

    }

}