package com.github.mchatg.telegram

import com.github.mchatg.Context
import com.github.mchatg.until.emojiFiller
import com.github.mchatg.until.getDisplayrName
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User

class MessagesHandler(context: Context) : Context by context {

    fun handle(update: Update) {

        try {
            if (update.message.isSuperGroupMessage &&
                update.message.chatId == (config.bot.groupId)
            )
                handleGroup(update)
        } catch (e: Exception) {
            logger.warning(e.toString())
        }


    }


    private fun handleGroup(update: Update) {

        if (update.message.text != null)
            textHandler(update.message)
        else
            mediaHandler(update.message)


    }

    private fun textHandler(message: Message) {
        if (config.message.forwardChatEnable) {
            val name: String = getName(message.from)
            val lines = message.text.split("\n")
            if (lines.size > 1) {
                serverApis.broadcastMessage("<$name@tg>↓" + emojiFiller(lines[0]))
                for (i in 1 until lines.size) {
                    serverApis.broadcastMessage("<$name@tg>↑" + emojiFiller(lines[i]))
                }
            } else
                serverApis.broadcastMessage("<$name@tg> " + emojiFiller(lines[0]))
        }
    }

    private fun mediaHandler(message: Message) {
        if (config.message.forwardChatEnable) {
            val name = getName(message.from)
            val broadcast = fun(name: String, caption: String?, type: String) {
                if (caption == null) {
                    serverApis.broadcastMessage("<$name@tg> [$type]")
                } else {
                    val lines = caption.split("\n")
                    if (lines.size > 1) {
                        serverApis.broadcastMessage("<$name@tg>↓[$type]" + emojiFiller(lines[0]))
                        for (i in 1 until lines.size) {
                            serverApis.broadcastMessage("<$name@tg>↑" + emojiFiller(lines[i]))
                        }

                    } else
                        serverApis.broadcastMessage("<$name@tg> [$type]" + emojiFiller(lines[0]))
                }
            }
            when {
                message.hasSticker() -> broadcast(name, null, "sticker")
                message.hasAnimation() -> broadcast(name, message.caption, "animation")
                message.hasAudio() -> broadcast(name, message.caption, "audio")
                message.hasDocument() -> broadcast(name, message.caption, "document")
                message.hasPhoto() -> broadcast(name, message.caption, "photo")
                message.hasVideo() -> broadcast(name, message.caption, "video")
            }
        }

    }


    private fun getName(user: User): String {
        return if (user.userName.isNullOrBlank()) {
            getDisplayrName(user)
        } else {
            user.userName
        }


    }


}