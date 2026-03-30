package com.inuker.bluetooth.library

import android.content.Context
import android.os.Handler
import android.os.Looper

object BluetoothContext {
    private var context: Context? = null
    private var handler: Handler? = null

    @JvmStatic
    fun set(context: Context) {
        this.context = context
    }

    @JvmStatic
    fun get(): Context = context ?: throw IllegalStateException("BluetoothContext not initialized")

    @JvmStatic
    fun post(runnable: Runnable) {
        postDelayed(runnable, 0L)
    }

    @JvmStatic
    fun postDelayed(runnable: Runnable, delayMillis: Long) {
        if (handler == null) {
            handler = Handler(Looper.getMainLooper())
        }
        handler!!.postDelayed(runnable, delayMillis)
    }
}
