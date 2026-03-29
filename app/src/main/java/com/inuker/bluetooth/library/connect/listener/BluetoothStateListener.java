package com.inuker.bluetooth.library.connect.listener;

import com.inuker.bluetooth.library.receiver.listener.BluetoothClientListener;

/* JADX INFO: loaded from: classes.dex */
public abstract class BluetoothStateListener extends BluetoothClientListener {
    public abstract void onBluetoothStateChanged(boolean z);

    @Override // com.inuker.bluetooth.library.receiver.listener.AbsBluetoothListener
    public void onSyncInvoke(Object... objArr) {
        onBluetoothStateChanged(((Boolean) objArr[0]).booleanValue());
    }
}
