package com.inuker.bluetooth.library.model

import android.bluetooth.BluetoothGattCharacteristic
import android.os.Parcel
import android.os.ParcelUuid
import android.os.Parcelable
import java.util.UUID

class BleGattService : Parcelable, Comparable<BleGattService> {
    private var uuidInternal: ParcelUuid
    private var charactersInternal: MutableList<BleGattCharacter>? = null

    constructor(uuid: UUID, characters: Map<UUID, BluetoothGattCharacteristic>) {
        uuidInternal = ParcelUuid(uuid)
        characters.values.forEach { getCharacters().add(BleGattCharacter(it)) }
    }

    private constructor(parcel: Parcel) {
        uuidInternal = parcel.readParcelable(ParcelUuid::class.java.classLoader) ?: ParcelUuid.fromString("00000000-0000-0000-0000-000000000000")
        charactersInternal = parcel.createTypedArrayList(BleGattCharacter.CREATOR)
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(uuidInternal, flags)
        parcel.writeTypedList(charactersInternal)
    }

    fun getUUID(): UUID = uuidInternal.uuid

    fun getCharacters(): MutableList<BleGattCharacter> {
        if (charactersInternal == null) {
            charactersInternal = ArrayList()
        }
        return charactersInternal!!
    }

    override fun compareTo(other: BleGattService): Int = getUUID().compareTo(other.getUUID())

    override fun toString(): String {
        val body = getCharacters().joinToString("\n") { ">>> Character: $it" }
        return String.format("Service: %s\n%s", uuidInternal, body)
    }

    companion object CREATOR : Parcelable.Creator<BleGattService> {
        override fun createFromParcel(parcel: Parcel): BleGattService = BleGattService(parcel)

        override fun newArray(size: Int): Array<BleGattService?> = arrayOfNulls(size)
    }
}
