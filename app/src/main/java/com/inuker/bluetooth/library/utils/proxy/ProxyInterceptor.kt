package com.inuker.bluetooth.library.utils.proxy

import java.lang.reflect.Method

fun interface ProxyInterceptor {
    fun onIntercept(obj: Any?, method: Method, objArr: Array<Any?>?): Boolean
}
