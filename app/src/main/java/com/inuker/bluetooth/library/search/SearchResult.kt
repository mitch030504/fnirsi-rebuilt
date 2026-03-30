package com.inuker.bluetooth.library.search

import android.bluetooth.BluetoothDevice
import android.os.Parcel
import android.os.Parcelable

class SearchResult(
    @JvmField var device: BluetoothDevice,
    @JvmField var rssi: Int = 0,
    @JvmField var scanRecord: ByteArray? = null,
) : Parcelable {
    constructor(device: BluetoothDevice) : this(device, 0, null)

    private constructor(parcel: Parcel) : this(
        requireNotNull(parcel.readParcelable(BluetoothDevice::class.java.classLoader)),
        parcel.readInt(),
        parcel.createByteArray(),
    )

    val name: String
        get() = device.name?.takeUnless { it.isEmpty() } ?: "NULL"

    val address: String
        get() = device.address ?: ""

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(device, 0)
        parcel.writeInt(rssi)
        parcel.writeByteArray(scanRecord)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is SearchResult) {
            return false
        }
        return device == other.device
    }

    override fun hashCode(): Int = device.hashCode()

    override fun toString(): String = ", mac = ${device.address}"

    companion object CREATOR : Parcelable.Creator<SearchResult> {
        override fun createFromParcel(parcel: Parcel): SearchResult = SearchResult(parcel)

        override fun newArray(size: Int): Array<SearchResult?> = arrayOfNulls(size)
    }
}
