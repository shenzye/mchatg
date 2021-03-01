package com.github.mchatg.web.api

import com.github.mchatg.Context
import com.github.mchatg.account.Account
import com.github.mchatg.account.Token
import com.github.mchatg.account.TokenManager
import com.github.mchatg.web.ApiHandler
import com.github.mchatg.web.SuccessResponse
import com.github.mchatg.web.faileResponseInputStream
import com.google.gson.Gson
import java.io.InputStream

class SetAdmin(context: Context) : ApiHandler(), Context by context {


    data class Data(
        val key: String?,
        val keyPasswd: String?,
        val account: String?,
        val isAdmin: Boolean?
    )

    val gson = Gson()

    fun onCall(data: Data): InputStream {
        val token: Token? by lazy {
            TokenManager.getToken(data.key)
        }

        val hasPermission = Token.isSupperAdmin(token)

        val account = Account(data.account)
        val isAdmin = data.isAdmin

        val isSuccess = isAdmin != null &&
                hasPermission &&
                account.isValid() &&
                accountManager.setAdmin(Pair(account, isAdmin))


        return if (isSuccess) {
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