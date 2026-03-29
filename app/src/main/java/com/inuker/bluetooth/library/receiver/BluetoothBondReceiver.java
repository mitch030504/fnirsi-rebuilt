package com.inuker.bluetooth.library.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import com.inuker.bluetooth.library.receiver.listener.BluetoothBondStateChangeListener;
import com.inuker.bluetooth.library.receiver.listener.BluetoothReceiverListener;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothBondReceiver extends AbsBluetoothReceiver {
    private static final String[] ACTIONS = {"android.bluetooth.device.action.BOND_STATE_CHANGED"};

    protected BluetoothBondReceiver(IReceiverDispatcher iReceiverDispatcher) {
        super(iReceiverDispatcher);
    }

    public static BluetoothBondReceiver newInstance(IReceiverDispatcher iReceiverDispatcher) {
        return new BluetoothBondReceiver(iReceiverDispatcher);
    }

    @Override // com.inuker.bluetooth.library.receiver.AbsBluetoothReceiver
    List<String> getActions() {
        return Arrays.asList(ACTIONS);
    }

    @Override // com.inuker.bluetooth.library.receiver.AbsBluetoothReceiver
    boolean onReceive(Context context, Intent intent) {
        BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        int intExtra = intent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", -1);
        if (bluetoothDevice == null) {
            return true;
        }
        onBondStateChanged(bluetoothDevice.getAddress(), intExtra);
        return true;
    }

    private void onBondStateChanged(String str, int i) {
        Iterator<BluetoothReceiverListener> it = getListeners(BluetoothBondStateChangeListener.class).iterator();
        while (it.hasNext()) {
            it.next().invoke(str, Integer.valueOf(i));
        }
    }
}
