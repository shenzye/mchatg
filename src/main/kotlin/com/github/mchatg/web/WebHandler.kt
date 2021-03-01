package com.github.mchatg.web

import com.github.mchatg.Context
import com.google.gson.Gson
import com.sun.net.httpserver.HttpHandler
import java.io.ByteArrayInputStream

interface WebHandler : HttpHandler, Context {

    val path: String


}

open class SuccessResponse {
    val success: Boolean = true

    fun inputStream(): ByteArrayInputStream {
        return Gson().toJson(this).toByteArray(charset("UTF-8")).inputStream()
    }


}


fun faileResponseInputStream() = "{\"success\":false}".toByteArray(charset("UTF-8")).inputStream()

enum class ContentType(val suffix: String, val type: String) {

    TEXT(".txt", "text/plain"),
    HTML(".html", "text/html"),
    JS(".js", "application/x-javascript"),
    CSS(".css", "text/css"),
    JSON(".json", "application/json"),
    JPG(".jpg", "image/jpeg"),
    PNG(".png", "image/png"),
    ZIP(".zip", "application/octet-stream"),
    DOWNLOAD("", "application/octet-stream")
}