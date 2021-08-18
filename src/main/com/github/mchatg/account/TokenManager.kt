package com.github.mchatg.account

import com.github.mchatg.LogMe
import com.github.mchatg.until.ChaCha20Poly1305Cipher
import com.google.gson.Gson
import java.util.*

object TokenManager {

    private val cipher = ChaCha20Poly1305Cipher()
    private val gson = Gson()

    fun getToken(account: Account, passwd: Passwd): Token.PasswdToken? {
        if (Checkable.hasInvalid(account, passwd)) {
            return null
        }
        val token = Token.PasswdToken(account, passwd)
        if (token.isInvalid()) {
            return null
        }

        return token
    }


    fun encodeToken(token: Token.PasswdToken): String {
        val base64_raw = StringBuffer(
            Base64.getEncoder().encodeToString(
                cipher.encrypt(
                    gson.toJson(token).toByteArray()
                )
            )
        )
        println("base64-e:$base64_raw")
        //移除等号填充，将原等号填充个数放到最后，避免被j(g)son转码
        var esign = 0
        while (base64_raw.last() == '=') {
            base64_raw.deleteCharAt(base64_raw.length - 1)
            esign++
        }
        return base64_raw.append(esign.toString()).toString()
    }

    fun decodeToken(key: String?): Token.PasswdToken? {
        if (key == null) {
            return null
        }

        var base64: StringBuffer = StringBuffer(key)
        //根据最后一位填充等号
        base64 = when (base64[base64.length - 1]) {
            '1' -> {
                base64.deleteCharAt(base64.length - 1).append("=")
            }
            '2' -> {
                base64.deleteCharAt(base64.length - 1).append("==")
            }
            else -> {
                base64.deleteCharAt(base64.length - 1)
            }
        }

        println("base64-d:$base64")
        var token: Token.PasswdToken? = null
        try {
            token = gson.fromJson(
                String(
                    cipher.decrypt(
                        Base64.getDecoder().decode(base64.toString())
                    )
                ),
                Token.PasswdToken::class.java
            )
        } catch (e: Exception) {
            LogMe.d(e.toString())
        }

        return if (Checkable.allValid(token)) {
            token
        } else {
            null
        }
    }


}