package com.inuker.bluetooth.library.beacon;

import com.inuker.bluetooth.library.utils.ByteUtils;
import java.util.LinkedList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class Beacon {
    public byte[] mBytes;
    public List<BeaconItem> mItems = new LinkedList();

    public Beacon(byte[] bArr) {
        if (ByteUtils.isEmpty(bArr)) {
            return;
        }
        byte[] bArrTrimLast = ByteUtils.trimLast(bArr);
        this.mBytes = bArrTrimLast;
        this.mItems.addAll(BeaconParser.parseBeacon(bArrTrimLast));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("preParse: %s\npostParse:\n", ByteUtils.byteToString(this.mBytes)));
        for (int i = 0; i < this.mItems.size(); i++) {
            sb.append(this.mItems.get(i).toString());
            if (i != this.mItems.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
