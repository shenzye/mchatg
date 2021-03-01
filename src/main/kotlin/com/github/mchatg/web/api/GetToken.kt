package com.github.mchatg.web.api

import com.github.mchatg.Context
import com.github.mchatg.account.*
import com.github.mchatg.web.*
import com.google.gson.Gson
import java.io.InputStream

class GetToken(context: Context) : ApiHandler(), Context by context {

    data class Data(
        val account: String?,
        val passwd: String?
    )

    val gson = Gson()
    fun onCall(data: Data): InputStream {

        val key = TokenManager.getKey(this, Account(data.account), Passwd(data.passwd))

        if (key.isNullOrBlank())
            return faileResponseInputStream()



        log.d("key:$key")

        val token = TokenManager.getToken(key)
        return Response(
            key,
            Token.isAdmin(token),
            Token.isSupperAdmin(token)
        ).inputStream()


    }

    override fun onCall(data_json: String): InputStream {


        log.i(data_json)
        return try {
            onCall(gson.fromJson(data_json, Data::class.java))
        } catch (e: Exception) {
            faileResponseInputStream()
        }


    }


    private class Response(
        val key: String?,
        val isAdmin: Boolean?,
        val isSuperAdmin: Boolean?

    ) : SuccessResponse()


}