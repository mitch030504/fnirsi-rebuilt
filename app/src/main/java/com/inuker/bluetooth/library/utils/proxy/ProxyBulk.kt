package com.inuker.bluetooth.library.utils.proxy

import com.inuker.bluetooth.library.utils.BluetoothLog
import java.lang.reflect.Method

class ProxyBulk(
    var objectRef: Any?,
    var method: Method,
    var args: Array<Any?>?,
) {
    fun safeInvoke(): Any? =
        try {
            method.invoke(objectRef, *(args ?: emptyArray()))
        } catch (throwable: Throwable) {
            BluetoothLog.e(throwable)
            null
        }

    companion object {
        @JvmStatic
        fun safeInvoke(value: Any?): Any? = (value as ProxyBulk).safeInvoke()
    }
}
