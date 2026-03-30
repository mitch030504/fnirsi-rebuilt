package com.inuker.bluetooth.library.receiver.listener

import android.bluetooth.BluetoothAdapter
import com.inuker.bluetooth.library.BluetoothClientImpl

abstract class BluetoothStateChangeListener : BluetoothReceiverListener() {
    protected abstract fun onBluetoothStateChanged(previousState: Int, state: Int)

    override fun onInvoke(vararg objArr: Any?) {
        val previousState = (objArr.getOrNull(0) as? Int) ?: 0
        val state = (objArr.getOrNull(1) as? Int) ?: 0
        if (state == BluetoothAdapter.STATE_OFF || state == BluetoothAdapter.STATE_TURNING_OFF) {
            BluetoothClientImpl.getInstance(null).stopSearch()
        }
        onBluetoothStateChanged(previousState, state)
    }

    override fun getName(): String = BluetoothStateChangeListener::class.java.simpleName
}
