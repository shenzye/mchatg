package com.github.mchatg.web.api

import com.github.mchatg.account.Account
import com.github.mchatg.account.Passwd
import com.github.mchatg.account.Token
import com.github.mchatg.account.TokenManager
import com.github.mchatg.web.ApiHandler
import java.net.URI

class GetToken : ApiHandler<GetToken.Data>(Data::class.java) {

//    override val gson: Gson = GsonBuilder().disableHtmlEscaping().create()

    data class Data(
        val account: String?,
        val passwd: String?
    )

    data class Response(
        val token: String?,
        val isAdmin: Boolean,
    )

    override fun hasPermission(token: Token?, data: Data?): Boolean = true

    override fun onCall(data: Data?, uri: URI): Any? {
        val token = TokenManager.getToken(Account(data?.account), Passwd(data?.passwd))
        return if (token != null) {
            Response(
                TokenManager.encodeToken(token),
                Token.isAdmin(token)
            )
        } else {
            null
        }


    }


}