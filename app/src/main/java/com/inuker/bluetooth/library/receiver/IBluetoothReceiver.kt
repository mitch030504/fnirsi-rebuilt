package com.inuker.bluetooth.library.receiver

import com.inuker.bluetooth.library.receiver.listener.BluetoothReceiverListener

interface IBluetoothReceiver {
    fun register(bluetoothReceiverListener: BluetoothReceiverListener?)
}
