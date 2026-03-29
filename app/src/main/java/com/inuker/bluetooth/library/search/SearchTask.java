package com.inuker.bluetooth.library.search;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class SearchTask implements Parcelable {
    public static final Parcelable.Creator<SearchTask> CREATOR = new Parcelable.Creator<SearchTask>() { // from class: com.inuker.bluetooth.library.search.SearchTask.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SearchTask createFromParcel(Parcel parcel) {
            return new SearchTask(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SearchTask[] newArray(int i) {
            return new SearchTask[i];
        }
    };
    private int searchDuration;
    private int searchType;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.searchType);
        parcel.writeInt(this.searchDuration);
    }

    public SearchTask() {
    }

    public int getSearchType() {
        return this.searchType;
    }

    public void setSearchType(int i) {
        this.searchType = i;
    }

    public int getSearchDuration() {
        return this.searchDuration;
    }

    public void setSearchDuration(int i) {
        this.searchDuration = i;
    }

    protected SearchTask(Parcel parcel) {
        this.searchType = parcel.readInt();
        this.searchDuration = parcel.readInt();
    }
}
