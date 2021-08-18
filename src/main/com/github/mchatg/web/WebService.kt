package com.github.mchatg.web

import com.github.mchatg.Context
import com.github.mchatg.web.api.*
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress


class WebService(context: Context) : Context by context {

    val server: HttpServer by lazy {
        HttpServer.create(
            InetSocketAddress(
                config.web.socket.toInt()
            ), 0
        )
    }

    fun init() {
        if (config.web.socket !in 0..65535)
            return

        println("init web:" + config.web.socket)


        //TODO:文件功能:test
        registerWebHandler(AddFile(pluginFolder))
        registerWebHandler(DeleteFile(pluginFolder))
        registerWebHandler(GetFiles(pluginFolder))
        registerWebHandler(DownloadFile(pluginFolder))

        registerWebHandler(GetPlayers())
        registerWebHandler(GetToken())
        registerWebHandler(RegisterAccount(this.config))
        registerWebHandler(AddPlayer())
        registerWebHandler(Unlock())
        registerWebHandler(RemovePlayer())
        registerWebHandler(UpdatePasswd())
        registerWebHandler(GetAccounts())

        if (config.web.inner) {
            server.createContext("/", ViewHandler())
        }

        server.start()


    }


    private fun <T> registerWebHandler(handler: ApiHandler<T>) {
        server.createContext(handler.path, handler)
    }


}


