package com.inuker.bluetooth.library.connect.response

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.RemoteException
import com.inuker.bluetooth.library.IResponse

abstract class BluetoothResponse protected constructor() : IResponse.Stub(), Handler.Callback {
    private val handler = Handler(Looper.myLooper() ?: throw RuntimeException(), this)

    protected abstract fun onAsyncResponse(code: Int, bundle: Bundle?)

    @Throws(RemoteException::class)
    override fun onResponse(code: Int, bundle: Bundle?) {
        handler.obtainMessage(MSG_RESPONSE, code, 0, bundle).sendToTarget()
    }

    override fun handleMessage(message: Message): Boolean {
        if (message.what == MSG_RESPONSE) {
            onAsyncResponse(message.arg1, message.obj as? Bundle)
        }
        return true
    }

    private companion object {
        const val MSG_RESPONSE = 1
    }
}
