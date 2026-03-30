package com.inuker.bluetooth.library.connect.listener

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor

interface IBluetoothGattResponse {
    fun onCharacteristicChanged(characteristic: BluetoothGattCharacteristic, value: ByteArray)

    fun onCharacteristicRead(characteristic: BluetoothGattCharacteristic, status: Int, value: ByteArray)

    fun onCharacteristicWrite(characteristic: BluetoothGattCharacteristic, status: Int, value: ByteArray)

    fun onConnectionStateChange(status: Int, newState: Int)

    fun onDescriptorRead(descriptor: BluetoothGattDescriptor, status: Int, value: ByteArray)

    fun onDescriptorWrite(descriptor: BluetoothGattDescriptor, status: Int)

    fun onReadRemoteRssi(rssi: Int, status: Int)

    fun onServicesDiscovered(status: Int)
}
