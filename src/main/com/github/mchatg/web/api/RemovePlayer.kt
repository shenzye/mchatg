package com.github.mchatg.web.api

import com.github.mchatg.account.DatabaseAccessor
import com.github.mchatg.account.Player
import com.github.mchatg.account.TPlayers
import com.github.mchatg.account.Token
import com.github.mchatg.web.ApiHandler
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URI

class RemovePlayer : ApiHandler<RemovePlayer.Data>(Data::class.java) {


    data class Data(
        val player: String?
    )


    override fun hasPermission(token: Token?, data: Data?): Boolean {
        return Token.hasPermission(token, Player(data?.player))
    }


    override fun onCall(data: Data?, uri: URI): Boolean {
        if (Player(data?.player).isInvalid() ||
            !DatabaseAccessor.isPlayerRegistered(Player(data?.player))
        )
            return false
        var isSuccess = false
        transaction {
            //TODO:test
            isSuccess = TPlayers.deleteWhere {
                TPlayers.name eq data!!.player!!
            } > 0
        }
        return isSuccess
    }


}