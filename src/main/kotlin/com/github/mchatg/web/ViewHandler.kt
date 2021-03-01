package com.github.mchatg.web

import com.github.mchatg.Context
import com.sun.net.httpserver.HttpExchange
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class ViewHandler(context: Context) : WebHandler, Context by context {
    override val path = "/"

    override fun handle(exchange: HttpExchange) {


        exchange.requestURI.path.toString().let {
            val start = it.lastIndexOf(".")
            if (start != -1) {
                //TODO:better
                val fileType = it.substring(start)
                for (type in ContentType.values()) {
                    if (fileType == type.suffix) {
                        exchange.responseHeaders.set("Content-Type", type.type)
                        break
                    }
                }
            }

        }

        var input: InputStream? = null

        try {
            input = if (config.web.inner.get()) {
                this.javaClass.getResourceAsStream("/web" + exchange.requestURI.path)
            } else {
                File(config.web.folder.get() + exchange.requestURI.path).inputStream()
            }

        } catch (e: Exception) {
            println("找不到web文件")
            exchange.sendResponseHeaders(404, 0)
        }

        try {
            if (input != null) {
                exchange.sendResponseHeaders(200, 0)
                val output: OutputStream = exchange.responseBody
                input.copyTo(output)
                output.close()
                input.close()
            }


        } catch (e: Exception) {
            //everything is ok

        }

    }
}