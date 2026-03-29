package com.uct.entity

import com.uct.utlis.byteToHexString

class CmdEntity(
    var cmd: Byte,
    var date: ByteArray,
) {
    override fun toString(): String {
        return "CmdEntity{cmd=" + byteToHexString(byteArrayOf(cmd)) + ", date=" + byteToHexString(date) + '}'
    }
}
