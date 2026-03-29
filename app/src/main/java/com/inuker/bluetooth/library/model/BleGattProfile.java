package com.inuker.bluetooth.library.model;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Parcel;
import android.os.Parcelable;
import com.inuker.bluetooth.library.utils.ListUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class BleGattProfile implements Parcelable {
    public static final Parcelable.Creator<BleGattProfile> CREATOR = new Parcelable.Creator<BleGattProfile>() { // from class: com.inuker.bluetooth.library.model.BleGattProfile.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BleGattProfile createFromParcel(Parcel parcel) {
            return new BleGattProfile(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BleGattProfile[] newArray(int i) {
            return new BleGattProfile[i];
        }
    };
    private List<BleGattService> services;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public BleGattProfile(Map<UUID, Map<UUID, BluetoothGattCharacteristic>> map) {
        ArrayList arrayList = new ArrayList();
        for (Map.Entry<UUID, Map<UUID, BluetoothGattCharacteristic>> entry : map.entrySet()) {
            BleGattService bleGattService = new BleGattService(entry.getKey(), entry.getValue());
            if (!arrayList.contains(bleGattService)) {
                arrayList.add(bleGattService);
            }
        }
        addServices(arrayList);
    }

    public BleGattProfile(Parcel parcel) {
        parcel.readTypedList(getServices(), BleGattService.CREATOR);
    }

    public void addServices(List<BleGattService> list) {
        Collections.sort(list);
        getServices().addAll(list);
    }

    public List<BleGattService> getServices() {
        if (this.services == null) {
            this.services = new ArrayList();
        }
        return this.services;
    }

    public BleGattService getService(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        for (BleGattService bleGattService : getServices()) {
            if (bleGattService.getUUID().equals(uuid)) {
                return bleGattService;
            }
        }
        return null;
    }

    public boolean containsCharacter(UUID uuid, UUID uuid2) {
        BleGattService service;
        if (uuid != null && uuid2 != null && (service = getService(uuid)) != null) {
            List<BleGattCharacter> characters = service.getCharacters();
            if (!ListUtils.isEmpty(characters)) {
                Iterator<BleGattCharacter> it = characters.iterator();
                while (it.hasNext()) {
                    if (uuid2.equals(it.next().getUuid())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(getServices());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<BleGattService> it = this.services.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            sb.append("\n");
        }
        return sb.toString();
    }
}
