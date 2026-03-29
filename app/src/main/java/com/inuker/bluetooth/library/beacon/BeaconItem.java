package com.inuker.bluetooth.library.beacon;

import com.inuker.bluetooth.library.utils.ByteUtils;

/* JADX INFO: loaded from: classes.dex */
public class BeaconItem {
    public byte[] bytes;
    public int len;
    public int type;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("@Len = %02X, @Type = 0x%02X", Integer.valueOf(this.len), Integer.valueOf(this.type)));
        int i = this.type;
        String str = (i == 8 || i == 9) ? "%c" : "%02X ";
        sb.append(" -> ");
        StringBuilder sb2 = new StringBuilder();
        try {
            for (byte b : this.bytes) {
                sb2.append(String.format(str, Integer.valueOf(b & 255)));
            }
            sb.append(sb2.toString());
        } catch (Exception unused) {
            sb.append(ByteUtils.byteToString(this.bytes));
        }
        return sb.toString();
    }
}
