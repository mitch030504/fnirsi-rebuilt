package com.inuker.bluetooth.library.receiver.listener

abstract class BleConnectStatusChangeListener : BluetoothReceiverListener() {
    protected abstract fun onConnectStatusChanged(address: String?, status: Int)

    override fun onInvoke(vararg objArr: Any?) {
        onConnectStatusChanged(
            objArr.getOrNull(0) as? String,
            (objArr.getOrNull(1) as? Int) ?: 0,
        )
    }

    override fun getName(): String = BleConnectStatusChangeListener::class.java.simpleName
}
