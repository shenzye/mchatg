package com.github.mchatg.until

import java.util.*

class TimeMeter<T>(val field: Int, val amount: Int) {

    private val map: HashMap<T, Calendar> = HashMap<T, Calendar>()
    fun isTimeOut(obj: T?): Boolean {
        if (obj == null)
            return true
        val value = map[obj]

        return when {
            value == null -> true
            value < Calendar.getInstance() -> {
                map.remove(obj)
                true
            }
            else -> false
        }

    }

    fun push(obj: T?) {
        if (obj != null)
            map[obj] = (Calendar.getInstance().apply {
                add(field, amount)
            })
    }

    fun remove(obj: T?) {
        if (obj != null) {
            map.remove(obj)
        }
    }

    //GC
    fun clean(): ArrayList<T> {
        val list: ArrayList<T> = ArrayList()
        for ((key, value) in map) {
            if (
                value < Calendar.getInstance()
            ) {
                map.remove(key)
                list.add(key)
            }


        }
        return list

    }


}


