package com.inuker.bluetooth.library.connect.options;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class BleConnectOptions implements Parcelable {
    public static final Parcelable.Creator<BleConnectOptions> CREATOR = new Parcelable.Creator<BleConnectOptions>() { // from class: com.inuker.bluetooth.library.connect.options.BleConnectOptions.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BleConnectOptions createFromParcel(Parcel parcel) {
            return new BleConnectOptions(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BleConnectOptions[] newArray(int i) {
            return new BleConnectOptions[i];
        }
    };
    private int connectRetry;
    private int connectTimeout;
    private int serviceDiscoverRetry;
    private int serviceDiscoverTimeout;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static class Builder {
        private static final int DEFAULT_CONNECT_RETRY = 0;
        private static final int DEFAULT_CONNECT_TIMEOUT = 30000;
        private static final int DEFAULT_SERVICE_DISCOVER_RETRY = 0;
        private static final int DEFAULT_SERVICE_DISCOVER_TIMEOUT = 30000;
        private int connectRetry = 0;
        private int serviceDiscoverRetry = 0;
        private int connectTimeout = 30000;
        private int serviceDiscoverTimeout = 30000;

        public Builder setConnectRetry(int i) {
            this.connectRetry = i;
            return this;
        }

        public Builder setServiceDiscoverRetry(int i) {
            this.serviceDiscoverRetry = i;
            return this;
        }

        public Builder setConnectTimeout(int i) {
            this.connectTimeout = i;
            return this;
        }

        public Builder setServiceDiscoverTimeout(int i) {
            this.serviceDiscoverTimeout = i;
            return this;
        }

        public BleConnectOptions build() {
            return new BleConnectOptions(this);
        }
    }

    public BleConnectOptions(Builder builder) {
        this.connectRetry = builder.connectRetry;
        this.serviceDiscoverRetry = builder.serviceDiscoverRetry;
        this.connectTimeout = builder.connectTimeout;
        this.serviceDiscoverTimeout = builder.serviceDiscoverTimeout;
    }

    protected BleConnectOptions(Parcel parcel) {
        this.connectRetry = parcel.readInt();
        this.serviceDiscoverRetry = parcel.readInt();
        this.connectTimeout = parcel.readInt();
        this.serviceDiscoverTimeout = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.connectRetry);
        parcel.writeInt(this.serviceDiscoverRetry);
        parcel.writeInt(this.connectTimeout);
        parcel.writeInt(this.serviceDiscoverTimeout);
    }

    public int getConnectRetry() {
        return this.connectRetry;
    }

    public void setConnectRetry(int i) {
        this.connectRetry = i;
    }

    public int getServiceDiscoverRetry() {
        return this.serviceDiscoverRetry;
    }

    public void setServiceDiscoverRetry(int i) {
        this.serviceDiscoverRetry = i;
    }

    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    public void setConnectTimeout(int i) {
        this.connectTimeout = i;
    }

    public int getServiceDiscoverTimeout() {
        return this.serviceDiscoverTimeout;
    }

    public void setServiceDiscoverTimeout(int i) {
        this.serviceDiscoverTimeout = i;
    }

    public String toString() {
        return "BleConnectOptions{connectRetry=" + this.connectRetry + ", serviceDiscoverRetry=" + this.serviceDiscoverRetry + ", connectTimeout=" + this.connectTimeout + ", serviceDiscoverTimeout=" + this.serviceDiscoverTimeout + '}';
    }
}
