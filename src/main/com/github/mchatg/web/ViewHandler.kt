package com.github.mchatg.web

import com.github.mchatg.LogMe
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import java.io.InputStream
import java.io.OutputStream

class ViewHandler : HttpHandler {

    override fun handle(exchange: HttpExchange) {
        val path: String = exchange.requestURI.path.toString().let {
            if (it == "/" || it == "") {
                "/index.html"
            } else {
                it
            }

        }

        var input: InputStream? = null
        try {
            input = this.javaClass.getResourceAsStream("/webview" + path)
        } catch (e: Exception) {
            println(e.toString())
        }

        if (input == null) {
            exchange.sendResponseHeaders(404, 0)
            exchange.responseBody.write(pageNotFound)
            exchange.responseBody.close()
            return
        }


        val start = path.lastIndexOf(".")
        if (start != -1) {
            //TODO:better
            val fileType = path.substring(start)
            for (type in ContentType.values()) {
                if (fileType == type.suffix) {
                    exchange.responseHeaders.set("Content-Type", type.type)
                    break
                }
            }
        }

        try {
            exchange.sendResponseHeaders(200, 0)
            val output: OutputStream = exchange.responseBody
            input.copyTo(output)
            output.close()
            input.close()

        } catch (e: Exception) {
            //everything is ok
        }

    }
}

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

val pageNotFound =
    "<!DOCTYPE html><html lang=\"zh\"><head><meta charset=\"utf-8\"><link rel=\"icon\" href=\"/favicon.ico\"><title>mchatg-404找不到页面</title></head><body><div>404找不到页面</div><div>具体情况建议不要找作者，作者也不知道为什么你会访问到这个页面 w/v(*´∀｀*)v</div></body></html>".toByteArray(
        charset("UTF-8")
    )