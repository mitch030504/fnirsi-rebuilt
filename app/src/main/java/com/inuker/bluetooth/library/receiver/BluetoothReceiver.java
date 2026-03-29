package com.inuker.bluetooth.library.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import com.inuker.bluetooth.library.receiver.listener.BluetoothReceiverListener;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothReceiver extends BroadcastReceiver implements IBluetoothReceiver, Handler.Callback {
    private static final int MSG_REGISTER = 1;
    private static IBluetoothReceiver mReceiver;
    private AbsBluetoothReceiver[] RECEIVERS;
    private IReceiverDispatcher mDispatcher;
    private Handler mHandler;
    private Map<String, List<BluetoothReceiverListener>> mListeners;

    public static IBluetoothReceiver getInstance() {
        if (mReceiver == null) {
            synchronized (BluetoothReceiver.class) {
                if (mReceiver == null) {
                    mReceiver = new BluetoothReceiver();
                }
            }
        }
        return mReceiver;
    }

    private BluetoothReceiver() {
        IReceiverDispatcher iReceiverDispatcher = new IReceiverDispatcher() { // from class: com.inuker.bluetooth.library.receiver.BluetoothReceiver.1
            @Override // com.inuker.bluetooth.library.receiver.IReceiverDispatcher
            public List<BluetoothReceiverListener> getListeners(Class<?> cls) {
                return (List) BluetoothReceiver.this.mListeners.get(cls.getSimpleName());
            }
        };
        this.mDispatcher = iReceiverDispatcher;
        this.RECEIVERS = new AbsBluetoothReceiver[]{BluetoothStateReceiver.newInstance(iReceiverDispatcher), BluetoothBondReceiver.newInstance(this.mDispatcher), BleConnectStatusChangeReceiver.newInstance(this.mDispatcher), BleCharacterChangeReceiver.newInstance(this.mDispatcher)};
        this.mListeners = new HashMap();
        this.mHandler = new Handler(Looper.getMainLooper(), this);
        BluetoothUtils.registerReceiver(this, getIntentFilter());
    }

    private IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        for (AbsBluetoothReceiver absBluetoothReceiver : this.RECEIVERS) {
            Iterator<String> it = absBluetoothReceiver.getActions().iterator();
            while (it.hasNext()) {
                intentFilter.addAction(it.next());
            }
        }
        return intentFilter;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        BluetoothLog.v(String.format("BluetoothReceiver onReceive: %s", action));
        for (AbsBluetoothReceiver absBluetoothReceiver : this.RECEIVERS) {
            if (absBluetoothReceiver.containsAction(action) && absBluetoothReceiver.onReceive(context, intent)) {
                return;
            }
        }
    }

    @Override // com.inuker.bluetooth.library.receiver.IBluetoothReceiver
    public void register(BluetoothReceiverListener bluetoothReceiverListener) {
        this.mHandler.obtainMessage(1, bluetoothReceiverListener).sendToTarget();
    }

    private void registerInner(BluetoothReceiverListener bluetoothReceiverListener) {
        if (bluetoothReceiverListener != null) {
            List<BluetoothReceiverListener> linkedList = this.mListeners.get(bluetoothReceiverListener.getName());
            if (linkedList == null) {
                linkedList = new LinkedList<>();
                this.mListeners.put(bluetoothReceiverListener.getName(), linkedList);
            }
            linkedList.add(bluetoothReceiverListener);
        }
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        if (message.what == 1) {
            registerInner((BluetoothReceiverListener) message.obj);
        }
        return true;
    }
}
