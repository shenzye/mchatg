package com.github.mchatg.web.api

import com.github.mchatg.Context
import com.github.mchatg.account.Account
import com.github.mchatg.web.*
import com.google.gson.Gson
import java.io.InputStream

class GetPlayers(context: Context) : ApiHandler(), Context by context {


    data class Data(
        val key: String?,
        val account: String?
    )

    val gson = Gson()

    fun onCall(data: Data): InputStream {

        val players by lazy { accountManager.getPlayers(Account(data.account)) }
        if (data.account.isNullOrBlank() ||
            players.isNullOrEmpty()
        )
            return faileResponseInputStream()


        return Response(players.toTypedArray()).inputStream()

    }

    override fun onCall(data_json: String): InputStream {


        log.i(data_json)

        return try {
            onCall(gson.fromJson(data_json, Data::class.java))
        } catch (e: Exception) {
            faileResponseInputStream()
        }


    }

    private data class Response(
        val players: Array<String>? = null
    ) : SuccessResponse()

}