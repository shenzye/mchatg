package com.github.mchatg

import com.github.mchatg.account.AccountManager
import com.github.mchatg.configs.Configuration
import com.github.mchatg.message.MessageManager
import com.github.mchatg.telegram.TelegramBot
import com.github.mchatg.web.WebService

interface Context {
//    Gson().fromJson(File("config.yml").readText(), Configuration::class.java)

    //    val plugin: JavaPlugin
    val pluginFolder: String
    val config: Configuration
    val telegramBot: TelegramBot
    val webService: WebService
    val messageManager: MessageManager
    val accountManager: AccountManager

    val eventHandler: EventHandler
    val serverApis: ServerApis
}


class EventHandler {
    var onLogin: ((player: String) -> LoginControl)? = null
    var onChat: ((player: String, message: String) -> Unit)? = null
    var onJoin: ((player: String) -> Unit)? = null
    var onQuit: ((player: String) -> Unit)? = null
    var onDeath: ((player: String, message: String) -> Unit)? = null
    //    val onServerLoad
}


sealed class LoginControl {
    object Allow : LoginControl()
    data class Disallow(val reason: String) : LoginControl()
}


interface ServerApis {
    fun getOnlinePlayers(): List<String>

    fun kickPlayer(players: Iterable<String>)

    fun dispatchCommand(command: String, callBack: ((message: String) -> Unit)?)
}