package com.inuker.bluetooth.library.receiver

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.inuker.bluetooth.library.BluetoothContext
import com.inuker.bluetooth.library.receiver.listener.BluetoothReceiverListener

abstract class AbsBluetoothReceiver(
    protected val dispatcher: IReceiverDispatcher,
) {
    protected val context: Context = BluetoothContext.get()
    protected val handler: Handler = Handler(Looper.getMainLooper())

    internal abstract fun getActions(): List<String>

    internal abstract fun onReceive(context: Context, intent: Intent): Boolean

    internal fun containsAction(action: String?): Boolean {
        if (action.isNullOrEmpty()) {
            return false
        }
        val actions = getActions()
        return actions.isNotEmpty() && actions.contains(action)
    }

    protected fun getListeners(clazz: Class<*>): List<BluetoothReceiverListener> {
        return dispatcher.getListeners(clazz) ?: emptyList()
    }
}
