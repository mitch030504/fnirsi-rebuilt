package com.uct.protocol

import com.uct.entity.CmdEntity
import com.uct.utlis.crcCheck
import com.uct.utlis.getDate

object FnirsiProtocol {
    private const val HEADER: Int = 0xAA
    private const val MIN_FRAME_SIZE: Int = 4

    @JvmStatic
    fun extractFrames(packet: ByteArray?): List<ByteArray> {
        if (packet == null || packet.isEmpty()) {
            return emptyList()
        }
        val frames = mutableListOf<ByteArray>()
        var index = 0
        while (index < packet.size) {
            if ((packet[index].toInt() and 0xFF) != HEADER) {
                index++
                continue
            }
            if (index + MIN_FRAME_SIZE > packet.size) {
                break
            }
            val payloadLength = packet[index + 2].toInt() and 0xFF
            val frameLength = payloadLength + MIN_FRAME_SIZE
            val frameEnd = index + frameLength
            if (frameEnd > packet.size) {
                break
            }
            val frame = packet.copyOfRange(index, frameEnd)
            if (crcCheck(frame)) {
                frames += frame
                index = frameEnd
            } else {
                index++
            }
        }
        return frames
    }

    @JvmStatic
    fun extractCommands(packet: ByteArray?): List<CmdEntity> {
        return extractFrames(packet).mapNotNull(::getDate)
    }
}
