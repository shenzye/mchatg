package com.github.mchatg.web.api

import com.github.mchatg.account.Token
import com.github.mchatg.web.ApiHandler
import com.github.mchatg.web.ContentType
import java.io.File
import java.io.InputStream
import java.net.URI

class DownloadFile(private val pluginFolder: String) : ApiHandler<Unit>(null) {

    override val contentType: String = ContentType.DOWNLOAD.type;

    override fun hasPermission(token: Token?, data: Unit?): Boolean = true
    override fun onCall(body: Unit?, uri: URI): InputStream? {
        val parse = uri.query.split("=")
        //TODO:test
        if (parse.size != 2 ||
            parse[0] != "name" ||
            parse[1].isBlank() || parse[1].indexOf(File.separatorChar) >= 0
        ) {
            return null
        }

        val fileName = parse[1]
        return File(
            pluginFolder + File.separator + "files" + File.separator + fileName
        ).inputStream()


    }


}