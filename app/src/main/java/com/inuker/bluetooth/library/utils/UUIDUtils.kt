package com.inuker.bluetooth.library.utils

import java.util.UUID

object UUIDUtils {
    const val UUID_FORMAT = "0000%04x-0000-1000-8000-00805f9b34fb"

    @JvmStatic
    fun makeUUID(value: Int): UUID = UUID.fromString(String.format(UUID_FORMAT, value))

    @JvmStatic
    fun getValue(uuid: UUID): Int = (uuid.mostSignificantBits ushr 32).toInt()
}
