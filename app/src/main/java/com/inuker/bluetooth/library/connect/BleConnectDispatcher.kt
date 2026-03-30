package com.inuker.bluetooth.library.connect

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.inuker.bluetooth.library.RuntimeChecker
import com.inuker.bluetooth.library.connect.options.BleConnectOptions
import com.inuker.bluetooth.library.connect.request.BleConnectRequest
import com.inuker.bluetooth.library.connect.request.BleIndicateRequest
import com.inuker.bluetooth.library.connect.request.BleNotifyRequest
import com.inuker.bluetooth.library.connect.request.BleReadDescriptorRequest
import com.inuker.bluetooth.library.connect.request.BleReadRequest
import com.inuker.bluetooth.library.connect.request.BleReadRssiRequest
import com.inuker.bluetooth.library.connect.request.BleRefreshCacheRequest
import com.inuker.bluetooth.library.connect.request.BleRequest
import com.inuker.bluetooth.library.connect.request.BleUnnotifyRequest
import com.inuker.bluetooth.library.connect.request.BleWriteDescriptorRequest
import com.inuker.bluetooth.library.connect.request.BleWriteNoRspRequest
import com.inuker.bluetooth.library.connect.request.BleWriteRequest
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse
import com.inuker.bluetooth.library.utils.BluetoothLog
import java.util.LinkedList
import java.util.UUID

class BleConnectDispatcher private constructor(
    private val address: String,
) : IBleConnectDispatcher, RuntimeChecker, Handler.Callback {
    private var currentRequest: BleRequest? = null
    private val worker: IBleConnectWorker = BleConnectWorker(address, this)
    private val bleWorkList = LinkedList<BleRequest>()
    private val handler = Handler(Looper.myLooper() ?: throw IllegalStateException(), this)

    fun connect(bleConnectOptions: BleConnectOptions?, bleGeneralResponse: BleGeneralResponse?) {
        addNewRequest(BleConnectRequest(bleConnectOptions, bleGeneralResponse))
    }

    fun disconnect() {
        checkRuntime()
        BluetoothLog.w(String.format("Process disconnect"))
        currentRequest?.cancel()
        currentRequest = null
        bleWorkList.forEach { it.cancel() }
        bleWorkList.clear()
        worker.closeGatt()
    }

    fun refreshCache() {
        addNewRequest(BleRefreshCacheRequest(null))
    }

    fun clearRequest(type: Int) {
        checkRuntime()
        BluetoothLog.w(String.format("clearRequest %d", type))
        val requestsToCancel = LinkedList<BleRequest>()
        if (type == 0) {
            requestsToCancel.addAll(bleWorkList)
        } else {
            bleWorkList.forEach { request ->
                if (isRequestMatch(request, type)) {
                    requestsToCancel.add(request)
                }
            }
        }
        requestsToCancel.forEach { it.cancel() }
        bleWorkList.removeAll(requestsToCancel.toSet())
    }

    private fun isRequestMatch(bleRequest: BleRequest, type: Int): Boolean {
        if (type and 1 != 0) {
            return bleRequest is BleReadRequest
        }
        if (type and 2 != 0) {
            return bleRequest is BleWriteRequest || bleRequest is BleWriteNoRspRequest
        }
        if (type and 4 != 0) {
            return bleRequest is BleNotifyRequest || bleRequest is BleUnnotifyRequest || bleRequest is BleIndicateRequest
        }
        if (type and 8 != 0) {
            return bleRequest is BleReadRssiRequest
        }
        return false
    }

    fun read(service: UUID?, character: UUID?, bleGeneralResponse: BleGeneralResponse?) {
        addNewRequest(BleReadRequest(service, character, bleGeneralResponse))
    }

    fun write(service: UUID?, character: UUID?, value: ByteArray?, bleGeneralResponse: BleGeneralResponse?) {
        addNewRequest(BleWriteRequest(service, character, value, bleGeneralResponse))
    }

    fun writeNoRsp(service: UUID?, character: UUID?, value: ByteArray?, bleGeneralResponse: BleGeneralResponse?) {
        addNewRequest(BleWriteNoRspRequest(service, character, value, bleGeneralResponse))
    }

    fun readDescriptor(service: UUID?, character: UUID?, descriptor: UUID?, bleGeneralResponse: BleGeneralResponse?) {
        addNewRequest(BleReadDescriptorRequest(service, character, descriptor, bleGeneralResponse))
    }

    fun writeDescriptor(
        service: UUID?,
        character: UUID?,
        descriptor: UUID?,
        value: ByteArray?,
        bleGeneralResponse: BleGeneralResponse?,
    ) {
        addNewRequest(BleWriteDescriptorRequest(service, character, descriptor, value, bleGeneralResponse))
    }

    fun notify(service: UUID?, character: UUID?, bleGeneralResponse: BleGeneralResponse?) {
        addNewRequest(BleNotifyRequest(service, character, bleGeneralResponse))
    }

    fun unnotify(service: UUID?, character: UUID?, bleGeneralResponse: BleGeneralResponse?) {
        addNewRequest(BleUnnotifyRequest(service, character, bleGeneralResponse))
    }

    fun indicate(service: UUID?, character: UUID?, bleGeneralResponse: BleGeneralResponse?) {
        addNewRequest(BleIndicateRequest(service, character, bleGeneralResponse))
    }

    fun unindicate(service: UUID?, character: UUID?, bleGeneralResponse: BleGeneralResponse?) {
        addNewRequest(BleUnnotifyRequest(service, character, bleGeneralResponse))
    }

    fun readRemoteRssi(bleGeneralResponse: BleGeneralResponse?) {
        addNewRequest(BleReadRssiRequest(bleGeneralResponse))
    }

    private fun addNewRequest(bleRequest: BleRequest) {
        checkRuntime()
        if (bleWorkList.size < MAX_REQUEST_COUNT) {
            bleRequest.setRuntimeChecker(this)
            bleRequest.setAddress(address)
            bleRequest.setWorker(worker)
            bleWorkList.add(bleRequest)
        } else {
            bleRequest.onResponse(-8)
        }
        scheduleNextRequest(10L)
    }

    override fun onRequestCompleted(bleRequest: BleRequest) {
        checkRuntime()
        if (bleRequest !== currentRequest) {
            throw IllegalStateException("request not match")
        }
        currentRequest = null
        scheduleNextRequest(10L)
    }

    private fun scheduleNextRequest(delayMillis: Long) {
        handler.sendEmptyMessageDelayed(MSG_SCHEDULE_NEXT, delayMillis)
    }

    private fun scheduleNextRequest() {
        if (currentRequest == null && bleWorkList.isNotEmpty()) {
            val nextRequest = bleWorkList.removeAt(0)
            currentRequest = nextRequest
            nextRequest.process(this)
        }
    }

    override fun checkRuntime() {
        if (Thread.currentThread() != handler.looper.thread) {
            throw IllegalStateException("Thread Context Illegal")
        }
    }

    override fun handleMessage(message: Message): Boolean {
        if (message.what == MSG_SCHEDULE_NEXT) {
            scheduleNextRequest()
        }
        return true
    }

    companion object {
        private const val MAX_REQUEST_COUNT = 100
        private const val MSG_SCHEDULE_NEXT = 18

        @JvmStatic
        fun newInstance(address: String): BleConnectDispatcher = BleConnectDispatcher(address)
    }
}
