package com.github.mchatg.message

import com.github.mchatg.Context
import com.github.mchatg.until.emojiFiller
import com.github.mchatg.until.getUserName
import org.bukkit.Bukkit
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User

class TelegramMessagesHandler(context: Context) : Context by context {


    fun handler(message: Message) {
        if (message.text != null)
            textHandler(message)
        else
            mediaHandler(message)
    }


    private fun textHandler(message: Message) {
        if (config.message.forwardChat.enable) {
            val name: String = getName(message.from)
            val lines = message.text.split("\n")
            if (lines.size > 1) {
                Bukkit.broadcastMessage("<$name>↓" + emojiFiller(lines[0]))
                for (i in 1 until lines.size) {
                    Bukkit.broadcastMessage("<$name>↑" + emojiFiller(lines[i]))
                }
            } else
                Bukkit.broadcastMessage("<$name> " + emojiFiller(lines[0]))
        }
    }

    private fun mediaHandler(message: Message) {
        //TODO:conversion-name
        if (config.message.forwardChat.enable) {
            val name = getName(message.from)
            val broadcast = fun(name: String, caption: String?, type: String) {
                if (caption == null) {
                    Bukkit.broadcastMessage("<$name> [$type]")
                } else {
                    val lines = caption.split("\n")
                    if (lines.size > 1) {
                        Bukkit.broadcastMessage("<$name>↓[$type]" + emojiFiller(lines[0]))
                        for (i in 1 until lines.size) {
                            Bukkit.broadcastMessage("<$name>↑" + emojiFiller(lines[i]))
                        }

                    } else
                        Bukkit.broadcastMessage("<$name> [$type]" + emojiFiller(lines[0]))
                }
            }
            when {
                message.hasSticker() -> Bukkit.broadcastMessage("<$name> [sticker]")
                message.hasAnimation() -> broadcast(name, message.caption, "animation")
                message.hasAudio() -> broadcast(name, message.caption, "audio")
                message.hasDocument() -> broadcast(name, message.caption, "document")
                message.hasPhoto() -> broadcast(name, message.caption, "photo")
                message.hasVideo() -> broadcast(name, message.caption, "video")
            }
        }

    }


    private fun getName(user: User): String {


        return user.run {
//            TODO:conversion-name
//            if (context.config.getBoolean("forward.show.chat.conversion-name")) {
//                val names = database.getNamesById(id)
//                if (names.size > 0) {
//                    names[0]
//
//                } else
//                    if (userName != null) userName else "$firstName $lastName"
//            }

            if (user.userName.isNullOrBlank()) {
                getUserName(user)
            } else {
                user.userName
            }


        }
    }


}