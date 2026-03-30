package com.inuker.bluetooth.library.receiver

import com.inuker.bluetooth.library.receiver.listener.BluetoothReceiverListener

interface IReceiverDispatcher {
    fun getListeners(clazz: Class<*>): List<BluetoothReceiverListener>?
}
