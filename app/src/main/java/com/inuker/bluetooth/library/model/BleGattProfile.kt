package com.inuker.bluetooth.library.model

import android.bluetooth.BluetoothGattCharacteristic
import android.os.Parcel
import android.os.Parcelable
import com.inuker.bluetooth.library.utils.ListUtils
import java.util.Collections
import java.util.UUID

class BleGattProfile : Parcelable {
    private var servicesInternal: MutableList<BleGattService>? = null

    val services: MutableList<BleGattService>
        get() = ensureServices()

    constructor(services: Map<UUID, Map<UUID, BluetoothGattCharacteristic>>) {
        val result = ArrayList<BleGattService>()
        for ((uuid, characters) in services) {
            val service = BleGattService(uuid, characters)
            if (!result.contains(service)) {
                result.add(service)
            }
        }
        addServices(result)
    }

    private constructor(parcel: Parcel) {
        parcel.readTypedList(services, BleGattService.CREATOR)
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(services)
    }

    fun addServices(services: List<BleGattService>) {
        val copy = ArrayList(services)
        Collections.sort(copy)
        this.services.addAll(copy)
    }

    fun getService(uuid: UUID?): BleGattService? {
        if (uuid == null) {
            return null
        }
        return services.firstOrNull { it.getUUID() == uuid }
    }

    fun containsCharacter(serviceUuid: UUID?, characteristicUuid: UUID?): Boolean {
        val service = getService(serviceUuid) ?: return false
        if (characteristicUuid == null) {
            return false
        }
        val characters = service.getCharacters()
        if (ListUtils.isEmpty(characters)) {
            return false
        }
        return characters.any { it.getUuid() == characteristicUuid }
    }

    override fun toString(): String = services.joinToString("\n")

    private fun ensureServices(): MutableList<BleGattService> {
        if (servicesInternal == null) {
            servicesInternal = ArrayList()
        }
        return servicesInternal!!
    }

    companion object CREATOR : Parcelable.Creator<BleGattProfile> {
        override fun createFromParcel(parcel: Parcel): BleGattProfile = BleGattProfile(parcel)

        override fun newArray(size: Int): Array<BleGattProfile?> = arrayOfNulls(size)
    }
}
