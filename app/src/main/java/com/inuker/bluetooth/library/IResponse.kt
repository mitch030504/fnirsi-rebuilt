package com.inuker.bluetooth.library

import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import android.os.RemoteException

interface IResponse : IInterface {
    @Throws(RemoteException::class)
    fun onResponse(code: Int, bundle: Bundle?)

    abstract class Stub : Binder(), IResponse {
        init {
            attachInterface(this, DESCRIPTOR)
        }

        override fun asBinder(): IBinder = this

        @Throws(RemoteException::class)
        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            if (code == IBinder.INTERFACE_TRANSACTION) {
                reply?.writeString(DESCRIPTOR)
                return true
            }
            if (code != TRANSACTION_ON_RESPONSE) {
                return super.onTransact(code, data, reply, flags)
            }

            data.enforceInterface(DESCRIPTOR)
            val responseCode = data.readInt()
            val bundle = if (data.readInt() != 0) Bundle.CREATOR.createFromParcel(data) else null
            onResponse(responseCode, bundle)
            reply?.writeNoException()
            if (bundle != null) {
                reply?.writeInt(1)
                bundle.writeToParcel(requireNotNull(reply), 1)
            } else {
                reply?.writeInt(0)
            }
            return true
        }

        private class Proxy(
            private val remote: IBinder,
        ) : IResponse {
            override fun asBinder(): IBinder = remote

            @Throws(RemoteException::class)
            override fun onResponse(code: Int, bundle: Bundle?) {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    data.writeInt(code)
                    if (bundle != null) {
                        data.writeInt(1)
                        bundle.writeToParcel(data, 0)
                    } else {
                        data.writeInt(0)
                    }
                    remote.transact(TRANSACTION_ON_RESPONSE, data, reply, 0)
                    reply.readException()
                    if (reply.readInt() != 0) {
                        bundle?.readFromParcel(reply)
                    }
                } finally {
                    reply.recycle()
                    data.recycle()
                }
            }
        }

        companion object {
            private const val DESCRIPTOR = "com.inuker.bluetooth.library.IResponse"
            private const val TRANSACTION_ON_RESPONSE = IBinder.FIRST_CALL_TRANSACTION

            @JvmStatic
            fun asInterface(binder: IBinder?): IResponse? {
                if (binder == null) {
                    return null
                }
                val localInterface = binder.queryLocalInterface(DESCRIPTOR)
                return if (localInterface is IResponse) localInterface else Proxy(binder)
            }
        }
    }
}
