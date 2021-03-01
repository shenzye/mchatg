package com.github.mchatg

import com.github.mchatg.account.AccountManager
import com.github.mchatg.config.ConfigAccessorWapper
import com.github.mchatg.config.Configuration
import com.github.mchatg.telegram.TelegramBot
import com.github.mchatg.message.MessageManager
import com.github.mchatg.web.WebService
import org.bukkit.plugin.java.JavaPlugin





class Mchatg : JavaPlugin(), Context {

    override val plugin: JavaPlugin = this
    override val telegramBot: TelegramBot by lazy { TelegramBot(this) }
    override val messageManager: MessageManager by lazy { MessageManager(this) }
    override val accountManager: AccountManager by lazy { AccountManager(this) }
    override val webService: WebService by lazy { WebService(this) }
    override val config: Configuration by lazy { Configuration(ConfigAccessorWapper(getConfig())) }
    override val log: LogMe by lazy { LogMe(this) }



    init {
        saveDefaultConfig()
    }

    override fun onEnable() {








        if (!config.global.enable.get())
            return

        telegramBot.init()
        webService.init()

        if (config.message.enable.get()) { //, false
            messageManager.init()
        }

        if (config.account.enable.get()) {
            accountManager.init()
        }


    }

    override fun onDisable() {

    }


}