package com.inuker.bluetooth.library.utils.proxy

import java.lang.reflect.Proxy

object ProxyUtils {
    @JvmStatic
    fun getProxy(
        obj: Any,
        interfaces: Array<Class<*>>,
        interceptor: ProxyInterceptor?,
        weakRef: Boolean,
        postUI: Boolean,
    ): Any =
        Proxy.newProxyInstance(
            obj.javaClass.classLoader,
            interfaces,
            ProxyInvocationHandler(obj, interceptor, weakRef, postUI),
        )

    @JvmStatic
    fun getProxy(
        obj: Any,
        interfaceClass: Class<*>,
        interceptor: ProxyInterceptor?,
        weakRef: Boolean,
        postUI: Boolean,
    ): Any = getProxy(obj, arrayOf(interfaceClass), interceptor, weakRef, postUI)

    @JvmStatic
    fun getProxy(obj: Any, interfaceClass: Class<*>, interceptor: ProxyInterceptor?): Any =
        getProxy(obj, interfaceClass, interceptor, weakRef = false, postUI = false)

    @JvmStatic
    fun getWeakUIProxy(obj: Any, interfaceClass: Class<*>): Any =
        getProxy(obj, interfaceClass, interceptor = null, weakRef = true, postUI = true)

    @JvmStatic
    fun getUIProxy(obj: Any): Any = getUIProxy(obj, obj.javaClass.interfaces, interceptor = null)

    @JvmStatic
    fun getUIProxy(obj: Any, interfaceClass: Class<*>): Any = getUIProxy(obj, arrayOf(interfaceClass), interceptor = null)

    @JvmStatic
    fun getUIProxy(obj: Any, interfaceClass: Class<*>, interceptor: ProxyInterceptor?): Any =
        getUIProxy(obj, arrayOf(interfaceClass), interceptor)

    @JvmStatic
    fun getUIProxy(obj: Any, interfaces: Array<Class<*>>, interceptor: ProxyInterceptor?): Any =
        getProxy(obj, interfaces, interceptor, weakRef = false, postUI = true)
}
