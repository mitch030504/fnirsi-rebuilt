package com.inuker.bluetooth.library.receiver.listener

abstract class BluetoothBondListener : BluetoothClientListener() {
    abstract fun onBondStateChanged(address: String?, bondState: Int)

    override fun onSyncInvoke(vararg objArr: Any?) {
        onBondStateChanged(
            objArr.getOrNull(0) as? String,
            (objArr.getOrNull(1) as? Int) ?: 0,
        )
    }
}
