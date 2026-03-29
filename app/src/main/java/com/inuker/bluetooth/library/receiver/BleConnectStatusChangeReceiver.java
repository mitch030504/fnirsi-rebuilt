package com.inuker.bluetooth.library.receiver;

import android.content.Context;
import android.content.Intent;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.receiver.listener.BleConnectStatusChangeListener;
import com.inuker.bluetooth.library.receiver.listener.BluetoothReceiverListener;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class BleConnectStatusChangeReceiver extends AbsBluetoothReceiver {
    private static final String[] ACTIONS = {Constants.ACTION_CONNECT_STATUS_CHANGED};

    protected BleConnectStatusChangeReceiver(IReceiverDispatcher iReceiverDispatcher) {
        super(iReceiverDispatcher);
    }

    public static BleConnectStatusChangeReceiver newInstance(IReceiverDispatcher iReceiverDispatcher) {
        return new BleConnectStatusChangeReceiver(iReceiverDispatcher);
    }

    @Override // com.inuker.bluetooth.library.receiver.AbsBluetoothReceiver
    List<String> getActions() {
        return Arrays.asList(ACTIONS);
    }

    @Override // com.inuker.bluetooth.library.receiver.AbsBluetoothReceiver
    boolean onReceive(Context context, Intent intent) {
        String stringExtra = intent.getStringExtra(Constants.EXTRA_MAC);
        int intExtra = intent.getIntExtra(Constants.EXTRA_STATUS, 0);
        BluetoothLog.v(String.format("onConnectStatusChanged for %s, status = %d", stringExtra, Integer.valueOf(intExtra)));
        onConnectStatusChanged(stringExtra, intExtra);
        return true;
    }

    private void onConnectStatusChanged(String str, int i) {
        Iterator<BluetoothReceiverListener> it = getListeners(BleConnectStatusChangeListener.class).iterator();
        while (it.hasNext()) {
            it.next().invoke(str, Integer.valueOf(i));
        }
    }
}
