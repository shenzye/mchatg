package com.github.mchatg.until

import java.nio.ByteBuffer
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import kotlin.jvm.Throws

class ChaCha20Poly1305Cipher {
    private val key: SecretKey = KeyGenerator.getInstance("ChaCha20").let {
        // A 256-bit secret key (32 bytes)
        it.init(256, SecureRandom.getInstanceStrong())
        it.generateKey()
    }

    private val ENCRYPT_ALGO = "ChaCha20-Poly1305"
    private val NONCE_LEN = 12 // 96 bits, 12 bytes

    @Throws
    fun encrypt(pText: ByteArray): ByteArray {
        //generate a random 12 bytes nonce
        val nonce = ByteArray(NONCE_LEN).also {
            SecureRandom().nextBytes(it)
        }
        val iv = IvParameterSpec(nonce)
        val encrypter = Cipher.getInstance(ENCRYPT_ALGO)
        encrypter.init(Cipher.ENCRYPT_MODE, key, iv)
        val encryptedText = encrypter.doFinal(pText)
        return ByteBuffer.allocate(NONCE_LEN + encryptedText.size)
            .put(nonce)
            .put(encryptedText)
            .array()
    }

    @Throws
    fun decrypt(cText: ByteArray): ByteArray {
        val nonce = cText.sliceArray(0 until NONCE_LEN)
        val encryptedText = cText.sliceArray(NONCE_LEN until cText.size)
        val iv = IvParameterSpec(nonce)
        val decrypter = Cipher.getInstance(ENCRYPT_ALGO)
        decrypter.init(Cipher.DECRYPT_MODE, key, iv)
        // decrypted text
        return decrypter.doFinal(encryptedText)
    }


}