package com.inuker.bluetooth.library.search.le

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import com.inuker.bluetooth.library.search.BluetoothSearcher
import com.inuker.bluetooth.library.search.SearchResult
import com.inuker.bluetooth.library.search.response.BluetoothSearchResponse
import com.inuker.bluetooth.library.utils.BluetoothLog
import com.inuker.bluetooth.library.utils.BluetoothUtils

class BluetoothLESearcher private constructor() : BluetoothSearcher() {
    private val leScanCallback =
        BluetoothAdapter.LeScanCallback { bluetoothDevice: BluetoothDevice, rssi: Int, scanRecord: ByteArray ->
            notifyDeviceFounded(SearchResult(bluetoothDevice, rssi, scanRecord))
        }

    init {
        bluetoothAdapter = BluetoothUtils.getBluetoothAdapter()
    }

    override fun startScanBluetooth(bluetoothSearchResponse: BluetoothSearchResponse) {
        super.startScanBluetooth(bluetoothSearchResponse)
        bluetoothAdapter?.startLeScan(leScanCallback)
    }

    override fun stopScanBluetooth() {
        try {
            bluetoothAdapter?.stopLeScan(leScanCallback)
        } catch (e: Exception) {
            BluetoothLog.e(e)
        }
        super.stopScanBluetooth()
    }

    override fun cancelScanBluetooth() {
        bluetoothAdapter?.stopLeScan(leScanCallback)
        super.cancelScanBluetooth()
    }

    companion object {
        private val INSTANCE = BluetoothLESearcher()

        @JvmStatic
        fun getInstance(): BluetoothLESearcher = INSTANCE
    }
}
