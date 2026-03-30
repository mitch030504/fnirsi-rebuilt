package com.inuker.bluetooth.library.search

import android.bluetooth.BluetoothAdapter
import com.inuker.bluetooth.library.search.classic.BluetoothClassicSearcher
import com.inuker.bluetooth.library.search.le.BluetoothLESearcher
import com.inuker.bluetooth.library.search.response.BluetoothSearchResponse

open class BluetoothSearcher {
    protected var bluetoothAdapter: BluetoothAdapter? = null
    protected var searchResponse: BluetoothSearchResponse? = null

    internal open fun startScanBluetooth(bluetoothSearchResponse: BluetoothSearchResponse) {
        searchResponse = bluetoothSearchResponse
        searchResponse?.onSearchStarted()
    }

    internal open fun stopScanBluetooth() {
        searchResponse?.onSearchStopped()
        searchResponse = null
    }

    internal open fun cancelScanBluetooth() {
        searchResponse?.onSearchCanceled()
        searchResponse = null
    }

    protected fun notifyDeviceFounded(searchResult: SearchResult) {
        searchResponse?.onDeviceFounded(searchResult)
    }

    companion object {
        @JvmStatic
        fun newInstance(type: Int): BluetoothSearcher {
            return when (type) {
                1 -> BluetoothClassicSearcher.getInstance()
                2 -> BluetoothLESearcher.getInstance()
                else -> throw IllegalStateException(String.format("unknown search type %d", type))
            }
        }
    }
}
