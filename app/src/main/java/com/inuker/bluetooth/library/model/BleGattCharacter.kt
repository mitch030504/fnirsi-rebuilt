package com.inuker.bluetooth.library.model

import android.bluetooth.BluetoothGattCharacteristic
import android.os.Parcel
import android.os.ParcelUuid
import android.os.Parcelable
import java.util.UUID

class BleGattCharacter : Parcelable {
    private var uuidInternal: ParcelUuid
    private var propertyInternal: Int
    private var permissionsInternal: Int
    private var descriptorsInternal: MutableList<BleGattDescriptor>? = null

    constructor(characteristic: BluetoothGattCharacteristic) {
        uuidInternal = ParcelUuid(characteristic.uuid)
        propertyInternal = characteristic.properties
        permissionsInternal = characteristic.permissions
        characteristic.descriptors.forEach { getDescriptors().add(BleGattDescriptor(it)) }
    }

    private constructor(parcel: Parcel) {
        uuidInternal = parcel.readParcelable(ParcelUuid::class.java.classLoader) ?: ParcelUuid.fromString("00000000-0000-0000-0000-000000000000")
        propertyInternal = parcel.readInt()
        permissionsInternal = parcel.readInt()
        descriptorsInternal = parcel.createTypedArrayList(BleGattDescriptor.CREATOR)
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(uuidInternal, flags)
        parcel.writeInt(propertyInternal)
        parcel.writeInt(permissionsInternal)
        parcel.writeTypedList(descriptorsInternal)
    }

    fun getUuid(): UUID = uuidInternal.uuid

    fun setUuid(value: ParcelUuid) {
        uuidInternal = value
    }

    fun getProperty(): Int = propertyInternal

    fun setProperty(value: Int) {
        propertyInternal = value
    }

    fun getPermissions(): Int = permissionsInternal

    fun setPermissions(value: Int) {
        permissionsInternal = value
    }

    fun getDescriptors(): MutableList<BleGattDescriptor> {
        if (descriptorsInternal == null) {
            descriptorsInternal = ArrayList()
        }
        return descriptorsInternal!!
    }

    fun setDescriptors(value: MutableList<BleGattDescriptor>?) {
        descriptorsInternal = value
    }

    override fun toString(): String {
        return "BleGattCharacter{uuid=$uuidInternal, property=$propertyInternal, permissions=$permissionsInternal, descriptors=$descriptorsInternal}"
    }

    companion object CREATOR : Parcelable.Creator<BleGattCharacter> {
        override fun createFromParcel(parcel: Parcel): BleGattCharacter = BleGattCharacter(parcel)

        override fun newArray(size: Int): Array<BleGattCharacter?> = arrayOfNulls(size)
    }
}
