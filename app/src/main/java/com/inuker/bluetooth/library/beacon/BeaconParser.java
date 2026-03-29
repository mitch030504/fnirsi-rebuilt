package com.inuker.bluetooth.library.beacon;

import com.inuker.bluetooth.library.utils.ByteUtils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class BeaconParser {
    private byte[] bytes;
    private ByteBuffer mByteBuffer;

    public boolean getBit(int i, int i2) {
        return (i & (1 << i2)) != 0;
    }

    public BeaconParser(BeaconItem beaconItem) {
        this(beaconItem.bytes);
    }

    public BeaconParser(byte[] bArr) {
        this.bytes = bArr;
        this.mByteBuffer = ByteBuffer.wrap(bArr).order(ByteOrder.LITTLE_ENDIAN);
    }

    public void setPosition(int i) {
        this.mByteBuffer.position(i);
    }

    public int readByte() {
        return this.mByteBuffer.get() & 255;
    }

    public int readShort() {
        return this.mByteBuffer.getShort() & 65535;
    }

    public static List<BeaconItem> parseBeacon(byte[] bArr) {
        BeaconItem beaconItem;
        ArrayList arrayList = new ArrayList();
        int i = 0;
        while (i < bArr.length && (beaconItem = parse(bArr, i)) != null) {
            arrayList.add(beaconItem);
            i += beaconItem.len + 1;
        }
        return arrayList;
    }

    private static BeaconItem parse(byte[] bArr, int i) {
        int i2;
        if (bArr.length - i >= 2 && (i2 = bArr[i]) > 0) {
            byte b = bArr[i + 1];
            int i3 = i + 2;
            if (i3 < bArr.length) {
                BeaconItem beaconItem = new BeaconItem();
                int length = (i3 + i2) - 2;
                if (length >= bArr.length) {
                    length = bArr.length - 1;
                }
                beaconItem.type = b & 255;
                beaconItem.len = i2;
                beaconItem.bytes = ByteUtils.getBytes(bArr, i3, length);
                return beaconItem;
            }
        }
        return null;
    }
}
