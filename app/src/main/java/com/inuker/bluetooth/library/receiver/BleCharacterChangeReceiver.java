package com.inuker.bluetooth.library.receiver;

import android.content.Context;
import android.content.Intent;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.receiver.listener.BleCharacterChangeListener;
import com.inuker.bluetooth.library.receiver.listener.BluetoothReceiverListener;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class BleCharacterChangeReceiver extends AbsBluetoothReceiver {
    private static final String[] ACTIONS = {Constants.ACTION_CHARACTER_CHANGED};

    protected BleCharacterChangeReceiver(IReceiverDispatcher iReceiverDispatcher) {
        super(iReceiverDispatcher);
    }

    public static BleCharacterChangeReceiver newInstance(IReceiverDispatcher iReceiverDispatcher) {
        return new BleCharacterChangeReceiver(iReceiverDispatcher);
    }

    @Override // com.inuker.bluetooth.library.receiver.AbsBluetoothReceiver
    List<String> getActions() {
        return Arrays.asList(ACTIONS);
    }

    @Override // com.inuker.bluetooth.library.receiver.AbsBluetoothReceiver
    boolean onReceive(Context context, Intent intent) {
        onCharacterChanged(intent.getStringExtra(Constants.EXTRA_MAC), (UUID) intent.getSerializableExtra(Constants.EXTRA_SERVICE_UUID), (UUID) intent.getSerializableExtra(Constants.EXTRA_CHARACTER_UUID), intent.getByteArrayExtra(Constants.EXTRA_BYTE_VALUE));
        return true;
    }

    private void onCharacterChanged(String str, UUID uuid, UUID uuid2, byte[] bArr) {
        Iterator<BluetoothReceiverListener> it = getListeners(BleCharacterChangeListener.class).iterator();
        while (it.hasNext()) {
            it.next().invoke(str, uuid, uuid2, bArr);
        }
    }
}
