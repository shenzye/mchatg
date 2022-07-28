package com.github.mchatg.telegram

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock


class TelegramMessageSession(
    val telegramBot: TelegramBot,
    val flushTime: Long,
    val chatId: Long,
    val reply: Int? = null
) {
    private val bufferMutex: ReentrantLock = ReentrantLock()
    private var buffer: StringBuffer = StringBuffer()
    val isScheduled: AtomicBoolean = AtomicBoolean(false)

    fun send(message: String) {
        bufferMutex.lock()
        //未处理也不必考虑处理空行
        if (buffer.isNotEmpty()) {
            buffer.append("\n")
        }
        buffer.append(message.trimEnd())
        bufferMutex.unlock()


        val scheduled = isScheduled.compareAndExchange(true, true)
        if (!scheduled) {
                delayedExecute()
        }
    }

    private fun flush(): Boolean {
        bufferMutex.lock()
        if (buffer.isEmpty()) {
            isScheduled.set(false)
            bufferMutex.unlock()
            return false
        }
        val message = buffer.toString()
        buffer = StringBuffer()
        bufferMutex.unlock()
        telegramBot.send(SendMessage(chatId.toString(), message).also {
            it.replyToMessageId = reply
        })
        return true
    }

    //TODO:可能爆栈，先凑合吧
    private fun delayedExecute() {
        CompletableFuture.delayedExecutor(flushTime, TimeUnit.MILLISECONDS).execute {
            if (flush()) {
                delayedExecute()
            }
        }
    }
}