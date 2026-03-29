package com.inuker.bluetooth.library.model;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class BleGattCharacter implements Parcelable {
    public static final Parcelable.Creator<BleGattCharacter> CREATOR = new Parcelable.Creator<BleGattCharacter>() { // from class: com.inuker.bluetooth.library.model.BleGattCharacter.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BleGattCharacter createFromParcel(Parcel parcel) {
            return new BleGattCharacter(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BleGattCharacter[] newArray(int i) {
            return new BleGattCharacter[i];
        }
    };
    private List<BleGattDescriptor> descriptors;
    private int permissions;
    private int property;
    private ParcelUuid uuid;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    protected BleGattCharacter(Parcel parcel) {
        this.uuid = (ParcelUuid) parcel.readParcelable(ParcelUuid.class.getClassLoader());
        this.property = parcel.readInt();
        this.permissions = parcel.readInt();
        this.descriptors = parcel.createTypedArrayList(BleGattDescriptor.CREATOR);
    }

    public BleGattCharacter(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        this.uuid = new ParcelUuid(bluetoothGattCharacteristic.getUuid());
        this.property = bluetoothGattCharacteristic.getProperties();
        this.permissions = bluetoothGattCharacteristic.getPermissions();
        Iterator<BluetoothGattDescriptor> it = bluetoothGattCharacteristic.getDescriptors().iterator();
        while (it.hasNext()) {
            getDescriptors().add(new BleGattDescriptor(it.next()));
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.uuid, i);
        parcel.writeInt(this.property);
        parcel.writeInt(this.permissions);
        parcel.writeTypedList(this.descriptors);
    }

    public UUID getUuid() {
        return this.uuid.getUuid();
    }

    public void setUuid(ParcelUuid parcelUuid) {
        this.uuid = parcelUuid;
    }

    public int getProperty() {
        return this.property;
    }

    public void setProperty(int i) {
        this.property = i;
    }

    public int getPermissions() {
        return this.permissions;
    }

    public void setPermissions(int i) {
        this.permissions = i;
    }

    public List<BleGattDescriptor> getDescriptors() {
        if (this.descriptors == null) {
            this.descriptors = new ArrayList();
        }
        return this.descriptors;
    }

    public void setDescriptors(List<BleGattDescriptor> list) {
        this.descriptors = list;
    }

    public String toString() {
        return "BleGattCharacter{uuid=" + this.uuid + ", property=" + this.property + ", permissions=" + this.permissions + ", descriptors=" + this.descriptors + '}';
    }
}
