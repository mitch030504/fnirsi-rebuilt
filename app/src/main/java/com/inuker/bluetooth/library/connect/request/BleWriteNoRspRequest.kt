package com.inuker.bluetooth.library.connect.request

import android.bluetooth.BluetoothGattCharacteristic
import com.inuker.bluetooth.library.Constants
import com.inuker.bluetooth.library.connect.listener.WriteCharacterListener
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse
import java.util.UUID

class BleWriteNoRspRequest(
    private val serviceUUID: UUID?,
    private val characterUUID: UUID?,
    private val bytes: ByteArray?,
    bleGeneralResponse: BleGeneralResponse?,
) : BleRequest(bleGeneralResponse), WriteCharacterListener {
    override fun processRequest() {
        when (getCurrentStatus()) {
            Constants.STATUS_DEVICE_DISCONNECTED -> onRequestCompleted(Constants.REQUEST_FAILED)
            Constants.STATUS_DEVICE_CONNECTED, Constants.STATUS_DEVICE_SERVICE_READY -> startWrite()
            else -> onRequestCompleted(Constants.REQUEST_FAILED)
        }
    }

    private fun startWrite() {
        if (!writeCharacteristicWithNoRsp(serviceUUID, characterUUID, bytes)) {
            onRequestCompleted(Constants.REQUEST_FAILED)
        } else {
            startRequestTiming()
        }
    }

    override fun onCharacteristicWrite(characteristic: BluetoothGattCharacteristic, status: Int, value: ByteArray) {
        stopRequestTiming()
        onRequestCompleted(if (status == Constants.REQUEST_SUCCESS) Constants.REQUEST_SUCCESS else Constants.REQUEST_FAILED)
    }
}
