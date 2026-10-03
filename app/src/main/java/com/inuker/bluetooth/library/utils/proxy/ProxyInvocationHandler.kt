package com.inuker.bluetooth.library.utils.proxy

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.inuker.bluetooth.library.utils.BluetoothLog
import java.lang.ref.WeakReference
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

class ProxyInvocationHandler(
    obj: Any,
    private val interceptor: ProxyInterceptor? = null,
    private val weakRef: Boolean = false,
    private val postUI: Boolean = false,
) : InvocationHandler, ProxyInterceptor, Handler.Callback {
    private val subject: Any = if (weakRef) WeakReference(obj) else obj
    private val handler = Handler(Looper.getMainLooper(), this)

    override fun invoke(proxy: Any?, method: Method, args: Array<out Any?>?): Any? {
        val target = getObject()
        @Suppress("UNCHECKED_CAST")
        val invocationArgs = args as Array<Any?>?
        if (onIntercept(target, method, invocationArgs)) {
            return null
        }
        val bulk = ProxyBulk(target, method, invocationArgs)
        return if (postUI) postSafeInvoke(bulk) else safeInvoke(bulk)
    }

    override fun onIntercept(obj: Any?, method: Method, objArr: Array<Any?>?): Boolean {
        val currentInterceptor = interceptor ?: return false
        return try {
            currentInterceptor.onIntercept(obj, method, objArr)
        } catch (exception: Exception) {
            BluetoothLog.e(exception)
            false
        }
    }

    private fun getObject(): Any? =
        if (weakRef) {
            (subject as WeakReference<*>).get()
        } else {
            subject
        }

    private fun postSafeInvoke(proxyBulk: ProxyBulk): Any? {
        handler.obtainMessage(0, proxyBulk).sendToTarget()
        return null
    }

    private fun safeInvoke(proxyBulk: ProxyBulk): Any? =
        try {
            proxyBulk.safeInvoke()
        } catch (_: Throwable) {
            null
        }

    override fun handleMessage(message: Message): Boolean {
        ProxyBulk.safeInvoke(message.obj)
        return true
    }
}
