package com.github.mchatg

import com.github.mchatg.telegram.TelegramBot
import java.util.logging.Logger

interface Context {

    val config: Configuration
    val telegramBot: TelegramBot
    val eventHandler: EventHandler
    val serverApis: ServerApis
    val logger: Logger
    val unregisterEventHandler: (() -> Unit)?
}


class EventHandler {
    var onChat: ((player: String, message: String) -> Unit)? = null
    var onJoin: ((player: String) -> Unit)? = null
    var onQuit: ((player: String) -> Unit)? = null
    var onDeath: ((player: String, message: String) -> Unit)? = null
    //val onServerLoad
}


interface ServerApis {
    fun getOnlinePlayers(): List<String>
    fun kickPlayer(players: Iterable<String>)
    fun dispatchCommand(command: String, callBack: ((message: String) -> Unit)?)

    fun broadcastMessage(message: String)
}