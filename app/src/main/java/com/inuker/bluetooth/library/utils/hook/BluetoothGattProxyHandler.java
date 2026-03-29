package com.inuker.bluetooth.library.utils.hook;

import com.inuker.bluetooth.library.utils.BluetoothLog;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothGattProxyHandler implements InvocationHandler {
    private Object bluetoothGatt;

    BluetoothGattProxyHandler(Object obj) {
        this.bluetoothGatt = obj;
    }

    @Override // java.lang.reflect.InvocationHandler
    public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
        BluetoothLog.v(String.format("IBluetoothGatt method: %s", method.getName()));
        return method.invoke(this.bluetoothGatt, objArr);
    }
}
