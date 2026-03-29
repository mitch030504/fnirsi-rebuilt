package com.inuker.bluetooth.library.model;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class BleGattService implements Parcelable, Comparable {
    public static final Parcelable.Creator<BleGattService> CREATOR = new Parcelable.Creator<BleGattService>() { // from class: com.inuker.bluetooth.library.model.BleGattService.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BleGattService createFromParcel(Parcel parcel) {
            return new BleGattService(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BleGattService[] newArray(int i) {
            return new BleGattService[i];
        }
    };
    private List<BleGattCharacter> characters;
    private ParcelUuid uuid;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public BleGattService(UUID uuid, Map<UUID, BluetoothGattCharacteristic> map) {
        this.uuid = new ParcelUuid(uuid);
        Iterator<BluetoothGattCharacteristic> it = map.values().iterator();
        while (it.hasNext()) {
            getCharacters().add(new BleGattCharacter(it.next()));
        }
    }

    protected BleGattService(Parcel parcel) {
        this.uuid = (ParcelUuid) parcel.readParcelable(ParcelUuid.class.getClassLoader());
        this.characters = parcel.createTypedArrayList(BleGattCharacter.CREATOR);
    }

    public UUID getUUID() {
        return this.uuid.getUuid();
    }

    public List<BleGattCharacter> getCharacters() {
        if (this.characters == null) {
            this.characters = new ArrayList();
        }
        return this.characters;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Service: %s\n", this.uuid));
        List<BleGattCharacter> characters = getCharacters();
        int size = characters.size();
        for (int i = 0; i < size; i++) {
            sb.append(String.format(">>> Character: %s", characters.get(i)));
            if (i != size - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    @Override // java.lang.Comparable
    public int compareTo(Object obj) {
        if (obj == null) {
            return 1;
        }
        return getUUID().compareTo(((BleGattService) obj).getUUID());
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.uuid, i);
        parcel.writeTypedList(this.characters);
    }
}
