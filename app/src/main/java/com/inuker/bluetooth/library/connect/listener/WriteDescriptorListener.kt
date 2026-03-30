package com.inuker.bluetooth.library.connect.listener

import android.bluetooth.BluetoothGattDescriptor

interface WriteDescriptorListener : GattResponseListener {
    fun onDescriptorWrite(descriptor: BluetoothGattDescriptor, status: Int)
}
