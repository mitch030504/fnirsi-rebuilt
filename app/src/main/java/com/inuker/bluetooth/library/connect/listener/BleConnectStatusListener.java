package com.inuker.bluetooth.library.connect.listener;

import com.inuker.bluetooth.library.receiver.listener.BluetoothClientListener;

/* JADX INFO: loaded from: classes.dex */
public abstract class BleConnectStatusListener extends BluetoothClientListener {
    public abstract void onConnectStatusChanged(String str, int i);

    @Override // com.inuker.bluetooth.library.receiver.listener.AbsBluetoothListener
    public void onSyncInvoke(Object... objArr) {
        onConnectStatusChanged((String) objArr[0], ((Integer) objArr[1]).intValue());
    }
}
