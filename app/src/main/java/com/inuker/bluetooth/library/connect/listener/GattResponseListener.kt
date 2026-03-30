package com.inuker.bluetooth.library.connect.listener

interface GattResponseListener {
    fun onConnectStatusChanged(connected: Boolean)

    companion object {
        @JvmField val GATT_RESP_SERVICE_DISCOVER = 1
        @JvmField val GATT_RESP_CHARACTER_READ = 2
        @JvmField val GATT_RESP_CHARACTER_WRITE = 3
        @JvmField val GATT_RESP_DESCRIPTOR_WRITE = 4
        @JvmField val GATT_RESP_READ_RSSI = 5
        @JvmField val GATT_RESP_DISCONNECT = 6
    }
}
