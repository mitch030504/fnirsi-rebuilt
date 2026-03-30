package com.inuker.bluetooth.library

import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse
import com.inuker.bluetooth.library.receiver.listener.BluetoothBondListener

class BluetoothClientReceiver {
    var bluetoothBondListeners: List<BluetoothBondListener>? = null
    var bluetoothStateListeners: List<BluetoothStateListener>? = null
    var connectStatusListeners: HashMap<String, List<BleConnectStatusListener>>? = null
    var notifyResponses: HashMap<String, HashMap<String, List<BleNotifyResponse>>>? = null
}
