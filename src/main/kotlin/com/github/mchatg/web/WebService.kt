package com.github.mchatg.web

import com.github.mchatg.Context
import com.github.mchatg.web.api.*
import com.sun.net.httpserver.HttpServer

import java.net.InetSocketAddress

class WebService(context: Context) : Context by context {


    val server: HttpServer by lazy {
        HttpServer.create(
            InetSocketAddress(

                config.web.socket.get()


            ), 0
        )
    }

    fun init() {

        if (config.web.socket.get() !in 0..65535)
            return

        registerWebHandler(GetPlayers(this))
        registerWebHandler(GetToken(this))
        registerWebHandler(RegisterAccount(this))
        registerWebHandler(AddPlayer(this))
        registerWebHandler(Unlock(this))
        registerWebHandler(RemovePlayer(this))
        registerWebHandler(UpdatePasswd(this))
        registerWebHandler(GetAccounts(this))
        registerWebHandler(SetAdmin(this))

        registerWebHandler(ViewHandler(this))

        server.start()


    }


    private fun registerWebHandler(handler: WebHandler) {
        server.createContext(handler.path, handler)
    }


}


