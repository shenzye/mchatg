package com.github.mchatg.message.event

import com.github.mchatg.BukkitEvent
import com.github.mchatg.Context
import org.bukkit.event.EventHandler
import org.bukkit.event.server.ServerLoadEvent

class OnServerLoad(context: Context) : BukkitEvent<ServerLoadEvent>, Context by context {
    private val server_up = config.message.server_up
    private val group = config.global.bot.group_id //, 0

    @EventHandler
    override fun onCall(event: ServerLoadEvent) {
        if (!server_up.get())//, false
            return
        telegramBot.send(group.get(), "服务器已启动")
    }
}