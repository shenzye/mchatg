package com.github.mchatg.web

import com.github.mchatg.LogMe
import com.github.mchatg.account.Token
import com.github.mchatg.account.TokenManager
import com.google.gson.Gson
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import java.io.InputStream
import java.net.URI
import java.util.*

abstract class ApiHandler<T>(
    private val dataClass: Class<T>?
) : HttpHandler {
    open val gson = Gson()
    open val contentType = "application/json";

    protected val name: String
        get() = this::class.java.simpleName.lowercase(Locale.getDefault())

    val path
        get() = "/api/$name"


    private data class Response(
        val result: Any?
    )

    abstract fun hasPermission(token: Token?, data: T?): Boolean
    abstract fun onCall(data: T?, uri: URI): Any?
    override fun handle(exchange: HttpExchange) {

        try {
            val data: T? = when {
                dataClass == null -> {
                    null
                }
                InputStream::class.java.isAssignableFrom(dataClass) -> {
                    exchange.requestBody as T?
                }
                else -> {
                    val dataJson = String(exchange.requestBody.readAllBytes())
                    exchange.requestBody.close()
                    gson.fromJson(dataJson, dataClass).also {
                        LogMe.d("path:$path,body:$it")
                    }
                }
            }

//            LogMe.d(
//                "Authorization:" +
//                        exchange.requestHeaders.getFirst("Authorization")
//            )

            exchange.responseHeaders.set("Content-Type", this.contentType)
            exchange.sendResponseHeaders(200, 0)

            val token = TokenManager.decodeToken(
                exchange.requestHeaders.getFirst("Authorization")//String?
            )


            val hasPermission = hasPermission(token, data)
            LogMe.d("hasPermission:$hasPermission")

            val output = exchange.responseBody
            val result: Any? by lazy { onCall(data, exchange.requestURI!!) }
            val input: InputStream = when {
                !hasPermission || result == null -> {
                    "{}".toByteArray(charset = Charsets.UTF_8).inputStream()
                }
                InputStream::class.java.isAssignableFrom(result!!::class.java) -> {
                    result as InputStream
                }
                else -> {
                    gson.toJson(Response(result)).toByteArray(charset = Charsets.UTF_8).inputStream()
                }
            }
            input.copyTo(output)
            output.close()
            input.close()
        } catch (e: Exception) {
            e.printStackTrace()
            exchange.sendResponseHeaders(400, 0)
        }


    }

//    open fun getResultInputStream(result: Any?): InputStream {
//
//    }


}