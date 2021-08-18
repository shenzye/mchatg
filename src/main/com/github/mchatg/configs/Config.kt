//package com.github.mchatg.configs
//
//import com.github.mchatg.config.ConfigAccessor
//import com.google.gson.Gson
//
//
//object Config {
//    private var config: ConfigAccessor? = null
//    private val map: HashMap<String, Field> = HashMap()
//
//
////    fun getField(ctx: Context) {
////        LogMe.d(
////            "contextPath:" + ctx.contextPath(),
////            "path:" + ctx.path()
////        )
////        //不可能失败返回missingDelimiterValue
////        val path = ctx.path().substringAfter(ctx.contextPath())
////        val data: String? = map[path]?.toJson()
////        if (data != null) {
////            ctx.status(200)
////            ctx.result(data)
////        }
////        //TODO:test:404怎么办
////    }
//
////    fun setField(ctx: Context) {
////        //不可能失败返回missingDelimiterValue
////        val path = ctx.path().substringAfter(ctx.contextPath())
////        val isSuccess: Boolean = map[path]?.setFromJson(ctx.body()) ?: false
////
////        ctx.status(200)
////        ctx.result("{\"success\":$isSuccess}")
////
////        //TODO:test:404怎么办
////    }
//
//    fun init(config: ConfigAccessor) {
//        Config.config = config
//    }
//
//    object global {
//        //global
//        val enable = bool("plugin-enable", false)
//
//        object bot {
//            //global.bot
//            val token = text("global.bot.token", "")
//            val username = text("global.bot.username", "")
//            val group_id = long("global.bot.group-id", 0L)
//        }
//
//        object proxy {
//            //global.proxy
//            val enable = bool("global.proxy.enable", false)
//            val type = text("global.proxy.type", "")
//            val host = text("global.proxy.host", "")
//            val port = long("global.proxy.port", 0)
//        }
//
//
//    }
//
//    object message {
//        //message
//        val enable = bool("message.enable", false)
//        val server_up = bool("message.server-up", false)
//
//        object forward_chat {
//            //message.forward-chat
//            val enable = bool("message.forward-chat.enable", false)
//            val conversion_name = bool("message.forward-chat.conversion-name", false)
//        }
//
//
//        object onJoin { //message.onJoin
//            val enable = bool("message.onJoin.enable", false)
//            val interval = long("message.onJoin.interval", 0)
//        }
//
//        object onQuit {
//            //message.onQuit
//            val enable = bool("message.onQuit.enable", false)
//            val interval = long("message.onQuit.interval", 0)
//        }
//
//        object onDeath {
//            //message.onDeath
//            val enable = bool("message.onDeath.enable", false)
//            val interval = long("message.onDeath.interval", 0)
//        }
//
//
//    }
//
//    object account {
//        //account
//        val enable = bool("account.enable", false)
//        val invitecode = text("account.invitecode", "")
//        val limit = long("account.limit", 1)
//        val allow_tourist = bool("account.allow-tourist", false)
//        val admins = text("account.admins", "")
//
//    }
//
//    object web {
//        //web
//        val socket = long("web.socket", -1)
//        val inner = bool("web.inner", false)
//
//
//    }
//
//    object command {
//        //command
//        val enable = bool("command.enable", false)
//        val list = bool("command.list", false)
//        val ping = bool("command.ping", false)
//        val kickme = bool("command.kickme", false)
//        val execute = bool("command.execute", false)
//
//
//    }
//
//
//    class long(yamlPath: String, def: Long) : FieldWrapper<Long>(yamlPath, def, "long")
//    class text(yamlPath: String, def: String) : FieldWrapper<String>(yamlPath, def, "text")
//    class bool(yamlPath: String, def: Boolean) : FieldWrapper<Boolean>(yamlPath, def, "bool")
//    open class FieldWrapper<T>(yamlPath: String, def: Any, type: String) : Field(yamlPath, def, type) {
//        fun get(): T {
//            return super.getAny() as T
//        }
//
//        fun set(value: T) {
//            super.setAny(value as Any)
//        }
//    }
//
//
//    data class Data(
//        val type: String,
//        val bool: Boolean? = null,
//        val long: String? = null,
//        val text: String? = null
//    )
//
//
//    open class Field(private val yamlPath: String, def: Any, val type: String) {
//        private var field = config!!.get(yamlPath, def)
//
//        init {
//            val urlPath = "/" + yamlPath.replace(".", "/")
//            map[urlPath] = this
//        }
//
//        protected fun setAny(value: Any) {
//            field = value
//            config!!.set(yamlPath, field)
//        }
//
//        protected fun getAny() = field
//
////        fun toJson(): String? {
////            return when (type) {
////                "bool" -> toJson(Data(type, bool = field as Boolean))
////                "long" -> toJson(Data(type, long = (field as Long).toString()))
////                "text" -> toJson(Data(type, text = field as String))
////                else -> null
////            }
////        }
//
////        fun setFromJson(data_json: String): Boolean {
////
////
////            val data: Data = Gson().fromJson(data_json, Data::class.java)
////            val long by lazy { data.long?.toLongOrNull() }
////            return when {
////                this.type == "bool" && data.bool != null -> {
////                    setAny(data.bool)
////                    true
////                }
////                this.type == "long" && long != null -> {
////                    setAny(long!!)
////                    true
////                }
////                this.type == "text" && data.text != null -> {
////                    setAny(data.text)
////                    true
////                }
////                else -> false
////            }
////        }
//    }
//
//
//}
//
//
//
//
//
