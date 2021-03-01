package com.github.mchatg.web.api

import com.github.mchatg.Context
import com.github.mchatg.account.Player
import com.github.mchatg.account.Token
import com.github.mchatg.account.TokenManager
import com.github.mchatg.web.ApiHandler
import com.github.mchatg.web.SuccessResponse
import com.github.mchatg.web.faileResponseInputStream
import com.google.gson.Gson
import java.io.InputStream

class RemovePlayer(context: Context) : ApiHandler(), Context by context {


    data class Data(
        val key: String?,
        val player: String?
    )

    val gson = Gson()

    fun onCall(data: Data): InputStream {

        val token = TokenManager.getToken(data.key)


        val lackPermission = Token.lackPermission(token, Player(data.player))
        val isFail by lazy { !accountManager.removePlayer(Player(data.player)) }
        if (lackPermission || isFail)
            return faileResponseInputStream()



        return SuccessResponse().inputStream()


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