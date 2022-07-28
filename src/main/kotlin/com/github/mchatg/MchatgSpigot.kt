package com.github.mchatg

import org.bukkit.Bukkit
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*

class MchatgSpigot : JavaPlugin() {
    private val apis: ServerApis = Apis(this)
    var mchatg: Mchatg? = null

    val eventHandler: com.github.mchatg.EventHandler?
        get() = mchatg?.eventHandler

    override fun onEnable() {
        val config = Configuration.load(dataFolder.path + File.separator + "config.json")
        if (!config.pluginEnable) {
            return
        }
        this.mchatg = Mchatg(
            apis, super.getLogger(),
            { HandlerList.unregisterAll(this) },
            config
        )
        mchatg?.onEnable()
        server.pluginManager.registerEvents(
            EventHandlers(), this
        )
    }


    override fun onDisable() {
        mchatg?.onDisable()
        mchatg = null

    }

    inner class EventHandlers : Listener {
        //we should retain seq
        @EventHandler
        fun onChat(event: PlayerChatEvent) {
            eventHandler?.onChat ?: return
            eventHandler?.onChat!!(event.player.name, event.message)
        }

        @EventHandler
        fun onDeath(event: PlayerDeathEvent) {
            eventHandler?.onDeath ?: return
            event.deathMessage ?: return

            eventHandler?.onDeath!!(event.entity.name, event.deathMessage!!)
        }

        @EventHandler
        fun onJoin(event: PlayerJoinEvent) {
            eventHandler?.onJoin ?: return
            eventHandler?.onJoin!!(event.player.name)
        }

        @EventHandler
        fun onQuit(event: PlayerQuitEvent) {

            eventHandler?.onQuit ?: return

            eventHandler?.onQuit!!(event.player.name)
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
                        override fun sendRawMessage(message: String) {
                            //logger.info("1")
                            callBack?.invoke(message)
                        }

                        override fun sendRawMessage(sender: UUID?, message: String) {
                            //logger.info("2")
                            callBack?.invoke(message)
                        }

                        override fun sendMessage(message: String) {
                            //logger.info("3")
                            callBack?.invoke(message)
                        }

                        override fun sendMessage(vararg messages: String?) {
                            //logger.info("4")
                            messages.forEach {
                                it?.let {
                                    callBack?.invoke(it)
                                }
                            }
                        }

                        override fun sendMessage(sender: UUID?, message: String) {
                            //logger.info("5")
                            callBack?.invoke(message)
                        }

                        override fun sendMessage(sender: UUID?, vararg messages: String?) {
                            //logger.info("6")
                            messages.forEach {
                                it?.let {
                                    callBack?.invoke(it)
                                }
                            }
                        }
                    },
                    command
                )
            }


        }

        override fun broadcastMessage(message: String) {
            Bukkit.broadcastMessage(message)
        }


    }


}