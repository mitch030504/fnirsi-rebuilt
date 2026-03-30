package com.inuker.bluetooth.library.utils

import java.util.Arrays

object ByteUtils {
    const val BYTE_MAX = 255

    @JvmField
    val EMPTY_BYTES: ByteArray = byteArrayOf()

    @JvmStatic
    fun ubyteToInt(value: Byte): Int = value.toInt() and BYTE_MAX

    @JvmStatic
    fun getNonEmptyByte(value: ByteArray?): ByteArray = value ?: EMPTY_BYTES

    @JvmStatic
    fun byteToString(value: ByteArray?): String {
        val builder = StringBuilder()
        if (!isEmpty(value)) {
            value!!.forEach { builder.append(String.format("%02X", it)) }
        }
        return builder.toString()
    }

    @JvmStatic
    fun trimLast(value: ByteArray): ByteArray {
        var index = value.size - 1
        while (index >= 0 && value[index].toInt() == 0) {
            index--
        }
        return Arrays.copyOfRange(value, 0, index + 1)
    }

    @JvmStatic
    fun stringToBytes(value: String): ByteArray {
        val result = ByteArray((value.length + 1) / 2)
        var index = 0
        while (index < value.length) {
            val end = index + minOf(2, value.length - index)
            result[index / 2] = value.substring(index, end).toInt(16).toByte()
            index += 2
        }
        return result
    }

    @JvmStatic
    fun isEmpty(value: ByteArray?): Boolean = value == null || value.isEmpty()

    @JvmStatic
    fun fromInt(value: Int): ByteArray = ByteArray(4) { index -> (value ushr (index * 8)).toByte() }

    @JvmStatic
    fun byteEquals(first: ByteArray?, second: ByteArray?): Boolean {
        if (first == null && second == null) {
            return true
        }
        if (first == null || second == null || first.size != second.size) {
            return false
        }
        return first.indices.all { first[it] == second[it] }
    }

    @JvmStatic
    fun fillBeforeBytes(value: ByteArray?, targetLength: Int, fill: Byte): ByteArray? {
        val sourceLength = value?.size ?: 0
        if (sourceLength >= targetLength) {
            return value
        }
        val result = ByteArray(targetLength)
        var dest = targetLength - 1
        var source = sourceLength - 1
        while (dest >= 0) {
            result[dest] = if (source >= 0) value!![source--] else fill
            dest--
        }
        return result
    }

    @JvmStatic
    fun cutBeforeBytes(value: ByteArray?, cut: Byte): ByteArray? {
        if (isEmpty(value)) {
            return value
        }
        for (index in value!!.indices) {
            if (value[index] != cut) {
                return Arrays.copyOfRange(value, index, value.size)
            }
        }
        return EMPTY_BYTES
    }

    @JvmStatic
    fun cutAfterBytes(value: ByteArray?, cut: Byte): ByteArray? {
        if (isEmpty(value)) {
            return value
        }
        for (index in value!!.lastIndex downTo 0) {
            if (value[index] != cut) {
                return Arrays.copyOfRange(value, 0, index + 1)
            }
        }
        return EMPTY_BYTES
    }

    @JvmStatic
    fun getBytes(value: ByteArray?, start: Int, end: Int): ByteArray? {
        if (value == null) {
            return null
        }
        if (start < 0 || start >= value.size || end < 0 || end >= value.size || start > end) {
            return null
        }
        val result = ByteArray(end - start + 1)
        for (index in start..end) {
            result[index - start] = value[index]
        }
        return result
    }

    @JvmStatic
    fun isAllFF(value: ByteArray?): Boolean {
        val size = value?.size ?: 0
        for (index in 0 until size) {
            if (ubyteToInt(value!![index]) != 255) {
                return false
            }
        }
        return true
    }

    @JvmStatic
    fun fromLong(value: Long): ByteArray = ByteArray(8) { index -> (value ushr (index * 8)).toByte() }

    @JvmStatic
    fun copy(target: ByteArray?, source: ByteArray?, targetIndex: Int, sourceIndex: Int) {
        if (target == null || source == null || targetIndex < 0) {
            return
        }
        var t = targetIndex
        var s = sourceIndex
        while (s < source.size && t < target.size) {
            target[t++] = source[s++]
        }
    }
}
