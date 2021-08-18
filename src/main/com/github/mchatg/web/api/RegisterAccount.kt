package com.github.mchatg.web.api

import com.github.mchatg.LogMe
import com.github.mchatg.account.*
import com.github.mchatg.configs.Configuration
import com.github.mchatg.web.ApiHandler
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URI

class RegisterAccount(val config: Configuration) : ApiHandler<RegisterAccount.Data>(Data::class.java) {

    data class Data(
        val account: String?,
        val passwd: String?,
        val invitecode: String?
    )

    override fun hasPermission(token: Token?, data: Data?): Boolean {
        val code = config.account.inviteCode
        return code.isBlank() || code == data?.invitecode
    }

    override fun onCall(data: Data?, uri: URI): Boolean {
        LogMe.d("tryRegisterAccount")
        val account = Account(data?.account)
        val passwd = Passwd(data?.passwd)
        if (Checkable.hasInvalid(account, passwd) ||
            DatabaseAccessor.isAccountRegistered(account)
        ) {
            LogMe.d("hasInvalid:" + Checkable.hasInvalid(account, passwd))

            LogMe.d("account:" + data?.account + ",passwd:" + data?.passwd)

            LogMe.d("isAccountRegistered:" + DatabaseAccessor.isAccountRegistered(account))
            return false
        }


        var isSuccess = false
        transaction {
            isSuccess = TAccounts.insert {
                it[TAccounts.account] = account.value!!
                it[TAccounts.passwd] = passwd.value!!

            }.resultedValues?.size ?: 0 > 0
        }
        LogMe.d("isSuccess:$isSuccess")
        return isSuccess

    }


}