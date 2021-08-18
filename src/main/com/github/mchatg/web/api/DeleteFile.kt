package com.github.mchatg.web.api

import com.github.mchatg.account.Token
import com.github.mchatg.web.ApiHandler
import java.io.File
import java.net.URI

class DeleteFile(private val pluginFolder: String) : ApiHandler<DeleteFile.Data>(Data::class.java) {
    data class Data(
        val name: String?,
    )

    override fun hasPermission(token: Token?, data: Data?): Boolean {
        return token?.account?.isAdmin() ?: false
    }

    override fun onCall(data: Data?, uri: URI): Boolean {
        if (data?.name?.indexOf(File.separator) ?: 0 >= 0) {
            return false
        }

        val file = File(pluginFolder + File.separator + "files" + File.separator + data!!.name)
        return file.delete()
    }
}