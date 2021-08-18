package com.github.mchatg.account

import com.github.mchatg.Context
import com.github.mchatg.LoginControl
import com.github.mchatg.account.DatabaseAccessor.isPlayerRegistered
import com.github.mchatg.account.command.telegram.Bind
import com.github.mchatg.account.command.telegram.KickMe
import com.github.mchatg.account.command.telegram.Start
import com.github.mchatg.account.command.telegram.Unbind
import com.github.mchatg.telegram.registerTelegramCommand


class AccountManager(context: Context) : Context by context {

    fun init() {


        eventHandler.onLogin = this::onLogin

        registerTelegramCommand(Bind(this))
        registerTelegramCommand(Start(this))
        registerTelegramCommand(KickMe(this))
        registerTelegramCommand(Unbind(this))

    }


    private fun onLogin(player: String): LoginControl {

        //TODO：test
        return when {
            !config.account.enable -> {
                LoginControl.Allow
            }
            !Player(player).isLocked() -> {
                LoginControl.Allow
            }
            isPlayerRegistered(Player(player)) -> {
                //已注册
                LoginControl.Disallow("请解锁后登陆")
            }
            config.account.allowTourist -> {
                //未注册且允许游客
                LoginControl.Allow
            }
            else -> {
                LoginControl.Disallow("请先注册帐号")
//                LogMe.i("$player 尝试登录失败：已锁定")
            }
        }

    }


}



