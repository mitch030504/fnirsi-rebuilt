package com.inuker.bluetooth.library

import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import android.os.Parcelable
import android.os.RemoteException

interface IBluetoothService : IInterface {
    @Throws(RemoteException::class)
    fun callBluetoothApi(type: Int, bundle: Bundle?, response: IResponse?)

    abstract class Stub : Binder(), IBluetoothService {
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
            if (code != TRANSACTION_CALL_BLUETOOTH_API) {
                return super.onTransact(code, data, reply, flags)
            }

            data.enforceInterface(DESCRIPTOR)
            val type = data.readInt()
            val bundle = if (data.readInt() != 0) Bundle.CREATOR.createFromParcel(data) else null
            val response = IResponse.Stub.asInterface(data.readStrongBinder())
            callBluetoothApi(type, bundle, response)
            reply?.writeNoException()
            if (bundle != null) {
                reply?.writeInt(1)
                bundle.writeToParcel(requireNotNull(reply), Parcelable.PARCELABLE_WRITE_RETURN_VALUE)
            } else {
                reply?.writeInt(0)
            }
            return true
        }

        private class Proxy(
            private val remote: IBinder,
        ) : IBluetoothService {
            override fun asBinder(): IBinder = remote

            @Throws(RemoteException::class)
            override fun callBluetoothApi(type: Int, bundle: Bundle?, response: IResponse?) {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    data.writeInt(type)
                    if (bundle != null) {
                        data.writeInt(1)
                        bundle.writeToParcel(data, 0)
                    } else {
                        data.writeInt(0)
                    }
                    data.writeStrongBinder(response?.asBinder())
                    remote.transact(TRANSACTION_CALL_BLUETOOTH_API, data, reply, 0)
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
            private const val DESCRIPTOR = "com.inuker.bluetooth.library.IBluetoothService"
            private const val TRANSACTION_CALL_BLUETOOTH_API = IBinder.FIRST_CALL_TRANSACTION

            @JvmStatic
            fun asInterface(binder: IBinder?): IBluetoothService? {
                if (binder == null) {
                    return null
                }
                val localInterface = binder.queryLocalInterface(DESCRIPTOR)
                return if (localInterface is IBluetoothService) localInterface else Proxy(binder)
            }
        }
    }
}
