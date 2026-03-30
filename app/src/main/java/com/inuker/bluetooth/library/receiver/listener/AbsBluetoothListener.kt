package com.inuker.bluetooth.library.receiver.listener

import android.os.Handler
import android.os.Looper
import android.os.Message

abstract class AbsBluetoothListener : Handler.Callback {
    private val handler: Handler
    private val syncHandler: Handler

    init {
        val looper = Looper.myLooper() ?: throw IllegalStateException()
        handler = Handler(looper, this)
        syncHandler = Handler(Looper.getMainLooper(), this)
    }

    abstract fun onInvoke(vararg objArr: Any?)

    abstract fun onSyncInvoke(vararg objArr: Any?)

    override fun handleMessage(message: Message): Boolean {
        @Suppress("UNCHECKED_CAST")
        val args = message.obj as? Array<out Any?> ?: emptyArray()
        when (message.what) {
            MSG_INVOKE -> onInvoke(*args)
            MSG_SYNC_INVOKE -> onSyncInvoke(*args)
        }
        return true
    }

    fun invoke(vararg objArr: Any?) {
        handler.obtainMessage(MSG_INVOKE, objArr).sendToTarget()
    }

    fun invokeSync(vararg objArr: Any?) {
        syncHandler.obtainMessage(MSG_SYNC_INVOKE, objArr).sendToTarget()
    }

    private companion object {
        const val MSG_INVOKE = 1
        const val MSG_SYNC_INVOKE = 2
    }
}
