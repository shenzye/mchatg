package com.github.mchatg.web

import com.github.mchatg.until.decodeFromUrlBase64
import com.sun.net.httpserver.HttpExchange
import java.io.InputStream
import java.io.OutputStream

abstract class ApiHandler : WebHandler {

    protected val name: String
        get() = this::class.java.simpleName.toLowerCase()

    override val path
        get() = "/api/$name"

    abstract fun onCall(data_json: String): InputStream
    override fun handle(exchange: HttpExchange) {
        try {
            val json: String =
                if (exchange.requestMethod == "GET") {
                    decodeFromUrlBase64(exchange.requestURI.query)
                } else {
                    String(exchange.requestBody.readBytes())
                }


            if (json.isBlank()) {
                exchange.sendResponseHeaders(400, 0)
                return
            }


            exchange.responseHeaders.set("Content-Type", ContentType.JSON.type)
            exchange.sendResponseHeaders(200, 0)

            val output: OutputStream = exchange.responseBody
            val input = onCall(json)
            input.copyTo(output)
            output.close()
            input.close()
        } catch (e: Exception) {
            e.printStackTrace()
            exchange.sendResponseHeaders(400, 0)
        }


    }

}