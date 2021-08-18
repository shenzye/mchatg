package com.github.mchatg

import com.github.mchatg.account.AccountManager
import com.github.mchatg.account.Token
import com.github.mchatg.account.init_database
import com.github.mchatg.configs.Configuration
import com.github.mchatg.message.MessageManager
import com.github.mchatg.telegram.TelegramBot
import com.github.mchatg.web.WebService
import java.io.File


class Mchatg(
    override val pluginFolder: String,
    override val eventHandler: EventHandler,
    override val serverApis: ServerApis
) : Context {

    override val config: Configuration by lazy { Configuration.load(pluginFolder + File.separator + "config.json") }
    override val telegramBot: TelegramBot by lazy { TelegramBot(this) }
    override val messageManager: MessageManager by lazy { MessageManager(this) }
    override val accountManager: AccountManager by lazy { AccountManager(this) }
    override val webService: WebService by lazy {
        //创建目录
        File(pluginFolder + File.separator + "files").mkdir()
        WebService(this)
    }

    init {
        LogMe.init(pluginFolder + File.separator + "logs", info = true, debug = true)
        Token.setAdmins(config.account.admins)
    }

    fun onEnable() {


//        config.init(configAccessorWrapper(plugin.config))

        if (!config.pluginEnable)
            return


        init_database(this)


        telegramBot.init()
        webService.init()

        if (config.message.enable) { //, false
            messageManager.init()
        }

        if (config.account.enable) {
            accountManager.init()
        }


    }

    fun onDisable() {

    }


}