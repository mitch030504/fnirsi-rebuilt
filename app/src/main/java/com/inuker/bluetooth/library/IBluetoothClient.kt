package com.inuker.bluetooth.library

import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener
import com.inuker.bluetooth.library.connect.options.BleConnectOptions
import com.inuker.bluetooth.library.connect.response.BleConnectResponse
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse
import com.inuker.bluetooth.library.connect.response.BleReadResponse
import com.inuker.bluetooth.library.connect.response.BleReadRssiResponse
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse
import com.inuker.bluetooth.library.connect.response.BleWriteResponse
import com.inuker.bluetooth.library.receiver.listener.BluetoothBondListener
import com.inuker.bluetooth.library.search.SearchRequest
import com.inuker.bluetooth.library.search.response.SearchResponse
import java.util.UUID

interface IBluetoothClient {
    fun clearRequest(address: String?, type: Int)

    fun connect(address: String?, bleConnectOptions: BleConnectOptions?, bleConnectResponse: BleConnectResponse?)

    fun disconnect(address: String?)

    fun indicate(address: String?, service: UUID?, character: UUID?, bleNotifyResponse: BleNotifyResponse?)

    fun notify(address: String?, service: UUID?, character: UUID?, bleNotifyResponse: BleNotifyResponse?)

    fun read(address: String?, service: UUID?, character: UUID?, bleReadResponse: BleReadResponse?)

    fun readDescriptor(
        address: String?,
        service: UUID?,
        character: UUID?,
        descriptor: UUID?,
        bleReadResponse: BleReadResponse?,
    )

    fun readRssi(address: String?, bleReadRssiResponse: BleReadRssiResponse?)

    fun refreshCache(address: String?)

    fun registerBluetoothBondListener(bluetoothBondListener: BluetoothBondListener?)

    fun registerBluetoothStateListener(bluetoothStateListener: BluetoothStateListener?)

    fun registerConnectStatusListener(address: String?, bleConnectStatusListener: BleConnectStatusListener?)

    fun search(searchRequest: SearchRequest?, searchResponse: SearchResponse?)

    fun stopSearch()

    fun unindicate(address: String?, service: UUID?, character: UUID?, bleUnnotifyResponse: BleUnnotifyResponse?)

    fun unnotify(address: String?, service: UUID?, character: UUID?, bleUnnotifyResponse: BleUnnotifyResponse?)

    fun unregisterBluetoothBondListener(bluetoothBondListener: BluetoothBondListener?)

    fun unregisterBluetoothStateListener(bluetoothStateListener: BluetoothStateListener?)

    fun unregisterConnectStatusListener(address: String?, bleConnectStatusListener: BleConnectStatusListener?)

    fun write(address: String?, service: UUID?, character: UUID?, value: ByteArray?, bleWriteResponse: BleWriteResponse?)

    fun writeDescriptor(
        address: String?,
        service: UUID?,
        character: UUID?,
        descriptor: UUID?,
        value: ByteArray?,
        bleWriteResponse: BleWriteResponse?,
    )

    fun writeNoRsp(address: String?, service: UUID?, character: UUID?, value: ByteArray?, bleWriteResponse: BleWriteResponse?)
}
