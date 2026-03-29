package com.inuker.bluetooth.library.receiver.listener;

import com.inuker.bluetooth.library.BluetoothClientImpl;

/* JADX INFO: loaded from: classes.dex */
public abstract class BluetoothStateChangeListener extends BluetoothReceiverListener {
    protected abstract void onBluetoothStateChanged(int i, int i2);

    @Override // com.inuker.bluetooth.library.receiver.listener.AbsBluetoothListener
    public void onInvoke(Object... objArr) {
        int iIntValue = ((Integer) objArr[0]).intValue();
        int iIntValue2 = ((Integer) objArr[1]).intValue();
        if (iIntValue2 == 10 || iIntValue2 == 13) {
            BluetoothClientImpl.getInstance(null).stopSearch();
        }
        onBluetoothStateChanged(iIntValue, iIntValue2);
    }

    @Override // com.inuker.bluetooth.library.receiver.listener.BluetoothReceiverListener
    public String getName() {
        return BluetoothStateChangeListener.class.getSimpleName();
    }
}
