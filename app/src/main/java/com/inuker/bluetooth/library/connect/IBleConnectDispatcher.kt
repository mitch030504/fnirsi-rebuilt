package com.inuker.bluetooth.library.connect

import com.inuker.bluetooth.library.connect.request.BleRequest

interface IBleConnectDispatcher {
    fun onRequestCompleted(bleRequest: BleRequest)
}
