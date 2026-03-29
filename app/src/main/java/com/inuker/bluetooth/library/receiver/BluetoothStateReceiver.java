package com.inuker.bluetooth.library.receiver;

import android.content.Context;
import android.content.Intent;
import androidx.core.os.EnvironmentCompat;
import com.inuker.bluetooth.library.receiver.listener.BluetoothReceiverListener;
import com.inuker.bluetooth.library.receiver.listener.BluetoothStateChangeListener;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothStateReceiver extends AbsBluetoothReceiver {
    private static final String[] ACTIONS = {"android.bluetooth.adapter.action.STATE_CHANGED"};

    private String getStateString(int i) {
        switch (i) {
            case 10:
                return "state_off";
            case 11:
                return "state_turning_on";
            case 12:
                return "state_on";
            case 13:
                return "state_turning_off";
            default:
                return EnvironmentCompat.MEDIA_UNKNOWN;
        }
    }

    protected BluetoothStateReceiver(IReceiverDispatcher iReceiverDispatcher) {
        super(iReceiverDispatcher);
    }

    @Override // com.inuker.bluetooth.library.receiver.AbsBluetoothReceiver
    List<String> getActions() {
        return Arrays.asList(ACTIONS);
    }

    public static BluetoothStateReceiver newInstance(IReceiverDispatcher iReceiverDispatcher) {
        return new BluetoothStateReceiver(iReceiverDispatcher);
    }

    @Override // com.inuker.bluetooth.library.receiver.AbsBluetoothReceiver
    public boolean onReceive(Context context, Intent intent) {
        int intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", 0);
        int intExtra2 = intent.getIntExtra("android.bluetooth.adapter.extra.PREVIOUS_STATE", 0);
        BluetoothLog.v(String.format("state changed: %s -> %s", getStateString(intExtra2), getStateString(intExtra)));
        onBluetoothStateChanged(intExtra2, intExtra);
        return true;
    }

    private void onBluetoothStateChanged(int i, int i2) {
        Iterator<BluetoothReceiverListener> it = getListeners(BluetoothStateChangeListener.class).iterator();
        while (it.hasNext()) {
            it.next().invoke(Integer.valueOf(i), Integer.valueOf(i2));
        }
    }
}
