package com.inuker.bluetooth.library.connect.response

import java.util.UUID

interface BleNotifyResponse : BleResponse {
    fun onNotify(service: UUID, character: UUID, value: ByteArray)
}
