package com.inuker.bluetooth.library.receiver.listener;

/* JADX INFO: loaded from: classes.dex */
public abstract class BluetoothBondListener extends BluetoothClientListener {
    public abstract void onBondStateChanged(String str, int i);

    @Override // com.inuker.bluetooth.library.receiver.listener.AbsBluetoothListener
    public void onSyncInvoke(Object... objArr) {
        onBondStateChanged((String) objArr[0], ((Integer) objArr[1]).intValue());
    }
}
