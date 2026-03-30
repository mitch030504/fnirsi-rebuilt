package com.inuker.bluetooth.library.connect

import com.inuker.bluetooth.library.connect.listener.GattResponseListener
import com.inuker.bluetooth.library.model.BleGattProfile
import java.util.UUID

interface IBleConnectWorker {
    fun clearGattResponseListener(gattResponseListener: GattResponseListener?)

    fun closeGatt()

    fun discoverService(): Boolean

    fun getCurrentStatus(): Int

    fun getGattProfile(): BleGattProfile?

    fun openGatt(): Boolean

    fun readCharacteristic(service: UUID?, character: UUID?): Boolean

    fun readDescriptor(service: UUID?, character: UUID?, descriptor: UUID?): Boolean

    fun readRemoteRssi(): Boolean

    fun refreshDeviceCache(): Boolean

    fun registerGattResponseListener(gattResponseListener: GattResponseListener?)

    fun setCharacteristicIndication(service: UUID?, character: UUID?, enable: Boolean): Boolean

    fun setCharacteristicNotification(service: UUID?, character: UUID?, enable: Boolean): Boolean

    fun writeCharacteristic(service: UUID?, character: UUID?, value: ByteArray?): Boolean

    fun writeCharacteristicWithNoRsp(service: UUID?, character: UUID?, value: ByteArray?): Boolean

    fun writeDescriptor(service: UUID?, character: UUID?, descriptor: UUID?, value: ByteArray?): Boolean
}
