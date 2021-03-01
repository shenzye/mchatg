package com.github.mchatg.web.api

import com.github.mchatg.Context
import com.github.mchatg.account.Player
import com.github.mchatg.account.TokenManager
import com.github.mchatg.web.*
import com.google.gson.Gson
import java.io.InputStream

class Unlock(context: Context) : ApiHandler(), Context by context {

    data class Data(
        val key: String?,
        val player: String?
    )

    val gson = Gson()

    fun onCall(data: Data): InputStream {

        val token = TokenManager.getToken(data.key)
        return if (accountManager.unlock(token, Player(data.player)))
            SuccessResponse().inputStream()
        else
            faileResponseInputStream()

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