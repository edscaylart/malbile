package br.scaylart.malbile.utils.wrappers;

import android.os.Parcel;
import android.os.Parcelable;

import br.scaylart.malbile.controllers.networks.BaseService;

public class RequestWrapper implements Parcelable {
    public static final String TAG = RequestWrapper.class.getSimpleName();

    public static final String PARCELABLE_KEY = TAG + ":" + "ParcelableKey";
    public static final Parcelable.Creator<RequestWrapper> CREATOR = new Parcelable.Creator<RequestWrapper>() {
        @Override
        public RequestWrapper createFromParcel(Parcel inputParcel) {
            return new RequestWrapper(inputParcel);
        }

        @Override
        public RequestWrapper[] newArray(int size) {
            return new RequestWrapper[size];
        }
    };

    private String mUsername;
    private BaseService.ListType mListType;
    private int mId;

    public RequestWrapper(BaseService.ListType listType, int id, String username) {
        mUsername = username;
        mId = id;
        mListType = listType;
    }

    public RequestWrapper(Parcel in) {
        mListType = BaseService.ListType.valueOf(in.readString());
        mUsername = in.readString();
        mId = in.readInt();
    }

    public BaseService.ListType getListType() { return mListType; }

    public String getUsername() { return mUsername; }

    public int getId() { return mId; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel outputParcel, int flags) {
        outputParcel.writeString(mListType.name());
        outputParcel.writeString(mUsername);
        outputParcel.writeInt(mId);
    }
}