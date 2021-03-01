package com.github.mchatg.web.api

import com.github.mchatg.Context
import com.github.mchatg.account.*
import com.github.mchatg.web.ApiHandler
import com.github.mchatg.web.SuccessResponse
import com.github.mchatg.web.faileResponseInputStream
import com.google.gson.Gson
import java.io.InputStream

class UpdatePasswd(context: Context) : ApiHandler(), Context by context {

    data class Data(
        val key: String?,
        val keyPasswd: String?,
        val account: String?,
        val passwd: String?
    )

    val gson = Gson()


    fun onCall(data: Data): InputStream {
        val token = TokenManager.getToken(data.key)
        val keyPasswd = Passwd(data.keyPasswd)
        val account = Account(data.account)
        val passwd = Passwd(data.passwd)

        return when {
            Token.lackPermission(token, account) -> faileResponseInputStream()
            !accountManager.verifyPasswd(token?.account ?: Account(null), keyPasswd) -> faileResponseInputStream()
            accountManager.setPasswd(Pair(account, passwd)) -> SuccessResponse().inputStream()
            else -> faileResponseInputStream()

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