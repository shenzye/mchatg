package com.github.mchatg.web.api

import com.github.mchatg.account.*
import com.github.mchatg.web.ApiHandler
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URI

class AddPlayer : ApiHandler<AddPlayer.Data>(Data::class.java) {

    data class Data(
        val account: String?,
        val player: String?
    )

    override fun hasPermission(token: Token?, data: Data?): Boolean {
        return Token.hasPermission(token, Account(data?.account))
    }

    override fun onCall(data: Data?, uri: URI): Boolean {
        val account = Account(data?.account)
        val player = Player(data?.player)

        if (Checkable.hasInvalid(account, player) ||
            DatabaseAccessor.isPlayerRegistered(player)
        )
            return false


        var isSuccess = false
        transaction {
            isSuccess = TPlayers.insert {
                it[owner] = account.value!!
                it[name] = player.value!!

            }.resultedValues?.size ?: 0 > 0


        }

        return isSuccess

    }


}