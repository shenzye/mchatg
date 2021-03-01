package com.github.mchatg.web.api

import com.github.mchatg.Context
import com.github.mchatg.account.Account
import com.github.mchatg.account.Player
import com.github.mchatg.account.Token
import com.github.mchatg.account.TokenManager
import com.github.mchatg.web.*
import com.google.gson.Gson
import java.io.InputStream

class AddPlayer(context: Context) : ApiHandler(), Context by context {


    data class Data(
        val key: String?,
        val account: String?,
        val player: String?
    )

    val gson = Gson()

    fun onCall(data: Data): InputStream {

        val token: Token? by lazy {
            TokenManager.getToken(data.key)
        }

        val account = Account(data.account)
        val hasPermission = Token.hasPermission(token, account)
        val isSuccess by lazy { accountManager.addPlayer(Pair(Account(data.account), Player(data.player))) }

        return if (hasPermission && isSuccess) {
            SuccessResponse().inputStream()
        } else {
            faileResponseInputStream()

        }
    }

    override fun onCall(data_json: String): InputStream {

        log.i(data_json)
        return try {

            onCall(gson.fromJson(data_json, Data::class.java))
        } catch (e: Exception) {
            faileResponseInputStream()
        }


    }

}