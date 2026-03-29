package com.inuker.bluetooth.library;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import com.inuker.bluetooth.library.IBluetoothService;
import com.inuker.bluetooth.library.connect.BleConnectManager;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse;
import com.inuker.bluetooth.library.search.BluetoothSearchManager;
import com.inuker.bluetooth.library.search.SearchRequest;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothServiceImpl extends IBluetoothService.Stub implements Handler.Callback {
    private static BluetoothServiceImpl sInstance;
    private Handler mHandler = new Handler(Looper.getMainLooper(), this);

    private BluetoothServiceImpl() {
    }

    public static BluetoothServiceImpl getInstance() {
        if (sInstance == null) {
            synchronized (BluetoothServiceImpl.class) {
                if (sInstance == null) {
                    sInstance = new BluetoothServiceImpl();
                }
            }
        }
        return sInstance;
    }

    @Override // com.inuker.bluetooth.library.IBluetoothService
    public void callBluetoothApi(int i, Bundle bundle, final IResponse iResponse) throws RemoteException {
        Message messageObtainMessage = this.mHandler.obtainMessage(i, new BleGeneralResponse() { // from class: com.inuker.bluetooth.library.BluetoothServiceImpl.1
            @Override // com.inuker.bluetooth.library.connect.response.BleTResponse
            public void onResponse(int i2, Bundle bundle2) {
                if (iResponse != null) {
                    if (bundle2 == null) {
                        bundle2 = new Bundle();
                    }
                    try {
                        iResponse.onResponse(i2, bundle2);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        bundle.setClassLoader(getClass().getClassLoader());
        messageObtainMessage.setData(bundle);
        messageObtainMessage.sendToTarget();
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        Bundle data = message.getData();
        String string = data.getString(Constants.EXTRA_MAC);
        UUID uuid = (UUID) data.getSerializable(Constants.EXTRA_SERVICE_UUID);
        UUID uuid2 = (UUID) data.getSerializable(Constants.EXTRA_CHARACTER_UUID);
        UUID uuid3 = (UUID) data.getSerializable(Constants.EXTRA_DESCRIPTOR_UUID);
        byte[] byteArray = data.getByteArray(Constants.EXTRA_BYTE_VALUE);
        BleGeneralResponse bleGeneralResponse = (BleGeneralResponse) message.obj;
        switch (message.what) {
            case 1:
                BleConnectManager.connect(string, (BleConnectOptions) data.getParcelable(Constants.EXTRA_OPTIONS), bleGeneralResponse);
                break;
            case 2:
                BleConnectManager.disconnect(string);
                break;
            case 3:
                BleConnectManager.read(string, uuid, uuid2, bleGeneralResponse);
                break;
            case 4:
                BleConnectManager.write(string, uuid, uuid2, byteArray, bleGeneralResponse);
                break;
            case 5:
                BleConnectManager.writeNoRsp(string, uuid, uuid2, byteArray, bleGeneralResponse);
                break;
            case 6:
                BleConnectManager.notify(string, uuid, uuid2, bleGeneralResponse);
                break;
            case 7:
                BleConnectManager.unnotify(string, uuid, uuid2, bleGeneralResponse);
                break;
            case 8:
                BleConnectManager.readRssi(string, bleGeneralResponse);
                break;
            case 10:
                BleConnectManager.indicate(string, uuid, uuid2, bleGeneralResponse);
                break;
            case 11:
                BluetoothSearchManager.search((SearchRequest) data.getParcelable(Constants.EXTRA_REQUEST), bleGeneralResponse);
                break;
            case 12:
                BluetoothSearchManager.stopSearch();
                break;
            case 13:
                BleConnectManager.readDescriptor(string, uuid, uuid2, uuid3, bleGeneralResponse);
                break;
            case 14:
                BleConnectManager.writeDescriptor(string, uuid, uuid2, uuid3, byteArray, bleGeneralResponse);
                break;
            case 20:
                BleConnectManager.clearRequest(string, data.getInt(Constants.EXTRA_TYPE, 0));
                break;
            case 21:
                BleConnectManager.refreshCache(string);
                break;
        }
        return true;
    }
}
