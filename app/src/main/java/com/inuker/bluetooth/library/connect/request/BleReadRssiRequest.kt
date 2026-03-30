package com.inuker.bluetooth.library.connect.request

import com.inuker.bluetooth.library.Constants
import com.inuker.bluetooth.library.connect.listener.ReadRssiListener
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse

class BleReadRssiRequest(
    bleGeneralResponse: BleGeneralResponse?,
) : BleRequest(bleGeneralResponse), ReadRssiListener {
    override fun processRequest() {
        when (getCurrentStatus()) {
            Constants.STATUS_DEVICE_DISCONNECTED -> onRequestCompleted(Constants.REQUEST_FAILED)
            Constants.STATUS_DEVICE_CONNECTED, Constants.STATUS_DEVICE_SERVICE_READY -> startReadRssi()
            else -> onRequestCompleted(Constants.REQUEST_FAILED)
        }
    }

    private fun startReadRssi() {
        if (!readRemoteRssi()) {
            onRequestCompleted(Constants.REQUEST_FAILED)
        } else {
            startRequestTiming()
        }
    }

    override fun onReadRemoteRssi(rssi: Int, status: Int) {
        stopRequestTiming()
        if (status == Constants.REQUEST_SUCCESS) {
            putIntExtra(Constants.EXTRA_RSSI, rssi)
            onRequestCompleted(Constants.REQUEST_SUCCESS)
        } else {
            onRequestCompleted(Constants.REQUEST_FAILED)
        }
    }
}
