package com.inuker.bluetooth.library.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.inuker.bluetooth.library.receiver.listener.BluetoothReceiverListener
import com.inuker.bluetooth.library.utils.BluetoothLog
import com.inuker.bluetooth.library.utils.BluetoothUtils

class BluetoothReceiver private constructor() : BroadcastReceiver(), IBluetoothReceiver, Handler.Callback {
    private val listeners = HashMap<String, MutableList<BluetoothReceiverListener>>()
    private val dispatcher =
        object : IReceiverDispatcher {
            override fun getListeners(clazz: Class<*>): List<BluetoothReceiverListener>? {
                return listeners[clazz.simpleName]
            }
        }
    private val receivers =
        arrayOf(
            BluetoothStateReceiver.newInstance(dispatcher),
            BluetoothBondReceiver.newInstance(dispatcher),
            BleConnectStatusChangeReceiver.newInstance(dispatcher),
            BleCharacterChangeReceiver.newInstance(dispatcher),
        )
    private val handler = Handler(Looper.getMainLooper(), this)

    init {
        BluetoothUtils.registerReceiver(this, getIntentFilter())
    }

    private fun getIntentFilter(): IntentFilter {
        return IntentFilter().also { filter ->
            receivers.forEach { receiver ->
                receiver.getActions().forEach(filter::addAction)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) {
            return
        }
        val action = intent.action
        if (action.isNullOrEmpty()) {
            return
        }
        BluetoothLog.v(String.format("BluetoothReceiver onReceive: %s", action))
        for (receiver in receivers) {
            if (receiver.containsAction(action) && receiver.onReceive(context, intent)) {
                return
            }
        }
    }

    override fun register(bluetoothReceiverListener: BluetoothReceiverListener?) {
        handler.obtainMessage(MSG_REGISTER, bluetoothReceiverListener).sendToTarget()
    }

    private fun registerInner(bluetoothReceiverListener: BluetoothReceiverListener?) {
        if (bluetoothReceiverListener == null) {
            return
        }
        val receiverListeners =
            listeners.getOrPut(bluetoothReceiverListener.getName()) {
                ArrayList()
            }
        receiverListeners.add(bluetoothReceiverListener)
    }

    override fun handleMessage(message: Message): Boolean {
        if (message.what == MSG_REGISTER) {
            registerInner(message.obj as? BluetoothReceiverListener)
        }
        return true
    }

    companion object {
        private const val MSG_REGISTER = 1

        @Volatile
        private var receiver: IBluetoothReceiver? = null

        @JvmStatic
        fun getInstance(): IBluetoothReceiver {
            val existing = receiver
            if (existing != null) {
                return existing
            }
            return synchronized(BluetoothReceiver::class.java) {
                receiver ?: BluetoothReceiver().also { receiver = it }
            }
        }
    }
}
