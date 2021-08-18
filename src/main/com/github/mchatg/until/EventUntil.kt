package com.github.mchatg.until

import com.github.mchatg.telegram.TelegramBot
import java.util.*


class EventMessage(
    //TODO:热重载
    private val telegramBot: TelegramBot,
    private val enable: Boolean,
    private val interval: Long,
    private val chatId: Long
) {
    private val timeMeter by lazy { TimeMeter<Any>(Calendar.MINUTE, interval.toInt()) }
    fun send(target: Any, text: String) {
        when {
            !enable -> return
            (interval > 0) && !timeMeter.isTimeOut(target) -> return
            else -> {
                telegramBot.send(chatId, text)
                timeMeter.push(target)
            }
        }
    }
}
