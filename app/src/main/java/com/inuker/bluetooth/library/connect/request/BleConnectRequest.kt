package com.inuker.bluetooth.library.connect.request

import android.os.Message
import com.inuker.bluetooth.library.Constants
import com.inuker.bluetooth.library.connect.listener.ServiceDiscoverListener
import com.inuker.bluetooth.library.connect.options.BleConnectOptions
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse
import com.inuker.bluetooth.library.model.BleGattProfile
import com.inuker.bluetooth.library.utils.BluetoothLog

class BleConnectRequest(
    bleConnectOptions: BleConnectOptions?,
    bleGeneralResponse: BleGeneralResponse?,
) : BleRequest(bleGeneralResponse), ServiceDiscoverListener {
    private var connectCount = 0
    private val connectOptions = bleConnectOptions ?: BleConnectOptions.Builder().build()
    private var serviceDiscoverCount = 0

    override fun processRequest() {
        processConnect()
    }

    private fun processConnect() {
        handler.removeCallbacksAndMessages(null)
        serviceDiscoverCount = 0
        when (getCurrentStatus()) {
            Constants.STATUS_DEVICE_DISCONNECTED -> {
                if (!doOpenNewGatt()) {
                    closeGatt()
                } else {
                    handler.sendEmptyMessageDelayed(MSG_CONNECT_TIMEOUT, connectOptions.getConnectTimeout().toLong())
                }
            }

            Constants.STATUS_DEVICE_CONNECTED -> processDiscoverService()
            Constants.STATUS_DEVICE_SERVICE_READY -> onConnectSuccess()
        }
    }

    private fun doOpenNewGatt(): Boolean {
        connectCount++
        return openGatt()
    }

    private fun doDiscoverService(): Boolean {
        serviceDiscoverCount++
        return discoverService()
    }

    private fun retryConnectIfNeeded() {
        if (connectCount < connectOptions.getConnectRetry() + 1) {
            retryConnectLater()
        } else {
            onRequestCompleted(Constants.REQUEST_FAILED)
        }
    }

    private fun retryDiscoverServiceIfNeeded() {
        if (serviceDiscoverCount < connectOptions.getServiceDiscoverRetry() + 1) {
            retryDiscoverServiceLater()
        } else {
            closeGatt()
        }
    }

    private fun onServiceDiscoverFailed() {
        BluetoothLog.v("onServiceDiscoverFailed")
        refreshDeviceCache()
        handler.sendEmptyMessage(MSG_RETRY_DISCOVER_SERVICE)
    }

    private fun processDiscoverService() {
        BluetoothLog.v(String.format("processDiscoverService, status = %s", getStatusText()))
        when (getCurrentStatus()) {
            Constants.STATUS_DEVICE_DISCONNECTED -> retryConnectIfNeeded()
            Constants.STATUS_DEVICE_CONNECTED -> {
                if (!doDiscoverService()) {
                    onServiceDiscoverFailed()
                } else {
                    handler.sendEmptyMessageDelayed(
                        MSG_DISCOVER_SERVICE_TIMEOUT,
                        connectOptions.getServiceDiscoverTimeout().toLong(),
                    )
                }
            }

            Constants.STATUS_DEVICE_SERVICE_READY -> onConnectSuccess()
        }
    }

    private fun retryConnectLater() {
        log("retry connect later")
        handler.removeCallbacksAndMessages(null)
        handler.sendEmptyMessageDelayed(MSG_CONNECT, 1_000L)
    }

    private fun retryDiscoverServiceLater() {
        log("retry discover service later")
        handler.removeCallbacksAndMessages(null)
        handler.sendEmptyMessageDelayed(MSG_DISCOVER_SERVICE, 1_000L)
    }

    private fun processConnectTimeout() {
        log("connect timeout")
        handler.removeCallbacksAndMessages(null)
        closeGatt()
    }

    private fun processDiscoverServiceTimeout() {
        log("service discover timeout")
        handler.removeCallbacksAndMessages(null)
        closeGatt()
    }

    override fun handleMessage(message: Message): Boolean {
        when (message.what) {
            MSG_CONNECT -> processConnect()
            MSG_DISCOVER_SERVICE -> processDiscoverService()
            MSG_CONNECT_TIMEOUT -> processConnectTimeout()
            MSG_DISCOVER_SERVICE_TIMEOUT -> processDiscoverServiceTimeout()
            MSG_RETRY_DISCOVER_SERVICE -> retryDiscoverServiceIfNeeded()
        }
        return super.handleMessage(message)
    }

    override fun toString(): String = "BleConnectRequest{options=$connectOptions}"

    override fun onConnectStatusChanged(connected: Boolean) {
        checkRuntime()
        handler.removeMessages(MSG_CONNECT_TIMEOUT)
        if (connected) {
            handler.sendEmptyMessageDelayed(MSG_DISCOVER_SERVICE, 300L)
        } else {
            handler.removeCallbacksAndMessages(null)
            retryConnectIfNeeded()
        }
    }

    override fun onServicesDiscovered(status: Int, bleGattProfile: BleGattProfile) {
        checkRuntime()
        handler.removeMessages(MSG_DISCOVER_SERVICE_TIMEOUT)
        if (status == Constants.REQUEST_SUCCESS) {
            onConnectSuccess()
        } else {
            onServiceDiscoverFailed()
        }
    }

    private fun onConnectSuccess() {
        getGattProfile()?.let { putParcelable(Constants.EXTRA_GATT_PROFILE, it) }
        onRequestCompleted(Constants.REQUEST_SUCCESS)
    }

    private companion object {
        const val MSG_CONNECT = 1
        const val MSG_DISCOVER_SERVICE = 2
        const val MSG_CONNECT_TIMEOUT = 3
        const val MSG_DISCOVER_SERVICE_TIMEOUT = 4
        const val MSG_RETRY_DISCOVER_SERVICE = 5
    }
}
