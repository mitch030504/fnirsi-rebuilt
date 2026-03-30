package com.inuker.bluetooth.library.connect.listener

import com.inuker.bluetooth.library.receiver.listener.BluetoothClientListener

abstract class BluetoothStateListener : BluetoothClientListener() {
    abstract fun onBluetoothStateChanged(on: Boolean)

    override fun onSyncInvoke(vararg args: Any?) {
        onBluetoothStateChanged(args[0] as Boolean)
    }
}
