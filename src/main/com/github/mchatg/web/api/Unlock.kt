package com.github.mchatg.web.api

import com.github.mchatg.account.Player
import com.github.mchatg.account.Token
import com.github.mchatg.web.ApiHandler
import java.net.URI
import java.util.*

class Unlock : ApiHandler<Unlock.Data>(Data::class.java) {

    data class Data(
        val player: String?,
        //TODO
//        val expires: String//Long
    )


    override fun hasPermission(token: Token?, data: Data?): Boolean {
        return Token.hasPermission(token, Player(data?.player))
    }


    override fun onCall(data: Data?, uri: URI): Boolean {
        if (Player(data?.player).isInvalid()) {
            return false
        }

//        val expires: Long = data?.expires?.toLongOrNull() ?: return false
        val expires = Calendar.getInstance().also { it.add(Calendar.MINUTE, 2) }.timeInMillis
        return Player(data?.player).unlock(expires)

    }

}