package com.inuker.bluetooth.library.connect.request

import android.bluetooth.BluetoothGattCharacteristic
import com.inuker.bluetooth.library.Constants
import com.inuker.bluetooth.library.connect.listener.ReadCharacterListener
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse
import java.util.UUID

class BleReadRequest(
    private val serviceUUID: UUID?,
    private val characterUUID: UUID?,
    bleGeneralResponse: BleGeneralResponse?,
) : BleRequest(bleGeneralResponse), ReadCharacterListener {
    override fun processRequest() {
        when (getCurrentStatus()) {
            Constants.STATUS_DEVICE_DISCONNECTED -> onRequestCompleted(Constants.REQUEST_FAILED)
            Constants.STATUS_DEVICE_CONNECTED, Constants.STATUS_DEVICE_SERVICE_READY -> startRead()
            else -> onRequestCompleted(Constants.REQUEST_FAILED)
        }
    }

    private fun startRead() {
        if (!readCharacteristic(serviceUUID, characterUUID)) {
            onRequestCompleted(Constants.REQUEST_FAILED)
        } else {
            startRequestTiming()
        }
    }

    override fun onCharacteristicRead(characteristic: BluetoothGattCharacteristic, status: Int, value: ByteArray) {
        stopRequestTiming()
        if (status == Constants.REQUEST_SUCCESS) {
            putByteArray(Constants.EXTRA_BYTE_VALUE, value)
            onRequestCompleted(Constants.REQUEST_SUCCESS)
        } else {
            onRequestCompleted(Constants.REQUEST_FAILED)
        }
    }
}
