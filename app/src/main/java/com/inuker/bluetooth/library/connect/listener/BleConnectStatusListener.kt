package com.inuker.bluetooth.library.connect.listener

import com.inuker.bluetooth.library.receiver.listener.BluetoothClientListener

abstract class BleConnectStatusListener : BluetoothClientListener() {
    abstract fun onConnectStatusChanged(address: String, status: Int)

    override fun onSyncInvoke(vararg args: Any?) {
        onConnectStatusChanged(args[0] as String, (args[1] as Int))
    }
}
