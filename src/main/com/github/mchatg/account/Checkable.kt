package com.github.mchatg.account

import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.security.SecureRandom
import java.util.*


interface Checkable {
    @JsonIgnore
    fun isValid(): Boolean

    @JsonIgnore
    fun isInvalid() = !isValid()
//    fun isEquals()


    companion object {
        fun allValid(vararg objects: Checkable?): Boolean {
            for (obj in objects) {
                if (obj == null || obj.isInvalid())
                    return false
            }
            return true
        }

        fun hasInvalid(vararg objects: Checkable?): Boolean = !allValid(*objects)


    }
}


interface ValueComparable<T, V> : Checkable {

    val value: V?
    fun isEquals(other: ValueComparable<T, V>): Boolean {
        if (Checkable.hasInvalid(this, other))
            return false

        return this.value == other.value
    }

    companion object {
        fun <T : ValueComparable<T, V>, V> isEquals(a: T, b: T): Boolean {
            if (Checkable.hasInvalid(a, b))
                return false

            return a.isEquals(b)
        }

        inline fun <T, reified V> toValueArrayList(list: Collection<ValueComparable<T, V>>): ArrayList<V> {
            val size = list.size
            val arrayList: ArrayList<V> = ArrayList(size)
            list.forEach {
                if (it.isValid())
                    arrayList.add(it.value!!)
            }

            return arrayList
        }


    }
}

@JvmInline
value class Account(private val value_raw: String?) : Checkable, ValueComparable<Account, String> {

    override val value: String?
        get() = value_raw?.lowercase(Locale.getDefault()) ?: value_raw

    @JsonIgnore
    override fun isValid(): Boolean = (!value.isNullOrBlank() &&
            value!!.length < 16 &&
            value!!.indexOf("|") < 0)


    fun verifyPasswd(passwd: Passwd): Boolean {
        if (Checkable.hasInvalid(this, passwd))
            return false
        var isPass: Boolean = false
        transaction {
            for (it in TAccounts.select { TAccounts.account eq value!! }) {
                //TODO:add salt
                isPass = passwd.isEquals(Passwd(it[TAccounts.passwd]))



                break
            }
        }
        return isPass
    }


    fun isAdmin(): Boolean {
        if (isInvalid()) {
            return false
        }

        for (admin in Token.admins) {
            if (Account(admin).isValid() &&
                value == Account(admin).value
            ) {
                return true
            }
        }
        return false
    }


}

@JvmInline
value class TelegramUID(override val value: Long?) : Checkable, ValueComparable<TelegramUID, Long> {
    override fun isValid(): Boolean = (value != null && value != 0L)

}

@JvmInline
value class Passwd(override val value: String?) : Checkable, ValueComparable<Passwd, String> {
    override fun isValid(): Boolean = (!value.isNullOrBlank()
//            && value.length != 64
            ) //格式限定,16进制编码sha256

    companion object {
        //TODO:add salt
        val salt = ByteArray(16).let {
            SecureRandom().nextBytes(it)
        }
    }


}


@JvmInline
value class Player(override val value: String?) : Checkable, ValueComparable<Player, String> {
    override fun isValid(): Boolean = (!value.isNullOrBlank() && value.length < 16)

    fun getOwner(): Account? {
        if (isInvalid()) {
            return null
        }
        var account: Account? = null
        transaction {
            for (it in TPlayers.select { TPlayers.name eq value!! }) {
                //TODO:add salt
                account = Account(it[TPlayers.owner])
                break
            }
        }
        return account

    }

    fun isLocked(): Boolean {
        if (isInvalid()) {
            return true
        }
        var expires = 0L
        transaction {
            val result = TPlayers.select {
                TPlayers.name eq value!!
            }.firstOrNull()
            //自动判断player是否已注册
            expires = result?.get(TPlayers.expires) ?: 0L
        }

        println("$expires")
        println(Calendar.getInstance().timeInMillis.toString())
        return expires < Calendar.getInstance().timeInMillis
    }

    fun unlock(expires: Long): Boolean {
        if (isInvalid()) {
            return false
        }

        var isSuccess: Boolean = false
        transaction {
            isSuccess = TPlayers.update({
                TPlayers.name eq value!!
            }) {
                it[TPlayers.expires] = expires
            } > 0
        }
        return isSuccess
    }


}

