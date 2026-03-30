package com.inuker.bluetooth.library.receiver

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import androidx.core.os.EnvironmentCompat
import com.inuker.bluetooth.library.receiver.listener.BluetoothStateChangeListener
import com.inuker.bluetooth.library.utils.BluetoothLog

class BluetoothStateReceiver(
    dispatcher: IReceiverDispatcher,
) : AbsBluetoothReceiver(dispatcher) {
    override fun getActions(): List<String> = listOf(BluetoothAdapter.ACTION_STATE_CHANGED)

    override fun onReceive(context: Context, intent: Intent): Boolean {
        val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)
        val previousState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0)
        BluetoothLog.v(String.format("state changed: %s -> %s", getStateString(previousState), getStateString(state)))
        onBluetoothStateChanged(previousState, state)
        return true
    }

    private fun getStateString(state: Int): String {
        return when (state) {
            BluetoothAdapter.STATE_OFF -> "state_off"
            BluetoothAdapter.STATE_TURNING_ON -> "state_turning_on"
            BluetoothAdapter.STATE_ON -> "state_on"
            BluetoothAdapter.STATE_TURNING_OFF -> "state_turning_off"
            else -> EnvironmentCompat.MEDIA_UNKNOWN
        }
    }

    private fun onBluetoothStateChanged(previousState: Int, state: Int) {
        getListeners(BluetoothStateChangeListener::class.java).forEach {
            it.invoke(previousState, state)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(dispatcher: IReceiverDispatcher): BluetoothStateReceiver {
            return BluetoothStateReceiver(dispatcher)
        }
    }
}
