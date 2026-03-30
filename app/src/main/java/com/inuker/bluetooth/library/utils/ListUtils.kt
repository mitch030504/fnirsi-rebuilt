package com.inuker.bluetooth.library.utils

object ListUtils {
    @JvmStatic
    fun isEmpty(list: List<*>?): Boolean = list == null || list.isEmpty()

    @JvmStatic
    fun <E> getEmptyList(): List<E> = ArrayList()
}
