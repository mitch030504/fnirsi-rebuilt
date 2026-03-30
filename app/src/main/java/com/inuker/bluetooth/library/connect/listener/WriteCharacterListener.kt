package com.inuker.bluetooth.library.connect.listener

import android.bluetooth.BluetoothGattCharacteristic

interface WriteCharacterListener : GattResponseListener {
    fun onCharacteristicWrite(characteristic: BluetoothGattCharacteristic, status: Int, value: ByteArray)
}
