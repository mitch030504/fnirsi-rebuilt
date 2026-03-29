package com.inuker.bluetooth.library.utils.hook;

import android.os.IBinder;
import com.inuker.bluetooth.library.utils.hook.compat.ServiceManagerCompat;
import com.inuker.bluetooth.library.utils.hook.utils.HookUtils;
import java.lang.reflect.Proxy;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothHooker {
    private static final String BLUETOOTH_MANAGER = "bluetooth_manager";

    public static void hook() {
        IBinder iBinder = (IBinder) HookUtils.invoke(ServiceManagerCompat.getService(), null, BLUETOOTH_MANAGER);
        ServiceManagerCompat.getCacheValue().put(BLUETOOTH_MANAGER, (IBinder) Proxy.newProxyInstance(iBinder.getClass().getClassLoader(), new Class[]{IBinder.class}, new BluetoothManagerBinderProxyHandler(iBinder)));
    }
}
