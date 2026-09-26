package com.inuker.bluetooth.library.beacon

import com.inuker.bluetooth.library.utils.ByteUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder

class BeaconParser(bytes: ByteArray) {
    private val byteBuffer: ByteBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)

    constructor(beaconItem: BeaconItem) : this(beaconItem.bytes)

    fun getBit(value: Int, bit: Int): Boolean = value and (1 shl bit) != 0

    fun setPosition(position: Int) {
        byteBuffer.position(position)
    }

    fun readByte(): Int = byteBuffer.get().toInt() and 0xFF

    fun readShort(): Int = byteBuffer.short.toInt() and 0xFFFF

    companion object {
        @JvmStatic
        fun parseBeacon(bytes: ByteArray): List<BeaconItem> {
            val items = ArrayList<BeaconItem>()
            var offset = 0
            while (offset < bytes.size) {
                val item = parse(bytes, offset) ?: break
                items.add(item)
                offset += item.len + 1
            }
            return items
        }

        private fun parse(bytes: ByteArray, offset: Int): BeaconItem? {
            if (bytes.size - offset < 2) {
                return null
            }
            val length = bytes[offset].toInt()
            if (length <= 0) {
                return null
            }
            val type = bytes[offset + 1].toInt() and 0xFF
            val dataStart = offset + 2
            if (dataStart >= bytes.size) {
                return null
            }
            var dataEnd = dataStart + length - 2
            if (dataEnd >= bytes.size) {
                dataEnd = bytes.size - 1
            }
            return BeaconItem().apply {
                this.type = type
                len = length
                this.bytes = ByteUtils.getBytes(bytes, dataStart, dataEnd) ?: ByteUtils.EMPTY_BYTES
            }
        }
    }
}
