package com.github.mchatg.web.api

import com.github.mchatg.LogMe
import com.github.mchatg.account.Account
import com.github.mchatg.account.DatabaseAccessor
import com.github.mchatg.account.Token
import com.github.mchatg.web.*
import java.net.URI

class GetPlayers : ApiHandler<GetPlayers.Data>(Data::class.java) {
    data class Data(
        val account: String?
    )

    override fun hasPermission(token: Token?, data: Data?): Boolean {
        LogMe.d("token:" + (token == null))
        return Token.hasPermission(token, Account(data?.account))
    }

    override fun onCall(data: Data?, uri: URI): ArrayList<String>? {
        return DatabaseAccessor.getPlayers(Account(data?.account)).let {
            if (it.isEmpty()) {
                null
            } else {
                it
            }

        }


    }


}