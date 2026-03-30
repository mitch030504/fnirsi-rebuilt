package com.inuker.bluetooth.library.connect.response

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import com.inuker.bluetooth.library.connect.listener.IBluetoothGattResponse

class BluetoothGattResponse(
    private val response: IBluetoothGattResponse,
) : BluetoothGattCallback() {
    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        response.onConnectionStateChange(status, newState)
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        response.onServicesDiscovered(status)
    }

    @Suppress("DEPRECATION")
    override fun onCharacteristicRead(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        status: Int,
    ) {
        response.onCharacteristicRead(characteristic, status, characteristic.value ?: ByteArray(0))
    }

    override fun onCharacteristicRead(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        value: ByteArray,
        status: Int,
    ) {
        response.onCharacteristicRead(characteristic, status, value)
    }

    @Suppress("DEPRECATION")
    override fun onCharacteristicWrite(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        status: Int,
    ) {
        response.onCharacteristicWrite(characteristic, status, characteristic.value ?: ByteArray(0))
    }

    @Suppress("DEPRECATION")
    override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        response.onCharacteristicChanged(characteristic, characteristic.value ?: ByteArray(0))
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        value: ByteArray,
    ) {
        response.onCharacteristicChanged(characteristic, value)
    }

    override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
        response.onDescriptorWrite(descriptor, status)
    }

    @Suppress("DEPRECATION")
    override fun onDescriptorRead(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
        response.onDescriptorRead(descriptor, status, descriptor.value ?: ByteArray(0))
    }

    override fun onDescriptorRead(
        gatt: BluetoothGatt,
        descriptor: BluetoothGattDescriptor,
        status: Int,
        value: ByteArray,
    ) {
        response.onDescriptorRead(descriptor, status, value)
    }

    override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int) {
        response.onReadRemoteRssi(rssi, status)
    }
}
