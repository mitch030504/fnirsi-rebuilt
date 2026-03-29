package com.inuker.bluetooth.library.utils.proxy;

import java.lang.reflect.Proxy;

/* JADX INFO: loaded from: classes.dex */
public class ProxyUtils {
    public static <T> T getProxy(Object obj, Class<?>[] clsArr, ProxyInterceptor proxyInterceptor, boolean z, boolean z2) {
        return (T) Proxy.newProxyInstance(obj.getClass().getClassLoader(), clsArr, new ProxyInvocationHandler(obj, proxyInterceptor, z, z2));
    }

    public static <T> T getProxy(Object obj, Class<?> cls, ProxyInterceptor proxyInterceptor, boolean z, boolean z2) {
        return (T) getProxy(obj, (Class<?>[]) new Class[]{cls}, proxyInterceptor, z, z2);
    }

    public static <T> T getProxy(Object obj, Class<?> cls, ProxyInterceptor proxyInterceptor) {
        return (T) getProxy(obj, cls, proxyInterceptor, false, false);
    }

    public static <T> T getWeakUIProxy(Object obj, Class<?> cls) {
        return (T) getProxy(obj, cls, (ProxyInterceptor) null, true, true);
    }

    public static <T> T getUIProxy(Object obj) {
        return (T) getUIProxy(obj, obj.getClass().getInterfaces(), (ProxyInterceptor) null);
    }

    public static <T> T getUIProxy(Object obj, Class<?> cls) {
        return (T) getUIProxy(obj, (Class<?>[]) new Class[]{cls}, (ProxyInterceptor) null);
    }

    public static <T> T getUIProxy(Object obj, Class<?> cls, ProxyInterceptor proxyInterceptor) {
        return (T) getUIProxy(obj, (Class<?>[]) new Class[]{cls}, proxyInterceptor);
    }

    public static <T> T getUIProxy(Object obj, Class<?>[] clsArr, ProxyInterceptor proxyInterceptor) {
        return (T) getProxy(obj, clsArr, proxyInterceptor, false, true);
    }
}
