package com.inuker.bluetooth.library.connect.response;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import com.inuker.bluetooth.library.IResponse;

/* JADX INFO: loaded from: classes.dex */
public abstract class BluetoothResponse extends IResponse.Stub implements Handler.Callback {
    private static final int MSG_RESPONSE = 1;
    private Handler mHandler;

    protected abstract void onAsyncResponse(int i, Bundle bundle);

    protected BluetoothResponse() {
        if (Looper.myLooper() == null) {
            throw new RuntimeException();
        }
        this.mHandler = new Handler(Looper.myLooper(), this);
    }

    @Override // com.inuker.bluetooth.library.IResponse
    public void onResponse(int i, Bundle bundle) throws RemoteException {
        this.mHandler.obtainMessage(1, i, 0, bundle).sendToTarget();
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        if (message.what == 1) {
            onAsyncResponse(message.arg1, (Bundle) message.obj);
        }
        return true;
    }
}
