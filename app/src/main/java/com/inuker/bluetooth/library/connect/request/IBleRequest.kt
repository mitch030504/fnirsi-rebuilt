package com.inuker.bluetooth.library.connect.request

import com.inuker.bluetooth.library.connect.IBleConnectDispatcher

interface IBleRequest {
    fun cancel()

    fun process(dispatcher: IBleConnectDispatcher)
}
