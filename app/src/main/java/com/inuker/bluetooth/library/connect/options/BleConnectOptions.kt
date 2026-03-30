package com.inuker.bluetooth.library.connect.options

import android.os.Parcel
import android.os.Parcelable

class BleConnectOptions(builder: Builder) : Parcelable {
    private var connectRetry = builder.connectRetry
    private var serviceDiscoverRetry = builder.serviceDiscoverRetry
    private var connectTimeout = builder.connectTimeout
    private var serviceDiscoverTimeout = builder.serviceDiscoverTimeout

    private constructor(parcel: Parcel) : this(Builder()) {
        connectRetry = parcel.readInt()
        serviceDiscoverRetry = parcel.readInt()
        connectTimeout = parcel.readInt()
        serviceDiscoverTimeout = parcel.readInt()
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(connectRetry)
        parcel.writeInt(serviceDiscoverRetry)
        parcel.writeInt(connectTimeout)
        parcel.writeInt(serviceDiscoverTimeout)
    }

    fun getConnectRetry(): Int = connectRetry

    fun setConnectRetry(value: Int) {
        connectRetry = value
    }

    fun getServiceDiscoverRetry(): Int = serviceDiscoverRetry

    fun setServiceDiscoverRetry(value: Int) {
        serviceDiscoverRetry = value
    }

    fun getConnectTimeout(): Int = connectTimeout

    fun setConnectTimeout(value: Int) {
        connectTimeout = value
    }

    fun getServiceDiscoverTimeout(): Int = serviceDiscoverTimeout

    fun setServiceDiscoverTimeout(value: Int) {
        serviceDiscoverTimeout = value
    }

    override fun toString(): String {
        return "BleConnectOptions(connectRetry=$connectRetry, serviceDiscoverRetry=$serviceDiscoverRetry, " +
            "connectTimeout=$connectTimeout, serviceDiscoverTimeout=$serviceDiscoverTimeout)"
    }

    class Builder {
        internal var connectRetry = 0
        internal var serviceDiscoverRetry = 0
        internal var connectTimeout = 30_000
        internal var serviceDiscoverTimeout = 30_000

        fun setConnectRetry(value: Int) = apply { connectRetry = value }

        fun setServiceDiscoverRetry(value: Int) = apply { serviceDiscoverRetry = value }

        fun setConnectTimeout(value: Int) = apply { connectTimeout = value }

        fun setServiceDiscoverTimeout(value: Int) = apply { serviceDiscoverTimeout = value }

        fun build(): BleConnectOptions = BleConnectOptions(this)
    }

    companion object CREATOR : Parcelable.Creator<BleConnectOptions> {
        override fun createFromParcel(parcel: Parcel): BleConnectOptions = BleConnectOptions(parcel)

        override fun newArray(size: Int): Array<BleConnectOptions?> = arrayOfNulls(size)
    }
}
