package com.github.mchatg

import com.google.gson.Gson
import java.io.File


class Configuration {
    companion object {
        fun load(path: String): Configuration {
            val gson = Gson()
            return try {
                gson.fromJson(File(path).readText(), Configuration::class.java)
            } catch (e: Exception) {
                val file = File(path)
                if (!file.exists()) {
                    val bis = this::class.java.getResourceAsStream("/config.json")!!
                    bis.copyTo(file.outputStream())
                    bis.close()
                }
                Configuration()
            }

        }
    }


    var pluginEnable: Boolean = false

    var bot: Bot = Bot()


    class Bot {
        var token: String = ""
        var username: String = ""
        var groupId: Long = 0//Long
    }

    var proxy: Proxy = Proxy()
    var command: Command = Command()
    var message: Message = Message()

    class Proxy {
        var enable: Boolean = false
        var type: String = "socks5"
        var host: String = "localhost"
        var port: String = "1080"//Int
    }


    class Command {
        //command
        var enable: Boolean = true
        var list: Boolean = true
        var ping: Boolean = true
        var execute: Boolean = true
        var admins: Array<Long> = arrayOf()
    }


    class Message {
        //message
        var serverUp: Boolean = false
        var forwardChatEnable: Boolean = true
        var onJoinEnable: Boolean = true
        var onJoinInterval: Int = 2

        var onQuitEnable: Boolean = true
        var onQuitInterval: Int = 2
        var onDeathEnable: Boolean = true
        var onDeathInterval: Int = 2
    }


}

