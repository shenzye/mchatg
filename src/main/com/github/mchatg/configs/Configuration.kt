package com.github.mchatg.configs

import com.google.gson.Gson
import java.io.File


class Configuration {

    companion object {
        fun load(path: String): Configuration {

            return try {
                Gson().fromJson(File(path).readText(), Configuration::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                Configuration()
            }


        }

        fun save(path: String, config: Configuration) {
            //TODO
        }

    }


    var pluginEnable: Boolean = false
    var global: Global = Global()
    var account: Account = Account()
    var web: Web = Web()
    var command: Command = Command()
    var message: Message = Message()


    class Global {

        var bot: Bot = Bot()
        var proxy: Proxy = Proxy()


        class Bot {
            var enable: Boolean = false

            var token: String = ""
            var username: String = ""
            var groupId: String = ""//Long
        }

        class Proxy {
            var enable: Boolean = false
            var type: String = ""
            var host: String = ""
            var port: String = ""//Int
        }
    }


    class Account {
        //account
        var enable: Boolean = true
        var inviteCode: String = ""

        //TODO
        var limit: Long = 1
        var allowTourist: Boolean = false
        var admins: ArrayList<String> = ArrayList()

    }

    class Web {
        //web
        var socket: Long = -1
        var inner: Boolean = true
    }

    class Command {
        //command
        var enable: Boolean = true
        var list: Boolean = true
        var ping: Boolean = true
        var kickMe: Boolean = true
        var execute: Boolean = true

    }


    class Message {
        //message
        var enable: Boolean = true
        var serverUp: Boolean = true
        var forwardChat: ForwardChat = ForwardChat()
        var onJoin: OnJoin = OnJoin()
        var onQuit: OnQuit = OnQuit()
        var onDeath: OnDeath = OnDeath()


        class ForwardChat {
            var enable: Boolean = true

            //message.forward-chat
            var conversionName: Boolean = false
        }


        class OnJoin {
            //message.onJoin
            var enable: Boolean = true
            var interval: Long = 2
        }

        class OnQuit {
            //message.onQuit
            var enable: Boolean = true
            var interval: Long = 2
        }

        class OnDeath {
            //message.onDeath
            var enable: Boolean = true
            var interval: Long = 2
        }
    }


}

