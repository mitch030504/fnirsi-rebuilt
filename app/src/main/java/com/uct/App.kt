package com.uct

import android.app.Application
import android.content.Context
import com.inuker.bluetooth.library.BluetoothClient

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this
        appInstance = this
        bluetoothClient = BluetoothClient(this)
    }

    companion object {
        @Volatile
        private var bluetoothClient: BluetoothClient? = null

        private lateinit var appContext: Context
        private lateinit var appInstance: App

        @JvmStatic
        fun getInstance(): App = appInstance

        @JvmStatic
        fun getContext(): Context = appContext

        @JvmStatic
        fun getBle(): BluetoothClient {
            return bluetoothClient ?: synchronized(this) {
                bluetoothClient ?: BluetoothClient(getContext()).also { bluetoothClient = it }
            }
        }
    }
}
