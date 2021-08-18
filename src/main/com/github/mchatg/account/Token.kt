package com.github.mchatg.account

import com.fasterxml.jackson.annotation.JsonAutoDetect


abstract class Token : Checkable {


    abstract val account: Account


    @JsonAutoDetect(
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
    )
    class PasswdToken(override val account: Account, val passwd: Passwd) : Token() {
        override fun isValid(): Boolean {
            return account.verifyPasswd(passwd)

        }
    }

    class TelegramToken(private val uid: TelegramUID) : Token() {

        override val account: Account = DatabaseAccessor.getAccount(uid)
        override fun isValid(): Boolean {
            return account.isValid() && (account.isEquals(DatabaseAccessor.getAccount(uid)))
        }
    }


    companion object {

        val admins: ArrayList<String> = ArrayList()
        fun setAdmins(adminsList: ArrayList<String>) {
            admins.clear()
            for (admin in adminsList) {
                if (Account(admin).isValid()) {
                    admins.add(admin)
                }
            }
        }


        //TODO:better:两次数据库查询
        fun hasPermission(token: Token?, account: Account): Boolean {
            return Checkable.allValid(token, account) &&
                    (token!!.account.isEquals(account) ||
                            token.account.isAdmin())
        }

        fun lackPermission(token: Token?, account: Account): Boolean = !hasPermission(token, account)


        //TODO:better:三次数据库查询
        fun hasPermission(token: Token?, player: Player): Boolean {
            return (hasPermission(token, DatabaseAccessor.getAccount(player)))
        }

        fun lackPermission(token: Token?, player: Player): Boolean = !hasPermission(token, player)

        fun isAdmin(token: Token?): Boolean {

            return token != null && token.account.isAdmin()

        }


    }


}