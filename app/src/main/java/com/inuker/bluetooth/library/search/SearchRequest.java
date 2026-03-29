package com.inuker.bluetooth.library.search;

import android.os.Parcel;
import android.os.Parcelable;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class SearchRequest implements Parcelable {
    public static final Parcelable.Creator<SearchRequest> CREATOR = new Parcelable.Creator<SearchRequest>() { // from class: com.inuker.bluetooth.library.search.SearchRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SearchRequest createFromParcel(Parcel parcel) {
            return new SearchRequest(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SearchRequest[] newArray(int i) {
            return new SearchRequest[i];
        }
    };
    private List<SearchTask> tasks;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(this.tasks);
    }

    public SearchRequest() {
    }

    protected SearchRequest(Parcel parcel) {
        ArrayList arrayList = new ArrayList();
        this.tasks = arrayList;
        parcel.readTypedList(arrayList, SearchTask.CREATOR);
    }

    public List<SearchTask> getTasks() {
        return this.tasks;
    }

    public void setTasks(List<SearchTask> list) {
        this.tasks = list;
    }

    public static class Builder {
        private List<SearchTask> tasks = new ArrayList();

        public Builder searchBluetoothLeDevice(int i) {
            if (BluetoothUtils.isBleSupported()) {
                SearchTask searchTask = new SearchTask();
                searchTask.setSearchType(2);
                searchTask.setSearchDuration(i);
                this.tasks.add(searchTask);
            }
            return this;
        }

        public Builder searchBluetoothLeDevice(int i, int i2) {
            for (int i3 = 0; i3 < i2; i3++) {
                searchBluetoothLeDevice(i);
            }
            return this;
        }

        public Builder searchBluetoothClassicDevice(int i) {
            SearchTask searchTask = new SearchTask();
            searchTask.setSearchType(1);
            searchTask.setSearchDuration(i);
            this.tasks.add(searchTask);
            return this;
        }

        public Builder searchBluetoothClassicDevice(int i, int i2) {
            for (int i3 = 0; i3 < i2; i3++) {
                searchBluetoothClassicDevice(i);
            }
            return this;
        }

        public SearchRequest build() {
            SearchRequest searchRequest = new SearchRequest();
            searchRequest.setTasks(this.tasks);
            return searchRequest;
        }
    }
}
