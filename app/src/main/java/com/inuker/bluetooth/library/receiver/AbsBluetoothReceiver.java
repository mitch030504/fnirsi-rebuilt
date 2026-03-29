package com.inuker.bluetooth.library.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import com.inuker.bluetooth.library.BluetoothContext;
import com.inuker.bluetooth.library.receiver.listener.BluetoothReceiverListener;
import com.inuker.bluetooth.library.utils.ListUtils;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbsBluetoothReceiver {
    protected IReceiverDispatcher mDispatcher;
    protected Context mContext = BluetoothContext.get();
    protected Handler mHandler = new Handler(Looper.getMainLooper());

    abstract List<String> getActions();

    abstract boolean onReceive(Context context, Intent intent);

    protected AbsBluetoothReceiver(IReceiverDispatcher iReceiverDispatcher) {
        this.mDispatcher = iReceiverDispatcher;
    }

    boolean containsAction(String str) {
        List<String> actions = getActions();
        if (ListUtils.isEmpty(actions) || TextUtils.isEmpty(str)) {
            return false;
        }
        return actions.contains(str);
    }

    protected List<BluetoothReceiverListener> getListeners(Class<?> cls) {
        List<BluetoothReceiverListener> listeners = this.mDispatcher.getListeners(cls);
        return listeners != null ? listeners : Collections.EMPTY_LIST;
    }
}
