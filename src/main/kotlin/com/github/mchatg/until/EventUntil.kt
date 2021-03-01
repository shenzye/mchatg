package com.github.mchatg.until

import com.github.mchatg.config.Configuration
import com.github.mchatg.telegram.TelegramBot
import java.util.*


class EventMessage(
    private val telegramBot: TelegramBot,
    private val enable: Configuration.Field<Boolean>,
    private val interval: Configuration.Field<Int>,
    private val chatId: Configuration.Field<Long>
) {
    private val timeMeter by lazy { TimeMeter<Any>(Calendar.MINUTE,interval.get()) }
    fun send(target: Any, text: String) {
        when {
            !enable.get() -> return
            (interval.get() > 0) && !timeMeter.isTimeOut(target) -> return
            else -> {
                telegramBot.send(chatId.get(), text)
                timeMeter.push(target)
            }
        }
    }
}
