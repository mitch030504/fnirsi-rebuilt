package com.inuker.bluetooth.library.receiver

import android.content.Context
import android.content.Intent
import com.inuker.bluetooth.library.Constants
import com.inuker.bluetooth.library.receiver.listener.BleConnectStatusChangeListener
import com.inuker.bluetooth.library.utils.BluetoothLog

class BleConnectStatusChangeReceiver(
    dispatcher: IReceiverDispatcher,
) : AbsBluetoothReceiver(dispatcher) {
    override fun getActions(): List<String> = listOf(Constants.ACTION_CONNECT_STATUS_CHANGED)

    override fun onReceive(context: Context, intent: Intent): Boolean {
        val address = intent.getStringExtra(Constants.EXTRA_MAC)
        val status = intent.getIntExtra(Constants.EXTRA_STATUS, 0)
        BluetoothLog.v(String.format("onConnectStatusChanged for %s, status = %d", address, status))
        onConnectStatusChanged(address, status)
        return true
    }

    private fun onConnectStatusChanged(address: String?, status: Int) {
        getListeners(BleConnectStatusChangeListener::class.java).forEach {
            it.invoke(address, status)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(dispatcher: IReceiverDispatcher): BleConnectStatusChangeReceiver {
            return BleConnectStatusChangeReceiver(dispatcher)
        }
    }
}
