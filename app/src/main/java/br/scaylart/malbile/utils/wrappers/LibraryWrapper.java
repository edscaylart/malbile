package br.scaylart.malbile.utils.wrappers;

import android.os.Parcel;
import android.os.Parcelable;

import br.scaylart.malbile.controllers.MalbileManager.ListStatus;
import br.scaylart.malbile.controllers.MalbileManager.TaskJob;
import br.scaylart.malbile.controllers.networks.BaseService.ListType;

public class LibraryWrapper implements Parcelable {
    public static final String TAG = LibraryWrapper.class.getSimpleName();

    public static final String PARCELABLE_KEY = TAG + ":" + "ParcelableKey";
    public static final Parcelable.Creator<LibraryWrapper> CREATOR = new Parcelable.Creator<LibraryWrapper>() {
        @Override
        public LibraryWrapper createFromParcel(Parcel inputParcel) {
            return new LibraryWrapper(inputParcel);
        }

        @Override
        public LibraryWrapper[] newArray(int size) {
            return new LibraryWrapper[size];
        }
    };

    private String mUsername;
    private ListType mListType;
    private ListStatus mListStatus;
    private TaskJob mTaskJob;

    public LibraryWrapper(ListType listType, ListStatus listStatus, TaskJob taskJob, String username) {
        mUsername = username;
        mListStatus = listStatus;
        mListType = listType;
        mTaskJob = taskJob;
    }

    public LibraryWrapper(Parcel in) {
        mTaskJob = TaskJob.valueOf(in.readString());
        mListStatus = ListStatus.valueOf(in.readString());
        mListType = ListType.valueOf(in.readString());
        mUsername = in.readString();
    }

    public ListType getListType() { return mListType; }

    public ListStatus getListStatus() { return mListStatus; }

    public TaskJob getTaskJob() { return mTaskJob; }

    public String getUsername() { return mUsername; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel outputParcel, int flags) {
        outputParcel.writeString(mTaskJob.name());
        outputParcel.writeString(mListStatus.name());
        outputParcel.writeString(mListType.name());
        outputParcel.writeString(mUsername);
    }
}
