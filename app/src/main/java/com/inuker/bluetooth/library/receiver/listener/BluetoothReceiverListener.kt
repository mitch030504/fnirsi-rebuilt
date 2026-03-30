package com.inuker.bluetooth.library.receiver.listener

abstract class BluetoothReceiverListener : AbsBluetoothListener() {
    abstract fun getName(): String

    final override fun onSyncInvoke(vararg objArr: Any?) {
        throw UnsupportedOperationException()
    }
}
