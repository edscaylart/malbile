package br.scaylart.malbile.utils.wrappers;

import android.os.Parcel;
import android.os.Parcelable;

public class ReaderWrapper implements Parcelable {
    public static final String TAG = ReaderWrapper.class.getSimpleName();

    public static final String PARCELABLE_KEY = TAG + ":" + "ParcelableKey";
    public static final Parcelable.Creator<ReaderWrapper> CREATOR = new Parcelable.Creator<ReaderWrapper>() {
        @Override
        public ReaderWrapper createFromParcel(Parcel inputParcel) {
            return new ReaderWrapper(inputParcel);
        }

        @Override
        public ReaderWrapper[] newArray(int size) {
            return new ReaderWrapper[size];
        }
    };

    private String mSource;
    private String mUrl;

    public ReaderWrapper(String source, String url) {
        mSource = source;
        mUrl = url;
    }

    public ReaderWrapper(Parcel in) {
        mSource = in.readString();
        mUrl = in.readString();
    }

    public String getSource() {
        return mSource;
    }

    public String getUrl() {
        return mUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel outputParcel, int flags) {
        outputParcel.writeString(mSource);
        outputParcel.writeString(mUrl);
    }
}
