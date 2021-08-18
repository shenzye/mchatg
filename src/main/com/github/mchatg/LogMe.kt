package com.github.mchatg

import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

@Suppress("NOTHING_TO_INLINE")
object LogMe {
    var info: Boolean = false
    var debug: Boolean = false

    private val now: String
        get() = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm"))

    private var output: FileOutputStream? = null


    fun init(path: String, info: Boolean, debug: Boolean) {
        val dir = File(path)
        dir.mkdir()
        output = File(
            dir.path + File.separator + "$now.txt"
        ).outputStream()

        this.info = info
        this.debug = debug
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
        if (info) {
            output(message)
            print(message)
        }
    }

    inline fun d(vararg args: Any) {
        val message = deal(args)
        if (debug) {
            output(message)
            print(message)
        }


    }


    inline fun deal(vararg args: Any): String {
        var message = ""
        val stackTrace = Thread.currentThread().stackTrace[1]
        stackTrace.run {
            for (arg in args) {

                message = "$message$className.$methodName:" + Gson().toJson(arg).replace("\\", "") + "\n"

            }

        }
        return message

    }

    private val logs by lazy {
        val logs = Channel<String>(4)
        GlobalScope.launch {
            try {
                for (message in logs) {
                    output!!.write("[$now]$message".toByteArray())
                }
            } catch (e: Exception) {
                e.printStackTrace()
//                exitProcess(1)
            }
        }
        logs
    }


    fun output(message: String) {

        logs.trySendBlocking(message)

    }


}