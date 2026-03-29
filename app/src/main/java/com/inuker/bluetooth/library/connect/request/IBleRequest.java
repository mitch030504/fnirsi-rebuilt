package com.inuker.bluetooth.library.connect.request;

import com.inuker.bluetooth.library.connect.IBleConnectDispatcher;

/* JADX INFO: loaded from: classes.dex */
public interface IBleRequest {
    void cancel();

    void process(IBleConnectDispatcher iBleConnectDispatcher);
}
