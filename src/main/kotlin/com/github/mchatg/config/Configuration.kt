package com.github.mchatg.config


class Configuration(val config: ConfigAccessor) {

    val path = ""

    val global by lazy { Global() }

    inner class Global : Node("global", path) {
        val enable by lazy { Field("plugin-enable", false) }
        val bot by lazy { Bot() }
        val proxy by lazy { Proxy() }

        inner class Bot : Node("bot", path) {
            val token by lazy { Field("$path.token", "") }
            val username by lazy { Field("$path.username", "") }
            val group_id by lazy { Field("$path.group-id", 0L) }
        }

        inner class Proxy : Node("proxy", path) {
            val enable by lazy { Field("$path.enable", false) }
            val type by lazy { Field("$path.type", "") }
            val host by lazy { Field("$path.host", "") }
            val port by lazy { Field("$path.port", 0) }
        }


    }

    val message by lazy { Message() }

    inner class Message : Node("message", path) {
        val enable by lazy { Field("$path.enable", false) }
        val server_up by lazy { Field("$path.server-up", false) }
        val forward_chat by lazy { Forward_chat() }
        val onJoin by lazy { OnJoin() }
        val onDeath by lazy { OnDeath() }
        val onQuit by lazy { OnQuit() }


        inner class Forward_chat : Node("forward-chat", path) {
            val enable by lazy { Field("$path.enable", false) }
            val conversion_name by lazy { Field("$path.conversion-name", false) }

        }

        inner class OnJoin : Node("onJoin", path) {
            val enable by lazy { Field("$path.enable", false) }
            val interval by lazy { Field("$path.interval", 0) }
        }

        inner class OnQuit : Node("onQuit", path) {
            val enable by lazy { Field("$path.enable", false) }
            val interval by lazy { Field("$path.interval", 0) }
        }

        inner class OnDeath : Node("onDeath", path) {
            val enable by lazy { Field("$path.enable", false) }
            val interval by lazy { Field("$path.interval", 0) }
        }


    }

    val account by lazy { Account() }

    inner class Account : Node("account", path) {
        val enable by lazy { Field("$path.enable", false) }
        val invitecode by lazy { Field("$path.invitecode", "") }
        val limit by lazy { Field("$path.limit", 1) }
        val allow_tourist by lazy { Field("$path.allow-tourist", false) }
        val super_admin by lazy { Field("$path.super-admin", "") }


    }


    val web by lazy{ Web()}

    inner class Web : Node("web", path) {
        val socket by lazy { Field("$path.socket", -1) }
        val inner by lazy { Field("$path.inner", false) }
        val folder by lazy { Field("$path.folder", "") }

    }



    val command by lazy { Command() }

    inner class Command : Node("command", path) {
        val enable by lazy { Field("$path.enable", false) }
        val list by lazy { Field("$path.list", false) }
        val ping by lazy { Field("$path.ping", false) }
        val kickme by lazy { Field("$path.kickme", false) }

        val execute by lazy { Field("$path.execute", false) }


    }


    open inner class Field<T>(private val path: String, def: T) {
        private var field: T = (config.get(path, def as Any) as T)
        fun set(value: T) {
            field = value
            config.set(path, field as Any)
        }

        fun get() = field
        fun getRaw() = field

    }

//    inner class Switch(private val path: String, def: Boolean, val depend: Field<Boolean>) : Field<Boolean>(path, def) {
//        override fun get(): Boolean {
//            if (!depend.get()) {
//                return false
//            }
//            return getRaw()
//        }
//    }

    open inner class Node(name: String, parent: String) {
        val path = if (parent == "") name else "$parent.$name"
    }


}


