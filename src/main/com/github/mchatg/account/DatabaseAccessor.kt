package com.github.mchatg.account

import com.github.mchatg.Context
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File


object TPlayers : Table() {
    val name = text("name")//注意不区分大小写
    val owner = reference("owner", TAccounts.account)//注意不区分大小写
    val expires = long("expires").default(0)
    val lastPlayed = long("last_played").nullable()
    val playTime = long("play_time").nullable()

    override val primaryKey = PrimaryKey(name)
}

object TAccounts : Table() {
    val account = text("account")//注意不区分大小写
    val passwd = text("passwd")
    val telegramUID = long("telegramUID").nullable()

    override val primaryKey = PrimaryKey(account)
}


fun init_database(context: Context) {
    Database.connect("jdbc:sqlite:" + context.pluginFolder + File.separator + "users.db", "org.sqlite.JDBC")

    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(TPlayers, TAccounts)
    }
}


object DatabaseAccessor {


    //TODO:参数检查！！！！！！！！！！！！！！！！！！


    //无依赖>>
    fun getPlayers(account: Account): ArrayList<String> {
        val players: ArrayList<String> = ArrayList()
        if (account.isInvalid()) {
            return players
        }
        transaction {
            TPlayers.select {
                TPlayers.owner eq account.value!!
            }.forEach {
                players.add(it[TPlayers.name])
            }
        }
        return players
    }


    fun getAccount(player: Player): Account {
        var account_rv: String? = null
        if (player.isInvalid()) {
            return Account(account_rv)
        }

        transaction {
            val account_query = TPlayers.select {
                TPlayers.name eq player.value!!
            }
            for (account in account_query) {
                account_rv = account[TPlayers.owner]
                break
            }
        }
        return Account(account_rv)
    }


    data class AccountInfo(
        val account: Account,
        val passwd: Passwd,
        val telegramUID: TelegramUID
    )


    fun getAccountInfo(account: Account): AccountInfo? {
        var accountInfo: AccountInfo? = null
        if (account.isInvalid()) {
            return accountInfo
        }

        transaction {
            for (it in TAccounts.select { TAccounts.account eq account.value!! }) {
                accountInfo = AccountInfo(
                    account,
                    Passwd(it[TAccounts.passwd]),
                    TelegramUID(it[TAccounts.telegramUID])
                )
                break
            }
        }

        return accountInfo


    }


    fun getAccountInfo(uid: TelegramUID): AccountInfo? {
        var accountInfo: AccountInfo? = null
        if (uid.isInvalid()) {
            return accountInfo
        }

        transaction {
            for (it in TAccounts.select { TAccounts.telegramUID eq uid.value!! }) {
                accountInfo = AccountInfo(
                    Account(it[TAccounts.account]),
                    Passwd(it[TAccounts.passwd]),
                    uid
                )
                break
            }
        }
        return accountInfo
    }


    //无依赖<<
    //无依赖方法封装>>
    fun isAccountRegistered(account: Account): Boolean = (getAccountInfo(account) != null)
    fun isPlayerRegistered(player: Player): Boolean = (getAccount(player).isValid())


    fun getAccount(uid: TelegramUID): Account {
        if (uid.isInvalid())
            return Account(null)
        val accountInfo = getAccountInfo(uid)
//        if (accountInfo == null)
//            return Account(null)
        return Account(accountInfo?.account?.value)

    }

    fun getTelegramUID(account: Account): TelegramUID {
        val accountInfo = getAccountInfo(account)

        return TelegramUID(accountInfo?.telegramUID?.value)
    }


    //无依赖方法封装<<


    fun setPasswd(account: Account, passwd: Passwd): Boolean {

        if (Checkable.hasInvalid(account, passwd) ||
            !isAccountRegistered(account)
        )
            return false

        var isSuccess = false
        transaction {
            isSuccess = TAccounts.update({
                TAccounts.account eq account.value!!
            }) {
                it[TAccounts.passwd] = passwd.value!!
            } > 0


        }

        return isSuccess
    }

    fun setTelegram(account: Account, uid: TelegramUID): Boolean {
        if (!isAccountRegistered(account) || uid.isInvalid())
            return false
        var isSuccess = false
        transaction {
            isSuccess = TAccounts.update({
                TAccounts.account eq account.value!!
            }) {
                it[TAccounts.telegramUID] = uid.value!!
            } > 0


        }
        return isSuccess

    }


}

