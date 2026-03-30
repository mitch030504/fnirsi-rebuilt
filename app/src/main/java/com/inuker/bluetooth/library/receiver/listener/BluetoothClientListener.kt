package com.inuker.bluetooth.library.receiver.listener

abstract class BluetoothClientListener : AbsBluetoothListener() {
    final override fun onInvoke(vararg objArr: Any?) {
        throw UnsupportedOperationException()
    }
}
