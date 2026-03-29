package com.inuker.bluetooth.library.receiver.listener;

/* JADX INFO: loaded from: classes.dex */
public abstract class BluetoothBondStateChangeListener extends BluetoothReceiverListener {
    protected abstract void onBondStateChanged(String str, int i);

    @Override // com.inuker.bluetooth.library.receiver.listener.AbsBluetoothListener
    public void onInvoke(Object... objArr) {
        onBondStateChanged((String) objArr[0], ((Integer) objArr[1]).intValue());
    }

    @Override // com.inuker.bluetooth.library.receiver.listener.BluetoothReceiverListener
    public String getName() {
        return BluetoothBondStateChangeListener.class.getSimpleName();
    }
}
