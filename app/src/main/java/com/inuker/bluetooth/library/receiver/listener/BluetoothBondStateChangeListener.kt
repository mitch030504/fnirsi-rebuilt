package com.inuker.bluetooth.library.receiver.listener

abstract class BluetoothBondStateChangeListener : BluetoothReceiverListener() {
    protected abstract fun onBondStateChanged(address: String?, bondState: Int)

    override fun onInvoke(vararg objArr: Any?) {
        onBondStateChanged(
            objArr.getOrNull(0) as? String,
            (objArr.getOrNull(1) as? Int) ?: 0,
        )
    }

    override fun getName(): String = BluetoothBondStateChangeListener::class.java.simpleName
}
