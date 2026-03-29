@file:JvmName("CmdUtli")

package com.uct.utlis

import com.uct.entity.CmdEntity

fun getCmd(cmd: Byte, payload: ByteArray?): ByteArray {
    val payloadSize = payload?.size ?: 0
    val frame = ByteArray(payloadSize + 4)
    frame[0] = 0xAA.toByte()
    frame[1] = cmd
    frame[2] = payloadSize.toByte()
    if (payload != null) {
        System.arraycopy(payload, 0, frame, 3, payload.size)
    }
    val crcSource = frame.copyOf(frame.size - 1)
    frame[frame.lastIndex] = getCrcLow(crcSource)
    return frame
}

fun getDate(frame: ByteArray?): CmdEntity? {
    if (frame == null || frame.size < 4) {
        return null
    }
    val payloadLength = frame[2].toInt() and 0xFF
    if (payloadLength + 4 > frame.size) {
        return null
    }
    val payload = frame.copyOfRange(3, 3 + payloadLength)
    return CmdEntity(frame[1], payload)
}
