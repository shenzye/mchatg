package com.github.mchatg.until

import com.vdurmont.emoji.EmojiParser
import com.vdurmont.emoji.EmojiParser.UnicodeCandidate
import org.telegram.telegrambots.meta.api.objects.User
import java.io.UnsupportedEncodingException
import java.util.*


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

fun getUserNameUrl(user: User) = user.run {
    "[$firstName" +
            (if (lastName.isNullOrBlank()) "" else " $lastName") +
            "](tg://user?id=" + id.toString() + ")"
}

fun getUserName(user: User) = user.run {
    firstName + (if (lastName.isNullOrBlank()) "" else " $lastName")
}


fun getUrlQueryParam(queryString: String): Map<String, String> {
//    TODO:test
    val queryStringSplit = queryString.split("&").toTypedArray()
    val queryStringMap: MutableMap<String, String> = HashMap(queryStringSplit.size)
    var queryStringParam: Array<String>
    for (qs in queryStringSplit) {
        queryStringParam = qs.split("=").toTypedArray()
        queryStringMap[queryStringParam[0]] = queryStringParam[1]
    }
    return queryStringMap
}


fun encodeToUrlBase64(input: String, charset: String = "utf-8"): String {

    return try {
        Base64.getUrlEncoder().encodeToString(input.toByteArray(charset(charset)))
    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
        ""
    }
}

fun decodeFromUrlBase64(input: String, charset: String = "utf-8"): String {
    return try {
        String(Base64.getUrlDecoder().decode(input), charset(charset))
    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
        ""
    }
}
