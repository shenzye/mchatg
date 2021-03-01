package com.github.mchatg.web.api

import com.github.mchatg.Context
import com.github.mchatg.account.Account
import com.github.mchatg.account.Passwd
import com.github.mchatg.web.*
import com.google.gson.Gson
import java.io.InputStream

class RegisterAccount(context: Context) : ApiHandler(), Context by context {

    data class Data(
        val account: String?,
        val passwd: String?,
        val invitecode: String?
    )

    val gson = Gson()
    fun onCall(data: Data): InputStream {

        val code = config.account.invitecode.get()


        val isFail by lazy { !accountManager.addAccount(Pair(Account(data.account), Passwd(data.passwd))) }

        if ((code.isNotBlank() && code != data.invitecode) ||
            isFail
        )
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