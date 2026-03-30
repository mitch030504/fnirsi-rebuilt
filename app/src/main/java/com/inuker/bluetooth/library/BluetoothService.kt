package com.inuker.bluetooth.library

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.inuker.bluetooth.library.utils.BluetoothLog

class BluetoothService : Service() {
    override fun onCreate() {
        super.onCreate()
        BluetoothLog.v(String.format("BluetoothService onCreate"))
        val applicationContext = applicationContext
        context = applicationContext
        BluetoothContext.set(applicationContext)
    }

    override fun onBind(intent: Intent): IBinder {
        BluetoothLog.v(String.format("BluetoothService onBind"))
        return BluetoothServiceImpl.getInstance()
    }

    companion object {
        private var context: Context? = null

        @JvmStatic
        fun getContext(): Context? = context
    }
}
