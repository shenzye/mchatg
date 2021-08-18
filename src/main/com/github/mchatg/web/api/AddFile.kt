package com.github.mchatg.web.api

import com.github.mchatg.account.Token
import com.github.mchatg.web.ApiHandler
import java.io.File
import java.io.InputStream
import java.net.URI

class AddFile(private val pluginFolder: String) : ApiHandler<InputStream>(InputStream::class.java) {
    override fun hasPermission(token: Token?, data: InputStream?): Boolean {
        return token?.account?.isAdmin() ?: false
    }

    override fun onCall(body: InputStream?, uri: URI): Boolean {
        println("dataIsNull:" + (body == null))
        println("uri:$uri")
        println("uri-query:" + uri.query)

        body ?: return false
        val params = uri.query.split("&")
        val map = HashMap<String, String>()
        for (param in params) {
            val paramTmp = param.split("=")
            if (paramTmp.size == 2) {
                map[paramTmp[0]] = paramTmp[1]
            }
        }
        val name = map["name"] ?: return false

        if (name.isBlank() || name.indexOf(File.separatorChar) >= 0) {
            return false
        }

        //TODO:hash验证
        val length = map["length"]?.toLongOrNull() ?: return false
        try {
            val output = File(
                pluginFolder + File.separator + "files" + File.separator + name
            ).outputStream()
            body.copyTo(
                output
            )
            body.close()
            output.close()
        } catch (e: Exception) {
            File(
                pluginFolder + File.separator + "files" + File.separator + name
            ).delete()
            return false
        }
        val file = File(
            pluginFolder + File.separator + "files" + File.separator + name
        )
        return if (
            !file.isFile ||
            file.length() == 0L ||
            file.length() != length
        ) {
            file.delete()
            false
        } else {
            true
        }

    }
}

