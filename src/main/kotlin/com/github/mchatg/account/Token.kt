package com.github.mchatg.account

import com.github.mchatg.Context


abstract class Token(context: Context) : Checkable, Context by context {

    abstract val account: Account
    private val superAdmin = config.account.super_admin.get()

    fun isAdmin(): Boolean {

        return account.isEquals(Account(superAdmin)) ||
                accountManager.isAdmin(account)
    }

    fun isSupperAdmin(): Boolean = account.isEquals(Account(superAdmin))

    fun hasPermission(account: Account): Boolean {
        if (isInvalid())
            return false

        return (this.account.isEquals(account)) ||
                isAdmin()
    }

    fun lackPermission(account: Account): Boolean = !hasPermission(account)

    fun hasPermission(player: Player): Boolean {
        val account by lazy { accountManager.getAccount(player) }

        return hasPermission(account)
    }

    fun lackPermission(player: Player): Boolean = !hasPermission(player)


    class PasswdToken(context: Context, override val account: Account, val passwd: Passwd) : Token(context) {
        override fun isValid(): Boolean {
            return accountManager.verifyPasswd(account, passwd)

        }
    }

    class TelegramToken(context: Context, val uid: TelegramUID) : Token(context) {

        override val account: Account = accountManager.getAccount(uid)
        override fun isValid(): Boolean {

            return account.isValid() && (account.isEquals(accountManager.getAccount(uid)))
        }
    }


    companion object {


        fun isAdmin(token: Token?): Boolean {
            return (token != null && token.isAdmin())
        }

        fun isSupperAdmin(token: Token?): Boolean {
            return (token != null && token.isSupperAdmin())
        }

        fun hasPermission(token: Token?, account: Account): Boolean {
            return (token != null && token.hasPermission(account))
        }

        fun lackPermission(token: Token?, account: Account): Boolean = !hasPermission(token, account)

        fun hasPermission(token: Token?, player: Player): Boolean {
            return (token != null && token.hasPermission(player))
        }

        fun lackPermission(token: Token?, player: Player): Boolean = !hasPermission(token, player)

    }


}