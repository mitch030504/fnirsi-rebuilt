package com.inuker.bluetooth.library.search.classic

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.inuker.bluetooth.library.search.BluetoothSearcher
import com.inuker.bluetooth.library.search.SearchResult
import com.inuker.bluetooth.library.search.response.BluetoothSearchResponse
import com.inuker.bluetooth.library.utils.BluetoothUtils

class BluetoothClassicSearcher private constructor() : BluetoothSearcher() {
    private var receiver: BluetoothSearchReceiver? = null

    init {
        bluetoothAdapter = BluetoothUtils.getBluetoothAdapter()
    }

    override fun startScanBluetooth(bluetoothSearchResponse: BluetoothSearchResponse) {
        super.startScanBluetooth(bluetoothSearchResponse)
        registerReceiver()
        bluetoothAdapter?.takeIf { it.isDiscovering }?.cancelDiscovery()
        bluetoothAdapter?.startDiscovery()
    }

    override fun stopScanBluetooth() {
        unregisterReceiver()
        bluetoothAdapter?.takeIf { it.isDiscovering }?.cancelDiscovery()
        super.stopScanBluetooth()
    }

    override fun cancelScanBluetooth() {
        unregisterReceiver()
        bluetoothAdapter?.takeIf { it.isDiscovering }?.cancelDiscovery()
        super.cancelScanBluetooth()
    }

    private fun registerReceiver() {
        if (receiver != null) {
            return
        }
        val currentReceiver = BluetoothSearchReceiver()
        receiver = currentReceiver
        BluetoothUtils.registerReceiver(currentReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
    }

    private fun unregisterReceiver() {
        receiver?.let {
            BluetoothUtils.unregisterReceiver(it)
            receiver = null
        }
    }

    private inner class BluetoothSearchReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == BluetoothDevice.ACTION_FOUND) {
                val device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                if (device != null) {
                    val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE).toInt()
                    notifyDeviceFounded(SearchResult(device, rssi, null))
                }
            }
        }
    }

    companion object {
        private val INSTANCE = BluetoothClassicSearcher()

        @JvmStatic
        fun getInstance(): BluetoothClassicSearcher = INSTANCE
    }
}
