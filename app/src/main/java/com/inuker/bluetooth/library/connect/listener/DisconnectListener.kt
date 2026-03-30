package com.inuker.bluetooth.library.connect.listener

interface DisconnectListener : GattResponseListener {
    fun onDisconnected()
}
