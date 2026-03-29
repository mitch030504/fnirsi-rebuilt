@file:JvmName("TextTools")

package com.uct.utlis

import androidx.core.view.MotionEventCompat

fun intToByte2(value: Int): ByteArray = byteArrayOf(((value shr 8) and 0xFF).toByte(), (value and 0xFF).toByte())

fun intToByte2LH(value: Int): ByteArray = byteArrayOf((value and 0xFF).toByte(), ((value shr 8) and 0xFF).toByte())

fun intToByte4(value: Int): ByteArray = byteArrayOf(
    ((value shr 24) and 0xFF).toByte(),
    ((value shr 16) and 0xFF).toByte(),
    ((value shr 8) and 0xFF).toByte(),
    (value and 0xFF).toByte(),
)

fun unsignedShortToByte2(value: Int): ByteArray = byteArrayOf(((value shr 8) and 0xFF).toByte(), (value and 0xFF).toByte())

fun isEmpty(value: String?): Boolean = value == null || value.trim().isEmpty()

fun clearSpace(value: String): String = value.replace(" ", "")

fun hexString2Bytes(value: String): ByteArray {
    val normalized = if (value.length % 2 == 0) value else "0$value"
    return ByteArray(normalized.length / 2) { index ->
        val offset = index * 2
        uniteBytes(normalized[offset].code.toByte(), normalized[offset + 1].code.toByte())
    }
}

fun uniteBytes(high: Byte, low: Byte): Byte {
    val highNibble = ("0x" + high.toInt().toChar()).toInt(16)
    val lowNibble = ("0x" + low.toInt().toChar()).toInt(16)
    return ((highNibble shl 4) xor lowNibble).toByte()
}

fun byteToHexString(bytes: ByteArray?): String {
    if (bytes == null) {
        return ""
    }
    return buildString(bytes.size * 2) {
        bytes.forEach { append("%02X".format(it.toInt() and 0xFF)) }
    }
}

fun byteToHexStringAddSpace(bytes: ByteArray?): String {
    if (bytes == null) {
        return ""
    }
    return buildString(bytes.size * 3) {
        bytes.forEach {
            append("%02X".format(it.toInt() and 0xFF))
            append(' ')
        }
    }
}

fun hexStringToByteArray(value: String): ByteArray {
    val normalized = if (value.length % 2 == 0) value else "0$value"
    return ByteArray(normalized.length / 2) { index ->
        val offset = index * 2
        ((chr2hex(normalized.substring(offset, offset + 1)).toInt() shl 4) +
            chr2hex(normalized.substring(offset + 1, offset + 2)).toInt()).toByte()
    }
}

fun chr2hex(value: String): Byte = value.toIntOrNull(16)?.toByte() ?: 0

@Synchronized
fun removeTail0(value: String?): String? = value?.replace(Regex("(0){1,}$"), "")

@Synchronized
fun removeHead0(value: String?): String? = value?.replaceFirst(Regex("^0+"), "")

@Synchronized
fun removeHeadTail0(value: String?): String? = value?.replace(Regex("(0){1,}$"), "")?.replaceFirst(Regex("^0+"), "")

fun padLeft(value: String, size: Int, pad: String): String {
    var result = value
    while (result.length < size) {
        result = pad + result
    }
    return result
}

fun PadRight(value: String, size: Int, pad: String): String {
    var result = value
    while (result.length < size) {
        result += pad
    }
    return result
}

fun byteToInt2(bytes: ByteArray): Int = byteToInt2z(byteArrayOf(bytes[1], bytes[0]))

fun byteToInt2z(bytes: ByteArray): Int {
    var result = 0
    bytes.forEach { result = (result shl 8) or (it.toInt() and 0xFF) }
    return result
}

fun longToByte8(value: Long): ByteArray = ByteArray(8) { index -> ((value ushr ((7 - index) * 8)) and 0xFF).toByte() }

fun byte2ToUnsignedShort(bytes: ByteArray): Int = byte2ToUnsignedShort(bytes, 0)

fun byte2ToUnsignedShort(bytes: ByteArray, offset: Int): Int {
    val high = bytes[offset]
    val low = bytes[offset + 1]
    return (low.toInt() and 0xFF) or ((high.toInt() shl 8) and MotionEventCompat.ACTION_POINTER_INDEX_MASK)
}

fun byte4ToInt(bytes: ByteArray, offset: Int): Int {
    return (bytes[offset + 3].toInt() and 0xFF) or
        ((bytes[offset].toInt() and 0xFF) shl 24) or
        ((bytes[offset + 1].toInt() and 0xFF) shl 16) or
        ((bytes[offset + 2].toInt() and 0xFF) shl 8)
}

fun bytesToInt(bytes: ByteArray, offset: Int): Int {
    return ((bytes[offset + 3].toInt() and 0xFF) shl 24) or
        (bytes[offset].toInt() and 0xFF) or
        ((bytes[offset + 1].toInt() and 0xFF) shl 8) or
        ((bytes[offset + 2].toInt() and 0xFF) shl 16)
}

fun byte2ToShort(bytes: ByteArray): Short {
    return (((bytes[1].toInt() and 0xFF) or ((bytes[0].toInt() and 0xFF) shl 8)) and 0xFFFF).toShort()
}

fun IsHex(value: String): Boolean = value.uppercase().all { it in '0'..'9' || it in 'A'..'F' }
