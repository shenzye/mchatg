package com.github.mchatg.web.api

import com.github.mchatg.account.*
import com.github.mchatg.web.ApiHandler
import java.net.URI

class UpdatePasswd : ApiHandler<UpdatePasswd.Data>(Data::class.java) {
    data class Data(
        val tokenPasswd: String?,
        val account: String?,
        val passwd: String?
    )


    override fun hasPermission(token: Token?, data: Data?): Boolean {

        "str".isNotBlank()


        return Token.hasPermission(token, Account(data?.account)) &&
                token!!.account.verifyPasswd(Passwd(data?.tokenPasswd))
    }


    override fun onCall(data: Data?, uri: URI): Boolean {
        val account = Account(data?.account)
        val passwd = Passwd(data?.passwd)

        DatabaseAccessor.setPasswd(account, passwd)

        return DatabaseAccessor.setPasswd(account, passwd)
    }


}