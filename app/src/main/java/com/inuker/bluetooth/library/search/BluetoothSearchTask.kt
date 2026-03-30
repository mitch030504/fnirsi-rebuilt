package com.inuker.bluetooth.library.search

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.core.os.EnvironmentCompat
import com.inuker.bluetooth.library.search.response.BluetoothSearchResponse

class BluetoothSearchTask(searchTask: SearchTask) : Handler.Callback {
    private var bluetoothSearcher: BluetoothSearcher? = null
    private val handler = Handler(Looper.myLooper() ?: Looper.getMainLooper(), this)
    private var searchDuration = 0
    private var searchType = 0

    init {
        setSearchType(searchTask.getSearchType())
        setSearchDuration(searchTask.getSearchDuration())
    }

    fun setSearchType(value: Int) {
        searchType = value
    }

    fun setSearchDuration(value: Int) {
        searchDuration = value
    }

    fun isBluetoothLeSearch(): Boolean = searchType == 2

    fun isBluetoothClassicSearch(): Boolean = searchType == 1

    fun start(bluetoothSearchResponse: BluetoothSearchResponse) {
        getBluetoothSearcher().startScanBluetooth(bluetoothSearchResponse)
        handler.sendEmptyMessageDelayed(MSG_SEARCH_TIMEOUT, searchDuration.toLong())
    }

    fun cancel() {
        handler.removeCallbacksAndMessages(null)
        getBluetoothSearcher().cancelScanBluetooth()
    }

    override fun handleMessage(message: Message): Boolean {
        if (message.what == MSG_SEARCH_TIMEOUT) {
            getBluetoothSearcher().stopScanBluetooth()
        }
        return true
    }

    override fun toString(): String {
        val label = when {
            isBluetoothLeSearch() -> "Ble"
            isBluetoothClassicSearch() -> "classic"
            else -> EnvironmentCompat.MEDIA_UNKNOWN
        }
        return if (searchDuration >= 1000) {
            String.format("%s search (%ds)", label, searchDuration / 1000)
        } else {
            String.format("%s search (%.1fs)", label, searchDuration.toDouble() / 1000.0)
        }
    }

    private fun getBluetoothSearcher(): BluetoothSearcher {
        if (bluetoothSearcher == null) {
            bluetoothSearcher = BluetoothSearcher.newInstance(searchType)
        }
        return bluetoothSearcher!!
    }

    private companion object {
        const val MSG_SEARCH_TIMEOUT = 34
    }
}
