package com.github.mchatg

import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.system.exitProcess

@Suppress("NOTHING_TO_INLINE")
class LogMe(val context: Context) {

    val gson = Gson()
    val now: String
        get() = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm"))

    val output by lazy {
        val file = File(context.plugin.dataFolder.path + File.separator + "logs")
        file.mkdir()
        File(
            file.path + File.separator + "$now.txt"
        ).outputStream()
    }


    inline fun f(vararg args: Any) {
        val message = deal(args)
        output(message)
        print(message)
        exitProcess(0)
    }

    inline fun e(vararg args: Any) {
        val message = deal(args)
        output(message)
        print(message)
    }

    inline fun i(vararg args: Any) {

        val message = deal(args)
        output(message)
//        print(message)


    }

    inline fun d(vararg args: Any) {
        val message = deal(args)
        output(message)
        print(message)

    }


    inline fun deal(vararg args: Any): String {
        var message = ""
        val stackTrace = Thread.currentThread().stackTrace[1]
        stackTrace.run {
            for (arg in args) {
                message = "$message$className.$methodName:" + gson.toJson(arg).replace("\\", "") + "\n"

            }

        }
        return message

    }

    private val logs by lazy {
        val logs = Channel<String>(4)
        GlobalScope.launch {
            try {
                for (message in logs) {
                    output.write("[$now]$message".toByteArray())
                }
            } catch (e: Exception) {
                e.printStackTrace()
//                exitProcess(1)
            }
        }
        logs
    }


    fun output(message: String) {

        logs.sendBlocking(message)

    }


}