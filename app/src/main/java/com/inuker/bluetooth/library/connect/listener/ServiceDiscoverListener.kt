package com.inuker.bluetooth.library.connect.listener

import com.inuker.bluetooth.library.model.BleGattProfile

interface ServiceDiscoverListener : GattResponseListener {
    fun onServicesDiscovered(status: Int, bleGattProfile: BleGattProfile)
}
