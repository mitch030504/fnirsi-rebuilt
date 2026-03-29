package com.inuker.bluetooth.library.receiver.listener;

/* JADX INFO: loaded from: classes.dex */
public abstract class BluetoothReceiverListener extends AbsBluetoothListener {
    public abstract String getName();

    @Override // com.inuker.bluetooth.library.receiver.listener.AbsBluetoothListener
    public final void onSyncInvoke(Object... objArr) {
        throw new UnsupportedOperationException();
    }
}
