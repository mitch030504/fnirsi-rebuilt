package com.inuker.bluetooth.library.model;

import android.bluetooth.BluetoothGattDescriptor;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class BleGattDescriptor implements Parcelable {
    public static final Parcelable.Creator<BleGattDescriptor> CREATOR = new Parcelable.Creator<BleGattDescriptor>() { // from class: com.inuker.bluetooth.library.model.BleGattDescriptor.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BleGattDescriptor createFromParcel(Parcel parcel) {
            return new BleGattDescriptor(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BleGattDescriptor[] newArray(int i) {
            return new BleGattDescriptor[i];
        }
    };
    private int mPermissions;
    private ParcelUuid mUuid;
    private byte[] mValue;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    protected BleGattDescriptor(Parcel parcel) {
        this.mUuid = (ParcelUuid) parcel.readParcelable(ParcelUuid.class.getClassLoader());
        this.mPermissions = parcel.readInt();
        this.mValue = parcel.createByteArray();
    }

    public BleGattDescriptor(BluetoothGattDescriptor bluetoothGattDescriptor) {
        this.mUuid = new ParcelUuid(bluetoothGattDescriptor.getUuid());
        this.mPermissions = bluetoothGattDescriptor.getPermissions();
        this.mValue = bluetoothGattDescriptor.getValue();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.mUuid, i);
        parcel.writeInt(this.mPermissions);
        parcel.writeByteArray(this.mValue);
    }

    public ParcelUuid getmUuid() {
        return this.mUuid;
    }

    public void setmUuid(ParcelUuid parcelUuid) {
        this.mUuid = parcelUuid;
    }

    public int getmPermissions() {
        return this.mPermissions;
    }

    public void setmPermissions(int i) {
        this.mPermissions = i;
    }

    public byte[] getmValue() {
        return this.mValue;
    }

    public void setmValue(byte[] bArr) {
        this.mValue = bArr;
    }

    public String toString() {
        return "BleGattDescriptor{mUuid=" + this.mUuid + ", mPermissions=" + this.mPermissions + ", mValue=" + Arrays.toString(this.mValue) + '}';
    }
}
