package com.inuker.bluetooth.library.beacon

import com.inuker.bluetooth.library.utils.ByteUtils

class BeaconItem {
    var bytes: ByteArray = ByteUtils.EMPTY_BYTES
    var len: Int = 0
    var type: Int = 0

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append(String.format("@Len = %02X, @Type = 0x%02X", len, type))
        val format = if (type == 8 || type == 9) "%c" else "%02X "
        builder.append(" -> ")
        val payload = StringBuilder()
        try {
            for (value in bytes) {
                payload.append(String.format(format, value.toInt() and 0xFF))
            }
            builder.append(payload)
        } catch (_: Exception) {
            builder.append(ByteUtils.byteToString(bytes))
        }
        return builder.toString()
    }
}
