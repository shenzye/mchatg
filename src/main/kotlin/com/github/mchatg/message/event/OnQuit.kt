package com.github.mchatg.message.event

import com.github.mchatg.BukkitEvent
import com.github.mchatg.Context
import com.github.mchatg.until.EventMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent

class OnQuit(context: Context) : BukkitEvent<PlayerQuitEvent>, Context by context {
    private val eventMessage = EventMessage(
        telegramBot,
        config.message.onQuit.enable,
        config.message.onQuit.interval,
        config.global.bot.group_id
    )

    @EventHandler
    override fun onCall(event: PlayerQuitEvent) {
        eventMessage.send(event.player.name,event.player.name + " 已退出")
    }
}