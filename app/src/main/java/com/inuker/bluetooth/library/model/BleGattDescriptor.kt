package com.inuker.bluetooth.library.model

import android.bluetooth.BluetoothGattDescriptor
import android.os.Parcel
import android.os.ParcelUuid
import android.os.Parcelable
import java.util.Arrays

class BleGattDescriptor : Parcelable {
    private var uuidInternal: ParcelUuid
    private var permissionsInternal: Int
    private var valueInternal: ByteArray?

    constructor(bluetoothGattDescriptor: BluetoothGattDescriptor) {
        uuidInternal = ParcelUuid(bluetoothGattDescriptor.uuid)
        permissionsInternal = bluetoothGattDescriptor.permissions
        valueInternal = bluetoothGattDescriptor.value
    }

    private constructor(parcel: Parcel) {
        uuidInternal = parcel.readParcelable(ParcelUuid::class.java.classLoader) ?: ParcelUuid.fromString("00000000-0000-0000-0000-000000000000")
        permissionsInternal = parcel.readInt()
        valueInternal = parcel.createByteArray()
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(uuidInternal, flags)
        parcel.writeInt(permissionsInternal)
        parcel.writeByteArray(valueInternal)
    }

    fun getmUuid(): ParcelUuid = uuidInternal

    fun setmUuid(value: ParcelUuid) {
        uuidInternal = value
    }

    fun getmPermissions(): Int = permissionsInternal

    fun setmPermissions(value: Int) {
        permissionsInternal = value
    }

    fun getmValue(): ByteArray? = valueInternal

    fun setmValue(value: ByteArray?) {
        valueInternal = value
    }

    override fun toString(): String {
        return "BleGattDescriptor{mUuid=$uuidInternal, mPermissions=$permissionsInternal, mValue=${Arrays.toString(valueInternal)}}"
    }

    companion object CREATOR : Parcelable.Creator<BleGattDescriptor> {
        override fun createFromParcel(parcel: Parcel): BleGattDescriptor = BleGattDescriptor(parcel)

        override fun newArray(size: Int): Array<BleGattDescriptor?> = arrayOfNulls(size)
    }
}
