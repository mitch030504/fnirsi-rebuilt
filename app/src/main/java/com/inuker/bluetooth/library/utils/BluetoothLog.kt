package com.inuker.bluetooth.library.utils

import android.util.Log
import java.io.PrintWriter
import java.io.StringWriter

object BluetoothLog {
    private const val LOG_TAG = "miio-bluetooth"

    @JvmStatic
    fun i(value: String) {
        Log.i(LOG_TAG, value)
    }

    @JvmStatic
    fun e(value: String) {
        Log.e(LOG_TAG, value)
    }

    @JvmStatic
    fun v(value: String) {
        Log.v(LOG_TAG, value)
    }

    @JvmStatic
    fun d(value: String) {
        Log.d(LOG_TAG, value)
    }

    @JvmStatic
    fun w(value: String) {
        Log.w(LOG_TAG, value)
    }

    @JvmStatic
    fun e(throwable: Throwable) {
        e(getThrowableString(throwable))
    }

    @JvmStatic
    fun w(throwable: Throwable) {
        w(getThrowableString(throwable))
    }

    private fun getThrowableString(throwable: Throwable): String {
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)
        var current: Throwable? = throwable
        while (current != null) {
            current.printStackTrace(printWriter)
            current = current.cause
        }
        val result = stringWriter.toString()
        printWriter.close()
        return result
    }
}
