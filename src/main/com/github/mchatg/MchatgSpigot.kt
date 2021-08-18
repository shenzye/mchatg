package com.github.mchatg

import org.bukkit.command.ConsoleCommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class MchatgSpigot : JavaPlugin() {
//    override val pluginFolder: String = this.dataFolder.path


    private val eventHandler: com.github.mchatg.EventHandler = com.github.mchatg.EventHandler()
    private val apis: ServerApis = Apis(this)
    val mchatg: Mchatg = Mchatg(dataFolder.path, eventHandler, apis)

    override fun onEnable() {
        mchatg.onEnable()
        server.pluginManager.registerEvents(
            EventHandlers(), this
        )
    }

    override fun onDisable() {

        mchatg.onDisable()
    }

    inner class EventHandlers : Listener {
        @EventHandler(priority = EventPriority.LOWEST)
        fun onLogin(event: PlayerLoginEvent) {
            eventHandler.onLogin ?: return
            val control: LoginControl = eventHandler.onLogin!!(event.player.name)
            if (control is LoginControl.Disallow) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, control.reason)
            }
        }

        @EventHandler
        fun onChat(event: AsyncPlayerChatEvent) {
            eventHandler.onChat ?: return
            eventHandler.onChat!!(event.player.name, event.message)
        }

        @EventHandler
        fun onDeath(event: PlayerDeathEvent) {
            eventHandler.onDeath ?: return
            event.deathMessage ?: return

            eventHandler.onDeath!!(event.entity.name, event.deathMessage!!)
        }

        @EventHandler
        fun onJoin(event: PlayerJoinEvent) {
            eventHandler.onJoin ?: return
            eventHandler.onJoin!!(event.player.name)
        }

        @EventHandler
        fun onQuit(event: PlayerQuitEvent) {

            eventHandler.onQuit ?: return

            eventHandler.onQuit!!(event.player.name)
        }

    }

    inner class Apis(private val javaPlugin: JavaPlugin) : ServerApis {
        override fun getOnlinePlayers(): List<String> {
            val players = ArrayList<String>()
            for (player in server.onlinePlayers) {
                if (player.name.isNotBlank()) {
                    players.add(player.name)
                }
            }
            return players
        }

        override fun kickPlayer(players: Iterable<String>) {
            server.scheduler.scheduleSyncDelayedTask(javaPlugin) {
                players.forEach {
                    server.getPlayer(it)?.kickPlayer(it)
                }
            }

        }

        override fun dispatchCommand(command: String, callBack: ((message: String) -> Unit)?) {
            server.scheduler.scheduleSyncDelayedTask(javaPlugin) {
                val consoleSender = server.consoleSender
                server.dispatchCommand(
                    object : ConsoleCommandSender by consoleSender {
                        override fun sendRawMessage(var1: String) {
                            println("SRW($var1)")
                            consoleSender.sendRawMessage(var1)
                            callBack?.let { it(var1) }
                        }

                        override fun sendRawMessage(var1: UUID?, var2: String) {
                            println("SRW(UUID:$var1,message:$var2)")
                            consoleSender.sendRawMessage(var1, var2)
                            callBack?.let { it(var2) }
                        }

                    },
                    command
                )
            }


        }


    }


}