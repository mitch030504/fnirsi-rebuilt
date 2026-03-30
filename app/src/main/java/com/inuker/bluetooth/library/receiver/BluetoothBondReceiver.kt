package com.inuker.bluetooth.library.receiver

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import com.inuker.bluetooth.library.receiver.listener.BluetoothBondStateChangeListener

class BluetoothBondReceiver(
    dispatcher: IReceiverDispatcher,
) : AbsBluetoothReceiver(dispatcher) {
    override fun getActions(): List<String> = listOf(BluetoothDevice.ACTION_BOND_STATE_CHANGED)

    override fun onReceive(context: Context, intent: Intent): Boolean {
        @Suppress("DEPRECATION")
        val device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) as? BluetoothDevice
        val bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)
        if (device != null) {
            onBondStateChanged(device.address, bondState)
        }
        return true
    }

    private fun onBondStateChanged(address: String?, bondState: Int) {
        getListeners(BluetoothBondStateChangeListener::class.java).forEach {
            it.invoke(address, bondState)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(dispatcher: IReceiverDispatcher): BluetoothBondReceiver {
            return BluetoothBondReceiver(dispatcher)
        }
    }
}
