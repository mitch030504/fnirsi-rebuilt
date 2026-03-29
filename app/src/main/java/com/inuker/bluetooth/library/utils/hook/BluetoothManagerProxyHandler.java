package com.inuker.bluetooth.library.utils.hook;

import android.os.IBinder;
import android.os.IInterface;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.hook.utils.HookUtils;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothManagerProxyHandler implements InvocationHandler {
    private Object bluetoothGatt;
    private Class<?> bluetoothGattClaz = HookUtils.getClass("android.bluetooth.IBluetoothGatt");
    private Object iBluetoothManager;

    BluetoothManagerProxyHandler(Object obj) {
        this.iBluetoothManager = obj;
        this.bluetoothGatt = HookUtils.invoke(HookUtils.getMethod(HookUtils.getClass("android.bluetooth.IBluetoothManager"), "getBluetoothGatt", new Class[0]), obj, new Object[0]);
    }

    @Override // java.lang.reflect.InvocationHandler
    public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
        BluetoothLog.v(String.format("IBluetoothManager method: %s", method.getName()));
        return "getBluetoothGatt".equals(method.getName()) ? Proxy.newProxyInstance(obj.getClass().getClassLoader(), new Class[]{IBinder.class, IInterface.class, this.bluetoothGattClaz}, new BluetoothGattProxyHandler(this.bluetoothGatt)) : method.invoke(this.iBluetoothManager, objArr);
    }
}
