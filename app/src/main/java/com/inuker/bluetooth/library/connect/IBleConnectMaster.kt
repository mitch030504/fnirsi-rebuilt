package com.inuker.bluetooth.library.connect

import com.inuker.bluetooth.library.connect.options.BleConnectOptions
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse
import java.util.UUID

interface IBleConnectMaster {
    fun clearRequest(type: Int)

    fun connect(options: BleConnectOptions?, response: BleGeneralResponse?)

    fun disconnect()

    fun indicate(service: UUID?, character: UUID?, response: BleGeneralResponse?)

    fun notify(service: UUID?, character: UUID?, response: BleGeneralResponse?)

    fun read(service: UUID?, character: UUID?, response: BleGeneralResponse?)

    fun readDescriptor(service: UUID?, character: UUID?, descriptor: UUID?, response: BleGeneralResponse?)

    fun readRssi(response: BleGeneralResponse?)

    fun refreshCache()

    fun unnotify(service: UUID?, character: UUID?, response: BleGeneralResponse?)

    fun write(service: UUID?, character: UUID?, value: ByteArray?, response: BleGeneralResponse?)

    fun writeDescriptor(service: UUID?, character: UUID?, descriptor: UUID?, value: ByteArray?, response: BleGeneralResponse?)

    fun writeNoRsp(service: UUID?, character: UUID?, value: ByteArray?, response: BleGeneralResponse?)
}
