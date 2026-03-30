package com.inuker.bluetooth.library.beacon

import com.inuker.bluetooth.library.utils.ByteUtils
import java.util.LinkedList

class Beacon(bytes: ByteArray?) {
    @JvmField
    var mBytes: ByteArray? = null

    @JvmField
    var mItems: MutableList<BeaconItem> = LinkedList()

    init {
        if (!ByteUtils.isEmpty(bytes)) {
            val trimmed = ByteUtils.trimLast(bytes!!)
            mBytes = trimmed
            mItems.addAll(BeaconParser.parseBeacon(trimmed))
        }
    }

    override fun toString(): String {
        val parsed = mItems.joinToString("\n")
        return String.format("preParse: %s\npostParse:\n%s", ByteUtils.byteToString(mBytes), parsed)
    }
}
