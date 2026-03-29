package com.inuker.bluetooth.library.receiver.listener;

import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public abstract class BleCharacterChangeListener extends BluetoothReceiverListener {
    protected abstract void onCharacterChanged(String str, UUID uuid, UUID uuid2, byte[] bArr);

    @Override // com.inuker.bluetooth.library.receiver.listener.AbsBluetoothListener
    public void onInvoke(Object... objArr) {
        onCharacterChanged((String) objArr[0], (UUID) objArr[1], (UUID) objArr[2], (byte[]) objArr[3]);
    }

    @Override // com.inuker.bluetooth.library.receiver.listener.BluetoothReceiverListener
    public String getName() {
        return BleCharacterChangeListener.class.getSimpleName();
    }
}
