package com.inuker.bluetooth.library.connect.listener

import android.bluetooth.BluetoothGattDescriptor

interface ReadDescriptorListener : GattResponseListener {
    fun onDescriptorRead(descriptor: BluetoothGattDescriptor, status: Int, value: ByteArray)
}
