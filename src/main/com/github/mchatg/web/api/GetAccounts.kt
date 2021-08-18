package com.github.mchatg.web.api


import com.github.mchatg.account.*
import com.github.mchatg.web.ApiHandler
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URI

class GetAccounts : ApiHandler<Unit>(null) {

    data class AccountInfo(
        val account: String,
        val telegram: String?,
        val isAdmin: Boolean
    )

    override fun hasPermission(token: Token?, data: Unit?): Boolean {
        return Token.isAdmin(token)
    }


    override fun onCall(data: Unit?, uri: URI): ArrayList<AccountInfo>? {
        val accounts: ArrayList<AccountInfo> = ArrayList()
        transaction {
            for (it in TAccounts.selectAll()) {
                accounts.add(
                    AccountInfo(
                        it[TAccounts.account],
                        if (it[TAccounts.telegramUID] != null) {
                            it[TAccounts.telegramUID].toString()
                        } else {
                            null
                        },
                        Account(it[TAccounts.account]).isAdmin()
                    )
                )
            }
        }
        return if (accounts.size > 0) {
            accounts
        } else {
            null
        }

    }


}