package com.inuker.bluetooth.library.connect.request

import com.inuker.bluetooth.library.Constants
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse

class BleRefreshCacheRequest(
    bleGeneralResponse: BleGeneralResponse?,
) : BleRequest(bleGeneralResponse) {
    override fun processRequest() {
        refreshDeviceCache()
        handler.postDelayed(
            { onRequestCompleted(Constants.REQUEST_SUCCESS) },
            3_000L,
        )
    }
}
