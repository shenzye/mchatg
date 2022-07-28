package com.github.mchatg.until

import com.vdurmont.emoji.EmojiParser
import com.vdurmont.emoji.EmojiParser.UnicodeCandidate
import org.telegram.telegrambots.meta.api.objects.User


fun emojiFiller(input: String): String =
    EmojiParser.parseFromUnicode(
        input
    ) { unicodeCandidate: UnicodeCandidate? ->
        if (unicodeCandidate!!.hasFitzpatrick())
            "[emoji]"
        else
            "[" + unicodeCandidate.emoji.aliases[0] + "]"
    }


fun styleFiller(input: String): String = input.replace("§.".toRegex(), "")

fun getDisplayNameWithUrl(user: User) = user.run {
    "[$firstName" +
            (if (lastName.isNullOrBlank()) "" else " $lastName") +
            "](tg://user?id=" + id.toString() + ")"
}

fun getDisplayrName(user: User) = user.run {
    firstName + (if (lastName.isNullOrBlank()) "" else " $lastName")
}
