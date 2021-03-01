package com.github.mchatg.account

import com.github.mchatg.Context
import com.github.mchatg.until.TimeMeter
import java.security.SecureRandom
import java.util.*

object TokenManager {


    val tokenMap: HashMap<String, Token.PasswdToken> by lazy { HashMap() }
    val accountMap: HashMap<String, String> by lazy { HashMap() }
    val timeMeter: TimeMeter<String> by lazy { TimeMeter(Calendar.DATE, 14) }//有效期为14天

    fun getKey(context: Context, account: Account, passwd: Passwd): String? {
        val token = Token.PasswdToken(context, account, passwd)
        if (token.isInvalid())
            return null

        var key = ""
        do {
            key = SecureRandom().nextLong().toString()
        } while (tokenMap[key] != null)


        tokenMap[key] = token
        timeMeter.push(key)

        val key_old: String? = accountMap[token.account.value]
        if (key_old != null) {
            tokenMap.remove(key_old)
        }
        accountMap[token.account.value!!] = key
        return key
    }


    fun getToken(key: String?): Token? {
        if (key == null)
            return null
        val token = tokenMap[key]
        if (token == null)
            return null

        if (token.isInvalid() || timeMeter.isTimeOut(key)) {
            tokenMap.remove(key)
            accountMap.remove(token.account.value!!)
            timeMeter.remove(key)
            return null
        }

        return token
    }


}