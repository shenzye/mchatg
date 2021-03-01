package com.github.mchatg.message.event

import com.github.mchatg.BukkitEvent
import com.github.mchatg.Context
import com.github.mchatg.until.EventMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent

class OnDeath(context: Context) : BukkitEvent<PlayerDeathEvent>, Context by context {

    private val eventMessage = EventMessage(
        telegramBot,
        config.message.onDeath.enable,
        config.message.onDeath.interval,
        config.global.bot.group_id
    )

    @EventHandler
    override fun onCall(event: PlayerDeathEvent) {
        event.deathMessage?.let {
            eventMessage.send(event.entity.name, it)
        }
    }

}