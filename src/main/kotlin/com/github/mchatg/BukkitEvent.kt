package com.github.mchatg

import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

interface BukkitEvent<T : Event> : Listener, Context {

    @EventHandler
    fun onCall(event: T)

}

fun <T : Event> registerBukkitEvent(event: BukkitEvent<T>) {
    try {
        event.plugin.server.pluginManager.registerEvents(event, event.plugin)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}