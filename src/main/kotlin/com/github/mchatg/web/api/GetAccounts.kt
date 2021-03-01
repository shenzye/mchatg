package com.github.mchatg.web.api

import com.github.mchatg.Context
import com.github.mchatg.account.DatabaseAccessor
import com.github.mchatg.account.Token
import com.github.mchatg.account.TokenManager
import com.github.mchatg.web.ApiHandler
import com.github.mchatg.web.SuccessResponse
import com.github.mchatg.web.faileResponseInputStream
import com.google.gson.Gson
import java.io.InputStream

class GetAccounts(context: Context) : ApiHandler(), Context by context {


    data class Data(
        val key: String?
    )

    val gson = Gson()

    fun onCall(data: Data): InputStream {

        val token = TokenManager.getToken(data.key)

        return if (Token.isAdmin(token)) {
            Response(accountManager.getAccounts().toTypedArray()).inputStream()
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

    private data class Response(
        val accounts: Array<DatabaseAccessor.AccountInfo>? = null
    ) : SuccessResponse()

}