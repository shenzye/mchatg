package com.github.mchatg

import com.github.mchatg.account.AccountManager
import com.github.mchatg.config.Configuration
import com.github.mchatg.message.MessageManager
import com.github.mchatg.telegram.TelegramBot
import com.github.mchatg.web.WebService
import org.bukkit.plugin.java.JavaPlugin

interface Context {
    val plugin: JavaPlugin
    val telegramBot: TelegramBot
    val webService: WebService
    val messageManager: MessageManager
    val accountManager: AccountManager
    val config: Configuration
    val log:LogMe
}

//open class ContextWrapper(val context: Context) : Context {
//    override val plugin by lazy { context.plugin }
//    override val telegramBot by lazy { context.telegramBot }
//    override val database by lazy { context.database }
//    override val forwardManager by lazy { context.forwardManager }
//    override val loginManager by lazy { context.loginManager }
//    override val config by lazy { context.config }
//}