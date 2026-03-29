package com.inuker.bluetooth.library.utils.hook;

import android.os.IBinder;
import android.os.IInterface;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.hook.utils.HookUtils;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothManagerBinderProxyHandler implements InvocationHandler {
    private IBinder iBinder;
    private Object iBluetoothManager;
    private Class<?> iBluetoothManagerClaz = HookUtils.getClass("android.bluetooth.IBluetoothManager");

    BluetoothManagerBinderProxyHandler(IBinder iBinder) {
        this.iBinder = iBinder;
        this.iBluetoothManager = HookUtils.invoke(HookUtils.getMethod(HookUtils.getClass("android.bluetooth.IBluetoothManager$Stub"), "asInterface", IBinder.class), null, iBinder);
    }

    @Override // java.lang.reflect.InvocationHandler
    public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
        BluetoothLog.v(String.format("IBinder method: %s", method.getName()));
        return "queryLocalInterface".equals(method.getName()) ? Proxy.newProxyInstance(obj.getClass().getClassLoader(), new Class[]{IBinder.class, IInterface.class, this.iBluetoothManagerClaz}, new BluetoothManagerProxyHandler(this.iBluetoothManager)) : method.invoke(this.iBinder, objArr);
    }
}
