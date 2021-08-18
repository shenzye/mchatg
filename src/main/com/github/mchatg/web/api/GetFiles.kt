package com.github.mchatg.web.api

import com.github.mchatg.account.Token
import com.github.mchatg.web.ApiHandler
import java.io.File
import java.net.URI

class GetFiles(private val pluginFolder: String) : ApiHandler<Unit>(null) {
    override fun hasPermission(token: Token?, data: Unit?): Boolean = true
    override fun onCall(data: Unit?, uri: URI): ArrayList<String>? {
        val filesList = ArrayList<String>()
        val dir = File(pluginFolder + File.separator + "files")
        return if (dir.isDirectory) {
            for (file in dir.list()) {
                if (!file.isNullOrBlank()) {
                    filesList.add(file)
                }
            }
            filesList
        } else {
            null
        }
    }
}