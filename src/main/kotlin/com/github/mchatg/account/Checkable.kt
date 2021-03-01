package com.github.mchatg.account


interface Checkable {

    fun isValid(): Boolean
    fun isInvalid() = !isValid()
//    fun isEquals()


    companion object {
        fun allValid(vararg objects: Checkable?): Boolean {
            for (obj in objects) {
                if (obj == null || obj.isInvalid())
                    return false
            }
            return true
        }

        fun hasInvalid(vararg objects: Checkable?): Boolean = !allValid(*objects)


    }
}


interface ValueComparable<T, V> : Checkable {

    val value: V?
    fun isEquals(other: ValueComparable<T, V>): Boolean {
        if (Checkable.hasInvalid(this, other))
            return false

        return this.value == other.value
    }

    //    fun equals(other: ValueComparable<T, V>): Boolean {
//        if (Checkable.hasInvalid(this, other))
//            return false
//        return this.value == other.value
//    }


    companion object {
        fun <T : ValueComparable<T, V>, V> isEquals(a: T, b: T): Boolean {
            if (Checkable.hasInvalid(a, b))
                return false

            return a.isEquals(b)
        }

        inline fun <T, reified V> toValueArrayList(list: Collection<ValueComparable<T, V>>): ArrayList<V> {
            val size = list.size
            val arrayList: ArrayList<V> = ArrayList(size)
            list.forEach {
                if (it.isValid())
                    arrayList.add(it.value!!)
            }

            return arrayList
        }


    }
}

inline class Account(override val value: String?) : Checkable, ValueComparable<Account, String> {
    override fun isValid(): Boolean = (!value.isNullOrBlank() && value.length < 16)


}

inline class TelegramUID(override val value: Int?) : Checkable, ValueComparable<TelegramUID, Int> {
    override fun isValid(): Boolean = (value != null && value != 0)

}

inline class Passwd(override val value: String?) : Checkable, ValueComparable<Passwd, String> {
    override fun isValid(): Boolean = (!value.isNullOrBlank()
//            && value.length != 64
            ) //格式限定,16进制编码sha256

}


inline class Player(override val value: String?) : Checkable, ValueComparable<Player, String> {
    override fun isValid(): Boolean = (!value.isNullOrBlank() && value.length < 16)

}

