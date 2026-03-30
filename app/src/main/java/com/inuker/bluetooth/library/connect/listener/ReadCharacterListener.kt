package com.inuker.bluetooth.library.connect.listener

import android.bluetooth.BluetoothGattCharacteristic

interface ReadCharacterListener : GattResponseListener {
    fun onCharacteristicRead(characteristic: BluetoothGattCharacteristic, status: Int, value: ByteArray)
}
