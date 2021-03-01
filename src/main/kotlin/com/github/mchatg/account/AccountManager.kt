package com.github.mchatg.account

import com.github.mchatg.Context
import com.github.mchatg.account.command.telegram.Bind
import com.github.mchatg.account.command.telegram.Kickme
import com.github.mchatg.account.command.telegram.Start
import com.github.mchatg.account.command.telegram.Unbind
import com.github.mchatg.telegram.registerTelegramCommand
import com.github.mchatg.until.TimeMeter
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import java.util.*


class AccountManager(context: Context) : DatabaseAccessor(context), Listener {

    private val unlocked by lazy { TimeMeter<String>(Calendar.MINUTE, 2) }
    fun unlock(token: Token?, player: Player): Boolean {


        if (Token.lackPermission(token, getAccount(player))) {
            return false
        }

        unlocked.push(player.value!!)
        return true
    }


    fun init() {


        plugin.server.pluginManager.registerEvents(this, plugin)

        registerTelegramCommand(Bind(this))
        registerTelegramCommand(Start(this))
        registerTelegramCommand(Kickme(this))
        registerTelegramCommand(Unbind(this))

    }


    @EventHandler(priority = EventPriority.LOWEST)
    fun onLogin(event: PlayerLoginEvent) {

        //TODO：test
        when {
            !config.account.enable.get() -> {
                return
            }
            isPlayerRegistered(Player(event.player.name)) -> {
                if (unlocked.isTimeOut(event.player.name)) {
                    println("onLogin")
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "请解锁后登陆")
                }
            }
            config.account.allow_tourist.get() -> {
                return
            }
            else -> {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "请先注册帐号")
            }
        }

    }


}



