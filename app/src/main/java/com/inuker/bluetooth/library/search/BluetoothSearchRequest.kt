package com.inuker.bluetooth.library.search

import android.bluetooth.BluetoothDevice
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.inuker.bluetooth.library.search.response.BluetoothSearchResponse
import com.inuker.bluetooth.library.utils.BluetoothLog
import com.inuker.bluetooth.library.utils.BluetoothUtils

class BluetoothSearchRequest(searchRequest: SearchRequest) : Handler.Callback {
    private var currentTask: BluetoothSearchTask? = null
    private val handler = Handler(Looper.myLooper() ?: Looper.getMainLooper(), this)
    private var searchResponse: BluetoothSearchResponse? = null
    private val searchTaskList = ArrayList<BluetoothSearchTask>()

    init {
        searchRequest.getTasks().forEach { searchTaskList.add(BluetoothSearchTask(it)) }
    }

    fun setSearchResponse(bluetoothSearchResponse: BluetoothSearchResponse) {
        searchResponse = bluetoothSearchResponse
    }

    fun start() {
        searchResponse?.onSearchStarted()
        notifyConnectedBluetoothDevices()
        handler.sendEmptyMessageDelayed(MSG_START_SEARCH, SCAN_INTERVAL_MS)
    }

    override fun handleMessage(message: Message): Boolean {
        return when (message.what) {
            MSG_START_SEARCH -> {
                scheduleNewSearchTask()
                true
            }

            MSG_DEVICE_FOUND -> {
                (message.obj as? SearchResult)?.let { searchResponse?.onDeviceFounded(it) }
                true
            }

            else -> true
        }
    }

    fun cancel() {
        currentTask?.cancel()
        currentTask = null
        searchTaskList.clear()
        searchResponse?.onSearchCanceled()
        searchResponse = null
    }

    private fun scheduleNewSearchTask() {
        if (searchTaskList.isNotEmpty()) {
            val nextTask = searchTaskList.removeAt(0)
            currentTask = nextTask
            nextTask.start(BluetoothSearchTaskResponse(nextTask))
        } else {
            currentTask = null
            searchResponse?.onSearchStopped()
        }
    }

    private fun notifyConnectedBluetoothDevices() {
        var le = false
        var classic = false
        for (task in searchTaskList) {
            when {
                task.isBluetoothLeSearch() -> le = true
                task.isBluetoothClassicSearch() -> classic = true
                else -> throw IllegalArgumentException("unknown search task type!")
            }
        }
        if (le) {
            notifyConnectedBluetoothLeDevices()
        }
        if (classic) {
            notifyBondedBluetoothClassicDevices()
        }
    }

    private fun notifyConnectedBluetoothLeDevices() {
        BluetoothUtils.getConnectedBluetoothLeDevices().forEach { notifyDeviceFounded(SearchResult(it)) }
    }

    private fun notifyBondedBluetoothClassicDevices() {
        BluetoothUtils.getBondedBluetoothClassicDevices().forEach { notifyDeviceFounded(SearchResult(it)) }
    }

    private fun notifyDeviceFounded(searchResult: SearchResult) {
        handler.obtainMessage(MSG_DEVICE_FOUND, searchResult).sendToTarget()
    }

    override fun toString(): String = searchTaskList.joinToString(", ")

    private inner class BluetoothSearchTaskResponse(
        private val task: BluetoothSearchTask,
    ) : BluetoothSearchResponse {
        override fun onSearchStarted() {
            BluetoothLog.v(String.format("%s onSearchStarted", task))
        }

        override fun onDeviceFounded(searchResult: SearchResult) {
            BluetoothLog.v(String.format("onDeviceFounded %s", searchResult))
            notifyDeviceFounded(searchResult)
        }

        override fun onSearchStopped() {
            BluetoothLog.v(String.format("%s onSearchStopped", task))
            handler.sendEmptyMessageDelayed(MSG_START_SEARCH, SCAN_INTERVAL_MS)
        }

        override fun onSearchCanceled() {
            BluetoothLog.v(String.format("%s onSearchCanceled", task))
        }
    }

    private companion object {
        const val MSG_START_SEARCH = 17
        const val MSG_DEVICE_FOUND = 18
        const val SCAN_INTERVAL_MS = 100L
    }
}
