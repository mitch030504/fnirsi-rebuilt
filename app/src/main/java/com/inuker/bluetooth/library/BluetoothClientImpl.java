package com.inuker.bluetooth.library;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import com.inuker.bluetooth.library.IBluetoothService;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleReadRssiResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.connect.response.BluetoothResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.receiver.BluetoothReceiver;
import com.inuker.bluetooth.library.receiver.listener.BleCharacterChangeListener;
import com.inuker.bluetooth.library.receiver.listener.BleConnectStatusChangeListener;
import com.inuker.bluetooth.library.receiver.listener.BluetoothBondListener;
import com.inuker.bluetooth.library.receiver.listener.BluetoothBondStateChangeListener;
import com.inuker.bluetooth.library.receiver.listener.BluetoothStateChangeListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.ListUtils;
import com.inuker.bluetooth.library.utils.proxy.ProxyBulk;
import com.inuker.bluetooth.library.utils.proxy.ProxyInterceptor;
import com.inuker.bluetooth.library.utils.proxy.ProxyUtils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothClientImpl implements IBluetoothClient, ProxyInterceptor, Handler.Callback {
    private static final int MSG_INVOKE_PROXY = 1;
    private static final int MSG_REG_RECEIVER = 2;
    private static final String TAG = BluetoothClientImpl.class.getSimpleName();
    private static IBluetoothClient sInstance;
    private List<BluetoothBondListener> mBluetoothBondListeners;
    private IBluetoothService mBluetoothService;
    private List<BluetoothStateListener> mBluetoothStateListeners;
    private HashMap<String, List<BleConnectStatusListener>> mConnectStatusListeners;
    private final ServiceConnection mConnection = new ServiceConnection() { // from class: com.inuker.bluetooth.library.BluetoothClientImpl.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BluetoothClientImpl.this.mBluetoothService = IBluetoothService.Stub.asInterface(iBinder);
            BluetoothClientImpl.this.notifyBluetoothManagerReady();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            BluetoothClientImpl.this.mBluetoothService = null;
        }
    };
    private Context mContext;
    private CountDownLatch mCountDownLatch;
    private HashMap<String, HashMap<String, List<BleNotifyResponse>>> mNotifyResponses;
    private Handler mWorkerHandler;
    private HandlerThread mWorkerThread;

    private BluetoothClientImpl(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        BluetoothContext.set(applicationContext);
        HandlerThread handlerThread = new HandlerThread(TAG);
        this.mWorkerThread = handlerThread;
        handlerThread.start();
        this.mWorkerHandler = new Handler(this.mWorkerThread.getLooper(), this);
        this.mNotifyResponses = new HashMap<>();
        this.mConnectStatusListeners = new HashMap<>();
        this.mBluetoothStateListeners = new LinkedList();
        this.mBluetoothBondListeners = new LinkedList();
        this.mWorkerHandler.obtainMessage(2).sendToTarget();
    }

    public static IBluetoothClient getInstance(Context context) {
        if (sInstance == null) {
            synchronized (BluetoothClientImpl.class) {
                if (sInstance == null) {
                    BluetoothClientImpl bluetoothClientImpl = new BluetoothClientImpl(context);
                    sInstance = (IBluetoothClient) ProxyUtils.getProxy(bluetoothClientImpl, IBluetoothClient.class, bluetoothClientImpl);
                }
            }
        }
        return sInstance;
    }

    private IBluetoothService getBluetoothService() {
        if (this.mBluetoothService == null) {
            bindServiceSync();
        }
        return this.mBluetoothService;
    }

    private void bindServiceSync() {
        checkRuntime(true);
        this.mCountDownLatch = new CountDownLatch(1);
        Intent intent = new Intent();
        intent.setClass(this.mContext, BluetoothService.class);
        if (this.mContext.bindService(intent, this.mConnection, 1)) {
            waitBluetoothManagerReady();
        } else {
            this.mBluetoothService = BluetoothServiceImpl.getInstance();
        }
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void connect(String str, BleConnectOptions bleConnectOptions, final BleConnectResponse bleConnectResponse) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_MAC, str);
        bundle.putParcelable(Constants.EXTRA_OPTIONS, bleConnectOptions);
        safeCallBluetoothApi(1, bundle, new BluetoothResponse() { // from class: com.inuker.bluetooth.library.BluetoothClientImpl.2
            @Override // com.inuker.bluetooth.library.connect.response.BluetoothResponse
            protected void onAsyncResponse(int i, Bundle bundle2) {
                BluetoothClientImpl.this.checkRuntime(true);
                if (bleConnectResponse != null) {
                    bundle2.setClassLoader(getClass().getClassLoader());
                    bleConnectResponse.onResponse(i, (BleGattProfile) bundle2.getParcelable(Constants.EXTRA_GATT_PROFILE));
                }
            }
        });
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void disconnect(String str) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_MAC, str);
        safeCallBluetoothApi(2, bundle, null);
        clearNotifyListener(str);
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void registerConnectStatusListener(String str, BleConnectStatusListener bleConnectStatusListener) {
        checkRuntime(true);
        List<BleConnectStatusListener> arrayList = this.mConnectStatusListeners.get(str);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            this.mConnectStatusListeners.put(str, arrayList);
        }
        if (bleConnectStatusListener == null || arrayList.contains(bleConnectStatusListener)) {
            return;
        }
        arrayList.add(bleConnectStatusListener);
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void unregisterConnectStatusListener(String str, BleConnectStatusListener bleConnectStatusListener) {
        checkRuntime(true);
        List<BleConnectStatusListener> list = this.mConnectStatusListeners.get(str);
        if (bleConnectStatusListener == null || ListUtils.isEmpty(list)) {
            return;
        }
        list.remove(bleConnectStatusListener);
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void read(String str, UUID uuid, UUID uuid2, final BleReadResponse bleReadResponse) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_MAC, str);
        bundle.putSerializable(Constants.EXTRA_SERVICE_UUID, uuid);
        bundle.putSerializable(Constants.EXTRA_CHARACTER_UUID, uuid2);
        safeCallBluetoothApi(3, bundle, new BluetoothResponse() { // from class: com.inuker.bluetooth.library.BluetoothClientImpl.3
            @Override // com.inuker.bluetooth.library.connect.response.BluetoothResponse
            protected void onAsyncResponse(int i, Bundle bundle2) {
                BluetoothClientImpl.this.checkRuntime(true);
                BleReadResponse bleReadResponse2 = bleReadResponse;
                if (bleReadResponse2 != null) {
                    bleReadResponse2.onResponse(i, bundle2.getByteArray(Constants.EXTRA_BYTE_VALUE));
                }
            }
        });
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void write(String str, UUID uuid, UUID uuid2, byte[] bArr, final BleWriteResponse bleWriteResponse) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_MAC, str);
        bundle.putSerializable(Constants.EXTRA_SERVICE_UUID, uuid);
        bundle.putSerializable(Constants.EXTRA_CHARACTER_UUID, uuid2);
        bundle.putByteArray(Constants.EXTRA_BYTE_VALUE, bArr);
        safeCallBluetoothApi(4, bundle, new BluetoothResponse() { // from class: com.inuker.bluetooth.library.BluetoothClientImpl.4
            @Override // com.inuker.bluetooth.library.connect.response.BluetoothResponse
            protected void onAsyncResponse(int i, Bundle bundle2) {
                BluetoothClientImpl.this.checkRuntime(true);
                BleWriteResponse bleWriteResponse2 = bleWriteResponse;
                if (bleWriteResponse2 != null) {
                    bleWriteResponse2.onResponse(i);
                }
            }
        });
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void readDescriptor(String str, UUID uuid, UUID uuid2, UUID uuid3, final BleReadResponse bleReadResponse) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_MAC, str);
        bundle.putSerializable(Constants.EXTRA_SERVICE_UUID, uuid);
        bundle.putSerializable(Constants.EXTRA_CHARACTER_UUID, uuid2);
        bundle.putSerializable(Constants.EXTRA_DESCRIPTOR_UUID, uuid3);
        safeCallBluetoothApi(13, bundle, new BluetoothResponse() { // from class: com.inuker.bluetooth.library.BluetoothClientImpl.5
            @Override // com.inuker.bluetooth.library.connect.response.BluetoothResponse
            protected void onAsyncResponse(int i, Bundle bundle2) {
                BluetoothClientImpl.this.checkRuntime(true);
                BleReadResponse bleReadResponse2 = bleReadResponse;
                if (bleReadResponse2 != null) {
                    bleReadResponse2.onResponse(i, bundle2.getByteArray(Constants.EXTRA_BYTE_VALUE));
                }
            }
        });
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void writeDescriptor(String str, UUID uuid, UUID uuid2, UUID uuid3, byte[] bArr, final BleWriteResponse bleWriteResponse) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_MAC, str);
        bundle.putSerializable(Constants.EXTRA_SERVICE_UUID, uuid);
        bundle.putSerializable(Constants.EXTRA_CHARACTER_UUID, uuid2);
        bundle.putSerializable(Constants.EXTRA_DESCRIPTOR_UUID, uuid3);
        bundle.putByteArray(Constants.EXTRA_BYTE_VALUE, bArr);
        safeCallBluetoothApi(14, bundle, new BluetoothResponse() { // from class: com.inuker.bluetooth.library.BluetoothClientImpl.6
            @Override // com.inuker.bluetooth.library.connect.response.BluetoothResponse
            protected void onAsyncResponse(int i, Bundle bundle2) {
                BluetoothClientImpl.this.checkRuntime(true);
                BleWriteResponse bleWriteResponse2 = bleWriteResponse;
                if (bleWriteResponse2 != null) {
                    bleWriteResponse2.onResponse(i);
                }
            }
        });
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void writeNoRsp(String str, UUID uuid, UUID uuid2, byte[] bArr, final BleWriteResponse bleWriteResponse) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_MAC, str);
        bundle.putSerializable(Constants.EXTRA_SERVICE_UUID, uuid);
        bundle.putSerializable(Constants.EXTRA_CHARACTER_UUID, uuid2);
        bundle.putByteArray(Constants.EXTRA_BYTE_VALUE, bArr);
        safeCallBluetoothApi(5, bundle, new BluetoothResponse() { // from class: com.inuker.bluetooth.library.BluetoothClientImpl.7
            @Override // com.inuker.bluetooth.library.connect.response.BluetoothResponse
            protected void onAsyncResponse(int i, Bundle bundle2) {
                BluetoothClientImpl.this.checkRuntime(true);
                BleWriteResponse bleWriteResponse2 = bleWriteResponse;
                if (bleWriteResponse2 != null) {
                    bleWriteResponse2.onResponse(i);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveNotifyListener(String str, UUID uuid, UUID uuid2, BleNotifyResponse bleNotifyResponse) {
        checkRuntime(true);
        HashMap<String, List<BleNotifyResponse>> map = this.mNotifyResponses.get(str);
        if (map == null) {
            map = new HashMap<>();
            this.mNotifyResponses.put(str, map);
        }
        String strGenerateCharacterKey = generateCharacterKey(uuid, uuid2);
        List<BleNotifyResponse> arrayList = map.get(strGenerateCharacterKey);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            map.put(strGenerateCharacterKey, arrayList);
        }
        arrayList.add(bleNotifyResponse);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeNotifyListener(String str, UUID uuid, UUID uuid2) {
        checkRuntime(true);
        HashMap<String, List<BleNotifyResponse>> map = this.mNotifyResponses.get(str);
        if (map != null) {
            map.remove(generateCharacterKey(uuid, uuid2));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearNotifyListener(String str) {
        checkRuntime(true);
        this.mNotifyResponses.remove(str);
    }

    private String generateCharacterKey(UUID uuid, UUID uuid2) {
        return String.format("%s_%s", uuid, uuid2);
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void notify(final String str, final UUID uuid, final UUID uuid2, final BleNotifyResponse bleNotifyResponse) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_MAC, str);
        bundle.putSerializable(Constants.EXTRA_SERVICE_UUID, uuid);
        bundle.putSerializable(Constants.EXTRA_CHARACTER_UUID, uuid2);
        safeCallBluetoothApi(6, bundle, new BluetoothResponse() { // from class: com.inuker.bluetooth.library.BluetoothClientImpl.8
            @Override // com.inuker.bluetooth.library.connect.response.BluetoothResponse
            protected void onAsyncResponse(int i, Bundle bundle2) {
                BluetoothClientImpl.this.checkRuntime(true);
                BleNotifyResponse bleNotifyResponse2 = bleNotifyResponse;
                if (bleNotifyResponse2 != null) {
                    if (i == 0) {
                        BluetoothClientImpl.this.saveNotifyListener(str, uuid, uuid2, bleNotifyResponse2);
                    }
                    bleNotifyResponse.onResponse(i);
                }
            }
        });
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void unnotify(final String str, final UUID uuid, final UUID uuid2, final BleUnnotifyResponse bleUnnotifyResponse) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_MAC, str);
        bundle.putSerializable(Constants.EXTRA_SERVICE_UUID, uuid);
        bundle.putSerializable(Constants.EXTRA_CHARACTER_UUID, uuid2);
        safeCallBluetoothApi(7, bundle, new BluetoothResponse() { // from class: com.inuker.bluetooth.library.BluetoothClientImpl.9
            @Override // com.inuker.bluetooth.library.connect.response.BluetoothResponse
            protected void onAsyncResponse(int i, Bundle bundle2) {
                BluetoothClientImpl.this.checkRuntime(true);
                BluetoothClientImpl.this.removeNotifyListener(str, uuid, uuid2);
                BleUnnotifyResponse bleUnnotifyResponse2 = bleUnnotifyResponse;
                if (bleUnnotifyResponse2 != null) {
                    bleUnnotifyResponse2.onResponse(i);
                }
            }
        });
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void indicate(final String str, final UUID uuid, final UUID uuid2, final BleNotifyResponse bleNotifyResponse) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_MAC, str);
        bundle.putSerializable(Constants.EXTRA_SERVICE_UUID, uuid);
        bundle.putSerializable(Constants.EXTRA_CHARACTER_UUID, uuid2);
        safeCallBluetoothApi(10, bundle, new BluetoothResponse() { // from class: com.inuker.bluetooth.library.BluetoothClientImpl.10
            @Override // com.inuker.bluetooth.library.connect.response.BluetoothResponse
            protected void onAsyncResponse(int i, Bundle bundle2) {
                BluetoothClientImpl.this.checkRuntime(true);
                BleNotifyResponse bleNotifyResponse2 = bleNotifyResponse;
                if (bleNotifyResponse2 != null) {
                    if (i == 0) {
                        BluetoothClientImpl.this.saveNotifyListener(str, uuid, uuid2, bleNotifyResponse2);
                    }
                    bleNotifyResponse.onResponse(i);
                }
            }
        });
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void unindicate(String str, UUID uuid, UUID uuid2, BleUnnotifyResponse bleUnnotifyResponse) {
        unnotify(str, uuid, uuid2, bleUnnotifyResponse);
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void readRssi(String str, final BleReadRssiResponse bleReadRssiResponse) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_MAC, str);
        safeCallBluetoothApi(8, bundle, new BluetoothResponse() { // from class: com.inuker.bluetooth.library.BluetoothClientImpl.11
            @Override // com.inuker.bluetooth.library.connect.response.BluetoothResponse
            protected void onAsyncResponse(int i, Bundle bundle2) {
                BluetoothClientImpl.this.checkRuntime(true);
                BleReadRssiResponse bleReadRssiResponse2 = bleReadRssiResponse;
                if (bleReadRssiResponse2 != null) {
                    bleReadRssiResponse2.onResponse(i, Integer.valueOf(bundle2.getInt(Constants.EXTRA_RSSI, 0)));
                }
            }
        });
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void search(SearchRequest searchRequest, final SearchResponse searchResponse) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.EXTRA_REQUEST, searchRequest);
        safeCallBluetoothApi(11, bundle, new BluetoothResponse() { // from class: com.inuker.bluetooth.library.BluetoothClientImpl.12
            @Override // com.inuker.bluetooth.library.connect.response.BluetoothResponse
            protected void onAsyncResponse(int i, Bundle bundle2) {
                BluetoothClientImpl.this.checkRuntime(true);
                if (searchResponse == null) {
                    return;
                }
                bundle2.setClassLoader(getClass().getClassLoader());
                if (i == 1) {
                    searchResponse.onSearchStarted();
                    return;
                }
                if (i == 2) {
                    searchResponse.onSearchStopped();
                    return;
                }
                if (i == 3) {
                    searchResponse.onSearchCanceled();
                } else {
                    if (i == 4) {
                        searchResponse.onDeviceFounded((SearchResult) bundle2.getParcelable(Constants.EXTRA_SEARCH_RESULT));
                        return;
                    }
                    throw new IllegalStateException("unknown code");
                }
            }
        });
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void stopSearch() {
        safeCallBluetoothApi(12, null, null);
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void registerBluetoothStateListener(BluetoothStateListener bluetoothStateListener) {
        checkRuntime(true);
        if (bluetoothStateListener == null || this.mBluetoothStateListeners.contains(bluetoothStateListener)) {
            return;
        }
        this.mBluetoothStateListeners.add(bluetoothStateListener);
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void unregisterBluetoothStateListener(BluetoothStateListener bluetoothStateListener) {
        checkRuntime(true);
        if (bluetoothStateListener != null) {
            this.mBluetoothStateListeners.remove(bluetoothStateListener);
        }
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void registerBluetoothBondListener(BluetoothBondListener bluetoothBondListener) {
        checkRuntime(true);
        if (bluetoothBondListener == null || this.mBluetoothBondListeners.contains(bluetoothBondListener)) {
            return;
        }
        this.mBluetoothBondListeners.add(bluetoothBondListener);
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void unregisterBluetoothBondListener(BluetoothBondListener bluetoothBondListener) {
        checkRuntime(true);
        if (bluetoothBondListener != null) {
            this.mBluetoothBondListeners.remove(bluetoothBondListener);
        }
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void clearRequest(String str, int i) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_MAC, str);
        bundle.putInt(Constants.EXTRA_TYPE, i);
        safeCallBluetoothApi(20, bundle, null);
    }

    @Override // com.inuker.bluetooth.library.IBluetoothClient
    public void refreshCache(String str) {
        checkRuntime(true);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_MAC, str);
        safeCallBluetoothApi(21, bundle, null);
    }

    private void safeCallBluetoothApi(int i, Bundle bundle, BluetoothResponse bluetoothResponse) {
        checkRuntime(true);
        try {
            IBluetoothService bluetoothService = getBluetoothService();
            if (bluetoothService != null) {
                if (bundle == null) {
                    bundle = new Bundle();
                }
                bluetoothService.callBluetoothApi(i, bundle, bluetoothResponse);
                return;
            }
            bluetoothResponse.onResponse(-6, null);
        } catch (Throwable th) {
            BluetoothLog.e(th);
        }
    }

    @Override // com.inuker.bluetooth.library.utils.proxy.ProxyInterceptor
    public boolean onIntercept(Object obj, Method method, Object[] objArr) {
        this.mWorkerHandler.obtainMessage(1, new ProxyBulk(obj, method, objArr)).sendToTarget();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyBluetoothManagerReady() {
        CountDownLatch countDownLatch = this.mCountDownLatch;
        if (countDownLatch != null) {
            countDownLatch.countDown();
            this.mCountDownLatch = null;
        }
    }

    private void waitBluetoothManagerReady() {
        try {
            this.mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        int i = message.what;
        if (i == 1) {
            ProxyBulk.safeInvoke(message.obj);
        } else if (i == 2) {
            registerBluetoothReceiver();
        }
        return true;
    }

    private void registerBluetoothReceiver() {
        checkRuntime(true);
        BluetoothReceiver.getInstance().register(new BluetoothStateChangeListener() { // from class: com.inuker.bluetooth.library.BluetoothClientImpl.13
            @Override // com.inuker.bluetooth.library.receiver.listener.BluetoothStateChangeListener
            protected void onBluetoothStateChanged(int i, int i2) {
                BluetoothClientImpl.this.checkRuntime(true);
                BluetoothClientImpl.this.dispatchBluetoothStateChanged(i2);
            }
        });
        BluetoothReceiver.getInstance().register(new BluetoothBondStateChangeListener() { // from class: com.inuker.bluetooth.library.BluetoothClientImpl.14
            @Override // com.inuker.bluetooth.library.receiver.listener.BluetoothBondStateChangeListener
            protected void onBondStateChanged(String str, int i) {
                BluetoothClientImpl.this.checkRuntime(true);
                BluetoothClientImpl.this.dispatchBondStateChanged(str, i);
            }
        });
        BluetoothReceiver.getInstance().register(new BleConnectStatusChangeListener() { // from class: com.inuker.bluetooth.library.BluetoothClientImpl.15
            @Override // com.inuker.bluetooth.library.receiver.listener.BleConnectStatusChangeListener
            protected void onConnectStatusChanged(String str, int i) {
                BluetoothClientImpl.this.checkRuntime(true);
                if (i == 32) {
                    BluetoothClientImpl.this.clearNotifyListener(str);
                }
                BluetoothClientImpl.this.dispatchConnectionStatus(str, i);
            }
        });
        BluetoothReceiver.getInstance().register(new BleCharacterChangeListener() { // from class: com.inuker.bluetooth.library.BluetoothClientImpl.16
            @Override // com.inuker.bluetooth.library.receiver.listener.BleCharacterChangeListener
            public void onCharacterChanged(String str, UUID uuid, UUID uuid2, byte[] bArr) {
                BluetoothClientImpl.this.checkRuntime(true);
                BluetoothClientImpl.this.dispatchCharacterNotify(str, uuid, uuid2, bArr);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchCharacterNotify(String str, UUID uuid, UUID uuid2, byte[] bArr) {
        List<BleNotifyResponse> list;
        checkRuntime(true);
        HashMap<String, List<BleNotifyResponse>> map = this.mNotifyResponses.get(str);
        if (map == null || (list = map.get(generateCharacterKey(uuid, uuid2))) == null) {
            return;
        }
        Iterator<BleNotifyResponse> it = list.iterator();
        while (it.hasNext()) {
            it.next().onNotify(uuid, uuid2, bArr);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchConnectionStatus(String str, int i) {
        checkRuntime(true);
        List<BleConnectStatusListener> list = this.mConnectStatusListeners.get(str);
        if (ListUtils.isEmpty(list)) {
            return;
        }
        Iterator<BleConnectStatusListener> it = list.iterator();
        while (it.hasNext()) {
            it.next().invokeSync(str, Integer.valueOf(i));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchBluetoothStateChanged(int i) {
        checkRuntime(true);
        if (i == 10 || i == 12) {
            for (BluetoothStateListener bluetoothStateListener : this.mBluetoothStateListeners) {
                Object[] objArr = new Object[1];
                objArr[0] = Boolean.valueOf(i == 12);
                bluetoothStateListener.invokeSync(objArr);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchBondStateChanged(String str, int i) {
        checkRuntime(true);
        Iterator<BluetoothBondListener> it = this.mBluetoothBondListeners.iterator();
        while (it.hasNext()) {
            it.next().invokeSync(str, Integer.valueOf(i));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkRuntime(boolean z) {
        if (Looper.myLooper() != (z ? this.mWorkerHandler.getLooper() : Looper.getMainLooper())) {
            throw new RuntimeException();
        }
    }
}
