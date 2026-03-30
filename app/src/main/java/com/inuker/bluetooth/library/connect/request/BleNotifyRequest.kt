package com.inuker.bluetooth.library.connect.request

import android.bluetooth.BluetoothGattDescriptor
import com.inuker.bluetooth.library.Constants
import com.inuker.bluetooth.library.connect.listener.WriteDescriptorListener
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse
import java.util.UUID

class BleNotifyRequest(
    private val serviceUUID: UUID?,
    private val characterUUID: UUID?,
    bleGeneralResponse: BleGeneralResponse?,
) : BleRequest(bleGeneralResponse), WriteDescriptorListener {
    override fun processRequest() {
        when (getCurrentStatus()) {
            Constants.STATUS_DEVICE_DISCONNECTED -> onRequestCompleted(Constants.REQUEST_FAILED)
            Constants.STATUS_DEVICE_CONNECTED, Constants.STATUS_DEVICE_SERVICE_READY -> openNotify()
            else -> onRequestCompleted(Constants.REQUEST_FAILED)
        }
    }

    private fun openNotify() {
        if (!setCharacteristicNotification(serviceUUID, characterUUID, true)) {
            onRequestCompleted(Constants.REQUEST_FAILED)
        } else {
            startRequestTiming()
        }
    }

    override fun onDescriptorWrite(descriptor: BluetoothGattDescriptor, status: Int) {
        stopRequestTiming()
        onRequestCompleted(if (status == Constants.REQUEST_SUCCESS) Constants.REQUEST_SUCCESS else Constants.REQUEST_FAILED)
    }
}
