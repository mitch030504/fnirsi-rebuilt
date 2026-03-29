@file:JvmName("CrcTool")

package com.uct.utlis

fun crcCheck(frame: ByteArray): Boolean {
    if (frame.isEmpty()) {
        return false
    }
    val crcSource = frame.copyOf(frame.size - 1)
    return getCrc(crcSource)[1] == frame.last()
}

fun getCrcLow(bytes: ByteArray): Byte = getCrc(bytes)[1]

fun getCrc(bytes: ByteArray): ByteArray = intToByte2(CRC16_XMODEM(bytes))

fun CRC16_XMODEM(bytes: ByteArray): Int {
    var crc = 0
    for (value in bytes) {
        for (bit in 0 until 8) {
            val dataBit = ((value.toInt() shr (7 - bit)) and 1) == 1
            val crcBit = ((crc shr 15) and 1) == 1
            crc = crc shl 1
            if (dataBit.xor(crcBit)) {
                crc = crc xor 0x1021
            }
        }
    }
    return crc and 0xFFFF
}
