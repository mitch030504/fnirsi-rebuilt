package com.inuker.bluetooth.library.connect;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleGeneralResponse;
import com.inuker.bluetooth.library.utils.proxy.ProxyBulk;
import com.inuker.bluetooth.library.utils.proxy.ProxyInterceptor;
import com.inuker.bluetooth.library.utils.proxy.ProxyUtils;
import java.lang.reflect.Method;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class BleConnectMaster implements IBleConnectMaster, ProxyInterceptor, Handler.Callback {
    private String mAddress;
    private BleConnectDispatcher mBleConnectDispatcher;
    private Handler mHandler;

    private BleConnectMaster(String str, Looper looper) {
        this.mAddress = str;
        this.mHandler = new Handler(looper, this);
    }

    private BleConnectDispatcher getConnectDispatcher() {
        if (this.mBleConnectDispatcher == null) {
            this.mBleConnectDispatcher = BleConnectDispatcher.newInstance(this.mAddress);
        }
        return this.mBleConnectDispatcher;
    }

    static IBleConnectMaster newInstance(String str, Looper looper) {
        BleConnectMaster bleConnectMaster = new BleConnectMaster(str, looper);
        return (IBleConnectMaster) ProxyUtils.getProxy(bleConnectMaster, IBleConnectMaster.class, bleConnectMaster);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectMaster
    public void connect(BleConnectOptions bleConnectOptions, BleGeneralResponse bleGeneralResponse) {
        getConnectDispatcher().connect(bleConnectOptions, bleGeneralResponse);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectMaster
    public void disconnect() {
        getConnectDispatcher().disconnect();
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectMaster
    public void read(UUID uuid, UUID uuid2, BleGeneralResponse bleGeneralResponse) {
        getConnectDispatcher().read(uuid, uuid2, bleGeneralResponse);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectMaster
    public void write(UUID uuid, UUID uuid2, byte[] bArr, BleGeneralResponse bleGeneralResponse) {
        getConnectDispatcher().write(uuid, uuid2, bArr, bleGeneralResponse);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectMaster
    public void writeNoRsp(UUID uuid, UUID uuid2, byte[] bArr, BleGeneralResponse bleGeneralResponse) {
        getConnectDispatcher().writeNoRsp(uuid, uuid2, bArr, bleGeneralResponse);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectMaster
    public void readDescriptor(UUID uuid, UUID uuid2, UUID uuid3, BleGeneralResponse bleGeneralResponse) {
        getConnectDispatcher().readDescriptor(uuid, uuid2, uuid3, bleGeneralResponse);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectMaster
    public void writeDescriptor(UUID uuid, UUID uuid2, UUID uuid3, byte[] bArr, BleGeneralResponse bleGeneralResponse) {
        getConnectDispatcher().writeDescriptor(uuid, uuid2, uuid3, bArr, bleGeneralResponse);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectMaster
    public void notify(UUID uuid, UUID uuid2, BleGeneralResponse bleGeneralResponse) {
        getConnectDispatcher().notify(uuid, uuid2, bleGeneralResponse);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectMaster
    public void unnotify(UUID uuid, UUID uuid2, BleGeneralResponse bleGeneralResponse) {
        getConnectDispatcher().unnotify(uuid, uuid2, bleGeneralResponse);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectMaster
    public void readRssi(BleGeneralResponse bleGeneralResponse) {
        getConnectDispatcher().readRemoteRssi(bleGeneralResponse);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectMaster
    public void indicate(UUID uuid, UUID uuid2, BleGeneralResponse bleGeneralResponse) {
        getConnectDispatcher().indicate(uuid, uuid2, bleGeneralResponse);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectMaster
    public void clearRequest(int i) {
        getConnectDispatcher().clearRequest(i);
    }

    @Override // com.inuker.bluetooth.library.connect.IBleConnectMaster
    public void refreshCache() {
        getConnectDispatcher().refreshCache();
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
