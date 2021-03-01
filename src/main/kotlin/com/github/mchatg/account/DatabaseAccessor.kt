package com.github.mchatg.account

import com.github.mchatg.Context
import java.io.File
import java.lang.Exception
import java.sql.*
import java.util.*
import kotlin.collections.ArrayList


@Suppress("UnnecessaryVariable")
abstract class DatabaseAccessor(context: Context) : Context by context {
    private val connection: Connection by lazy {
        DriverManager.getConnection("jdbc:sqlite:" + plugin.dataFolder + File.separator + "users.db").also {
            with(it.createStatement()) {
                executeUpdate(
                    "create table if not exists players(" +
                            "player text primary key collate nocase," +
                            "account text" +
                            ")"
                )
                executeUpdate(
                    "create table if not exists accounts(" +
                            "account text primary key collate nocase," +
                            "passwd text not null," +
                            "isAdmin boolean default false," +
                            "telegramUID integer" +
                            ")"
                )
                close()
            }

        }
    }

    val closeable = ArrayList<() -> Unit>()

    //TODO:参数检查！！！！！！！！！！！！！！！！！！


    //无依赖>>
    val getPlayers by lazy {
        Accessor(
            "select player from players where account=?"
        ) { statement: PreparedStatement, account: Account ->

            val players: ArrayList<String> = ArrayList()
            if (account.isInvalid())
                return@Accessor players
            with(statement) {
                setString(1, account.value)
                val result = executeQuery()
                while (result.next()) {
                    players.add(result.getString(1))
                }
                result.close()
                clearParameters()
                return@Accessor players
            }


        }.func
    }


    val getAccount by lazy {
        Accessor(
            "select account from players where player=?"
        ) { statement: PreparedStatement, player: Player ->

            if (player.isInvalid())
                return@Accessor Account(null)

            with(statement) {
                setString(1, player.value)
                val result = executeQuery()
                val account = if (
                    result.next()
                )
                    result.getString(1)
                else
                    null
                result.close()
                clearParameters()

                return@Accessor Account(account)
            }


        }.func
    }

    data class AccountInfo(
        val account: Account,
        val passwd: Passwd,
        val isAdmin: Boolean,
        val telegramUID: TelegramUID
    )

    fun getAccountInfo(account: Account) = getAccountInfoByAccount(account)
    private val getAccountInfoByAccount by lazy {
        Accessor(
            "select account,passwd,isAdmin,telegramUID from accounts where account=?"
        ) { statement: PreparedStatement, account: Account ->

            if (account.isInvalid()) {
                return@Accessor null
            }
            return@Accessor queryAccountInfo(statement, account.value!!)
        }.func
    }


    private fun queryAccountInfo(statement: PreparedStatement, arg: Any): AccountInfo? {
        statement.setObject(1, arg)
        val result = statement.executeQuery()
        if (result.next()) {
            val account = result.getString(1)
            val passwd = result.getString(2)
            val isAdmin = result.getBoolean(3)
            val telegramUID = result.getInt(4)
            result.close()
            statement.clearParameters()
            return AccountInfo(Account(account), Passwd(passwd), isAdmin ?: false, TelegramUID(telegramUID))
        }

        result.close()
        statement.clearParameters()
        return null

    }

    fun getAccountInfo(uid: TelegramUID) = getAccountInfoById(uid)
    private val getAccountInfoById by lazy {
        Accessor(
            "select account,passwd,isAdmin,telegramUID from accounts where telegramUID=?"
        ) { statement: PreparedStatement, uid: TelegramUID ->

            if (uid.isInvalid())
                return@Accessor null

            return@Accessor queryAccountInfo(statement, uid.value!!)
        }.func
    }


    fun getAccounts() = getAccounts(Unit)
    private val getAccounts by lazy {
        Accessor(
            "select account,isAdmin,telegramUID from accounts"
        ) { statement: PreparedStatement, _: Unit ->

            val accounts = ArrayList<AccountInfo>()
            with(statement) {
                val result = executeQuery()
                while (result.next()) {
                    val account = Account(result.getString(1))
                    val passwd = Passwd(null)
                    val isAdmin = result.getBoolean(2) ?: false
                    val telegramUID = TelegramUID(result.getInt(3))

                    accounts.add(AccountInfo(account, passwd, isAdmin, telegramUID))
                }
                result.close()
                clearParameters()
                return@Accessor accounts
            }
        }.func
    }


