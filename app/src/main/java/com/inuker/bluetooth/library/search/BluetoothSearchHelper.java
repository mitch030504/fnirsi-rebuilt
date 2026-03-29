package com.inuker.bluetooth.library.search;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.inuker.bluetooth.library.search.response.BluetoothSearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.inuker.bluetooth.library.utils.proxy.ProxyBulk;
import com.inuker.bluetooth.library.utils.proxy.ProxyInterceptor;
import com.inuker.bluetooth.library.utils.proxy.ProxyUtils;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothSearchHelper implements IBluetoothSearchHelper, ProxyInterceptor, Handler.Callback {
    private static IBluetoothSearchHelper sInstance;
    private BluetoothSearchRequest mCurrentRequest;
    private Handler mHandler = new Handler(Looper.getMainLooper(), this);

    private BluetoothSearchHelper() {
    }

    public static IBluetoothSearchHelper getInstance() {
        if (sInstance == null) {
            synchronized (BluetoothSearchHelper.class) {
                if (sInstance == null) {
                    BluetoothSearchHelper bluetoothSearchHelper = new BluetoothSearchHelper();
                    sInstance = (IBluetoothSearchHelper) ProxyUtils.getProxy(bluetoothSearchHelper, IBluetoothSearchHelper.class, bluetoothSearchHelper);
                }
            }
        }
        return sInstance;
    }

    @Override // com.inuker.bluetooth.library.search.IBluetoothSearchHelper
    public void startSearch(BluetoothSearchRequest bluetoothSearchRequest, BluetoothSearchResponse bluetoothSearchResponse) {
        bluetoothSearchRequest.setSearchResponse(new BluetoothSearchResponseImpl(bluetoothSearchResponse));
        if (!BluetoothUtils.isBluetoothEnabled()) {
            bluetoothSearchRequest.cancel();
            return;
        }
        stopSearch();
        if (this.mCurrentRequest == null) {
            this.mCurrentRequest = bluetoothSearchRequest;
            bluetoothSearchRequest.start();
        }
    }

    private class BluetoothSearchResponseImpl implements BluetoothSearchResponse {
        BluetoothSearchResponse response;

        BluetoothSearchResponseImpl(BluetoothSearchResponse bluetoothSearchResponse) {
            this.response = bluetoothSearchResponse;
        }

        @Override // com.inuker.bluetooth.library.search.response.BluetoothSearchResponse
        public void onSearchStarted() {
            this.response.onSearchStarted();
        }

        @Override // com.inuker.bluetooth.library.search.response.BluetoothSearchResponse
        public void onDeviceFounded(SearchResult searchResult) {
            this.response.onDeviceFounded(searchResult);
        }

        @Override // com.inuker.bluetooth.library.search.response.BluetoothSearchResponse
        public void onSearchStopped() {
            this.response.onSearchStopped();
            BluetoothSearchHelper.this.mCurrentRequest = null;
        }

        @Override // com.inuker.bluetooth.library.search.response.BluetoothSearchResponse
        public void onSearchCanceled() {
            this.response.onSearchCanceled();
            BluetoothSearchHelper.this.mCurrentRequest = null;
        }
    }

    @Override // com.inuker.bluetooth.library.search.IBluetoothSearchHelper
    public void stopSearch() {
        BluetoothSearchRequest bluetoothSearchRequest = this.mCurrentRequest;
        if (bluetoothSearchRequest != null) {
            bluetoothSearchRequest.cancel();
            this.mCurrentRequest = null;
        }
    }

    @Override // com.inuker.bluetooth.library.utils.proxy.ProxyInterceptor
    public boolean onIntercept(Object obj, Method method, Object[] objArr) {
        this.mHandler.obtainMessage(0, new ProxyBulk(obj, method, objArr)).sendToTarget();
        return true;
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        ProxyBulk.safeInvoke(message.obj);
        return true;
    }
}
