package com.inuker.bluetooth.library.search;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/* JADX INFO: loaded from: classes.dex */
public class SearchResult implements Parcelable {
    public static final Parcelable.Creator<SearchResult> CREATOR = new Parcelable.Creator<SearchResult>() { // from class: com.inuker.bluetooth.library.search.SearchResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SearchResult createFromParcel(Parcel parcel) {
            return new SearchResult(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SearchResult[] newArray(int i) {
            return new SearchResult[i];
        }
    };
    public BluetoothDevice device;
    public int rssi;
    public byte[] scanRecord;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public SearchResult(BluetoothDevice bluetoothDevice) {
        this(bluetoothDevice, 0, null);
    }

    public SearchResult(BluetoothDevice bluetoothDevice, int i, byte[] bArr) {
        this.device = bluetoothDevice;
        this.rssi = i;
        this.scanRecord = bArr;
    }

    public String getName() {
        String name = this.device.getName();
        return TextUtils.isEmpty(name) ? "NULL" : name;
    }

    public String getAddress() {
        BluetoothDevice bluetoothDevice = this.device;
        return bluetoothDevice != null ? bluetoothDevice.getAddress() : "";
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(", mac = " + this.device.getAddress());
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.device, 0);
        parcel.writeInt(this.rssi);
        parcel.writeByteArray(this.scanRecord);
    }

    public SearchResult(Parcel parcel) {
        this.device = (BluetoothDevice) parcel.readParcelable(BluetoothDevice.class.getClassLoader());
        this.rssi = parcel.readInt();
        this.scanRecord = parcel.createByteArray();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return this.device.equals(((SearchResult) obj).device);
    }

    public int hashCode() {
        return this.device.hashCode();
    }
}
