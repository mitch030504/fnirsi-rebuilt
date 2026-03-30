package com.inuker.bluetooth.library.connect.listener

interface ReadRssiListener : GattResponseListener {
    fun onReadRemoteRssi(rssi: Int, status: Int)
}
