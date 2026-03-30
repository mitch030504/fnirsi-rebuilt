package com.inuker.bluetooth.library.connect.request

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.Parcelable
import com.inuker.bluetooth.library.Constants
import com.inuker.bluetooth.library.RuntimeChecker
import com.inuker.bluetooth.library.connect.IBleConnectDispatcher
import com.inuker.bluetooth.library.connect.IBleConnectWorker
import com.inuker.bluetooth.library.connect.listener.GattResponseListener
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse
import com.inuker.bluetooth.library.model.BleGattProfile
import com.inuker.bluetooth.library.utils.BluetoothLog
import com.inuker.bluetooth.library.utils.BluetoothUtils
import java.util.UUID

abstract class BleRequest(
    protected val response: BleGeneralResponse?,
) : IBleConnectWorker, IBleRequest, Handler.Callback, GattResponseListener, RuntimeChecker {
    protected var addressInternal: String? = null
    protected lateinit var dispatcher: IBleConnectDispatcher
    private var finished = false
    protected var requestTimeout = false
    private var runtimeChecker: RuntimeChecker? = null
    protected lateinit var workerInternal: IBleConnectWorker
    protected val extraBundle = Bundle()
    protected val handler = Handler(Looper.myLooper() ?: throw IllegalStateException(), this)
    protected val responseHandler = Handler(Looper.getMainLooper())

    protected open fun getTimeoutInMillis(): Long = 30_000L

    abstract fun processRequest()

    fun getAddress(): String? = addressInternal

    fun setAddress(value: String?) {
        addressInternal = value
    }

    fun setWorker(value: IBleConnectWorker) {
        workerInternal = value
    }

    fun onResponse(code: Int) {
        if (finished) {
            return
        }
        finished = true
        responseHandler.post {
            try {
                response?.onResponse(code, extraBundle)
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            }
        }
    }

    override fun toString(): String = javaClass.simpleName

    fun putIntExtra(key: String, value: Int) {
        extraBundle.putInt(key, value)
    }

    fun getIntExtra(key: String, defaultValue: Int): Int = extraBundle.getInt(key, defaultValue)

    fun putByteArray(key: String, value: ByteArray?) {
        extraBundle.putByteArray(key, value)
    }

    fun putParcelable(key: String, value: Parcelable?) {
        extraBundle.putParcelable(key, value)
    }

    fun getExtra(): Bundle = extraBundle

    protected fun getStatusText(): String = Constants.getStatusText(getCurrentStatus())

    override fun readDescriptor(service: UUID?, character: UUID?, descriptor: UUID?): Boolean {
        return workerInternal.readDescriptor(service, character, descriptor)
    }

    override fun writeDescriptor(service: UUID?, character: UUID?, descriptor: UUID?, value: ByteArray?): Boolean {
        return workerInternal.writeDescriptor(service, character, descriptor, value)
    }

    override fun openGatt(): Boolean = workerInternal.openGatt()

    override fun discoverService(): Boolean = workerInternal.discoverService()

    override fun getCurrentStatus(): Int = workerInternal.getCurrentStatus()

    final override fun process(dispatcher: IBleConnectDispatcher) {
        checkRuntime()
        this.dispatcher = dispatcher
        BluetoothLog.w(String.format("Process %s, status = %s", javaClass.simpleName, getStatusText()))
        if (!BluetoothUtils.isBleSupported()) {
            onRequestCompleted(Constants.BLE_NOT_SUPPORTED)
            return
        }
        if (!BluetoothUtils.isBluetoothEnabled()) {
            onRequestCompleted(Constants.BLUETOOTH_DISABLED)
            return
        }
        try {
            registerGattResponseListener(this)
            processRequest()
        } catch (throwable: Throwable) {
            BluetoothLog.e(throwable)
            onRequestCompleted(Constants.REQUEST_EXCEPTION)
        }
    }

    protected fun onRequestCompleted(code: Int) {
        checkRuntime()
        log(String.format("request complete: code = %d", code))
        handler.removeCallbacksAndMessages(null)
        clearGattResponseListener(this)
        onResponse(code)
        dispatcher.onRequestCompleted(this)
    }

    override fun closeGatt() {
        log("close gatt")
        workerInternal.closeGatt()
    }

    open override fun handleMessage(message: Message): Boolean {
        if (message.what == MSG_REQUEST_TIMEOUT) {
            requestTimeout = true
            closeGatt()
        }
        return true
    }

    override fun registerGattResponseListener(gattResponseListener: GattResponseListener?) {
        workerInternal.registerGattResponseListener(gattResponseListener)
    }

    override fun clearGattResponseListener(gattResponseListener: GattResponseListener?) {
        workerInternal.clearGattResponseListener(gattResponseListener)
    }

    override fun refreshDeviceCache(): Boolean = workerInternal.refreshDeviceCache()

    override fun readCharacteristic(service: UUID?, character: UUID?): Boolean {
        return workerInternal.readCharacteristic(service, character)
    }

    override fun writeCharacteristic(service: UUID?, character: UUID?, value: ByteArray?): Boolean {
        return workerInternal.writeCharacteristic(service, character, value)
    }

    override fun writeCharacteristicWithNoRsp(service: UUID?, character: UUID?, value: ByteArray?): Boolean {
        return workerInternal.writeCharacteristicWithNoRsp(service, character, value)
    }

    override fun setCharacteristicNotification(service: UUID?, character: UUID?, enable: Boolean): Boolean {
        return workerInternal.setCharacteristicNotification(service, character, enable)
    }

    override fun setCharacteristicIndication(service: UUID?, character: UUID?, enable: Boolean): Boolean {
        return workerInternal.setCharacteristicIndication(service, character, enable)
    }

    override fun readRemoteRssi(): Boolean = workerInternal.readRemoteRssi()

    protected fun log(message: String) {
        BluetoothLog.v(String.format("%s %s >>> %s", javaClass.simpleName, getAddress(), message))
    }

    fun setRuntimeChecker(runtimeChecker: RuntimeChecker) {
        this.runtimeChecker = runtimeChecker
    }

    override fun checkRuntime() {
        runtimeChecker?.checkRuntime() ?: throw IllegalStateException("RuntimeChecker not set")
    }

    final override fun cancel() {
        checkRuntime()
        log("request canceled")
        handler.removeCallbacksAndMessages(null)
        clearGattResponseListener(this)
        onResponse(Constants.REQUEST_CANCELED)
    }

    override fun onConnectStatusChanged(connected: Boolean) {
        if (!connected) {
            onRequestCompleted(if (requestTimeout) Constants.REQUEST_TIMEDOUT else Constants.REQUEST_FAILED)
        }
    }

    protected fun startRequestTiming() {
        handler.sendEmptyMessageDelayed(MSG_REQUEST_TIMEOUT, getTimeoutInMillis())
    }

    protected fun stopRequestTiming() {
        handler.removeMessages(MSG_REQUEST_TIMEOUT)
    }

    override fun getGattProfile(): BleGattProfile? = workerInternal.getGattProfile()

    companion object {
        protected const val MSG_REQUEST_TIMEOUT = 32
    }
}