    //无依赖<<
    //无依赖方法封装>>


    fun isAccountRegistered(account: Account): Boolean = (getAccountInfo(account) != null)
    fun isPlayerRegistered(player: Player): Boolean = (getAccount(player).isValid())
    fun verifyPasswd(account: Account, passwd: Passwd): Boolean {
        if (Checkable.hasInvalid(account, passwd))
            return false
        val accountInfo = getAccountInfo(account)
        return accountInfo != null && accountInfo.passwd.isEquals(passwd)
    }

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


     fun isAdmin(account: Account): Boolean {
        if (account.isInvalid())
            return false

        val accountInfo = getAccountInfo(account)
        return (accountInfo != null) && accountInfo.isAdmin
    }


    //无依赖方法封装<<
    val addAccount by lazy {
        Accessor(
            "insert into accounts(account,passwd) values(?,?)"
        ) { statement: PreparedStatement, (account, passwd): Pair<Account, Passwd> ->


            if (Checkable.hasInvalid(account, passwd) ||
                isAccountRegistered(account)
            )
                return@Accessor false
            with(statement) {
                setString(1, account.value)
                setString(2, passwd.value)

                return@Accessor (executeUpdate() > 0).also {
                    clearParameters()
                }
            }


        }.func
    }


    val addPlayer by lazy {
        Accessor(
            "insert into players(account,player) values(?,?)"
        ) { statement: PreparedStatement, (account, player): Pair<Account, Player> ->
            if (Checkable.hasInvalid(account, player) ||
                isPlayerRegistered(player)
            )
                return@Accessor false

            with(statement) {
                setString(1, account.value)
                setString(2, player.value)

                return@Accessor (executeUpdate() > 0).also {
                    clearParameters()
                }
            }


        }.func
    }

    val removePlayer by lazy {
        Accessor(
            "delete from players where player=?"
        ) { statement: PreparedStatement, player: Player ->
            if (player.isInvalid() ||
                !isPlayerRegistered(player)
            )
                return@Accessor false

            with(statement) {
                setString(1, player.value)

                return@Accessor (executeUpdate() > 0).also {
                    clearParameters()
                }
            }


        }.func
    }


    val setPasswd by lazy {
        Accessor(
            "update accounts set passwd=? where account=?"
        ) { statement: PreparedStatement, (account, passwd): Pair<Account, Passwd> ->

            if (Checkable.hasInvalid(account, passwd) ||
                !isAccountRegistered(account)
            )
                return@Accessor false

            return@Accessor updateAccountInfo(statement, account.value!!, passwd.value!!)
        }.func
    }

    //TODO
    private fun updateAccountInfo(statement: PreparedStatement, account: String, value: Any?): Boolean {

        with(statement) {
            //account传参在前，但是查询语句的字段位置靠后，需要调整
            setString(2, account)
            setObject(1, value)
            return (executeUpdate() > 0).also {
                clearParameters()
            }


        }


    }


    val setTelegram by lazy {
        Accessor(
            "update accounts set telegramUID=? where account=?"
        ) { statement: PreparedStatement, (account, uid): Pair<Account, TelegramUID> ->
            if (!isAccountRegistered(account))
                return@Accessor false

            return@Accessor updateAccountInfo(statement, account.value!!, uid.value)
        }.func
    }

    val setAdmin by lazy {
        Accessor(
            "update accounts set isAdmin=? where account=?"
        ) { statement: PreparedStatement, (account, isAdmin): Pair<Account, Boolean?> ->

            if (account.isInvalid() ||
                isAdmin == null ||
                !isAccountRegistered(account)
            )
                return@Accessor false


            return@Accessor updateAccountInfo(statement, account.value!!, isAdmin)
        }.func
    }


    inner class Accessor<Args, Values>(
        sql: String,
        private val function: (statement: PreparedStatement, args: Args) -> Values
    ) {
        private val statement: PreparedStatement = connection.prepareStatement(sql).also {
//            closeable.add(it::close)
        }

        val func = { args: Args -> function(statement, args) }


    }


    fun close() {
        try {
            for (close in closeable) {
                close()
            }

            connection.close()


        } catch (e: Exception) {

            e.printStackTrace()
        }


    }


}

