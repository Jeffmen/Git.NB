package com.example.gitnb.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by a557114 on 29/07/2015.
 */
public class ReleaseAsset implements Parcelable {
    public String url;
    public String browser_download_url;
    public int id;
    public String name;
    public String labnel;
    public String state;
    public String content_type;
    public long size = 0;
    private int download_count;
    private String created_at;
    private String updated_at;
    private User uploader;

    public ReleaseAsset() {

    }

    protected ReleaseAsset(Parcel in) {
        url = in.readString();
        browser_download_url = in.readString();
        id = in.readInt();
        name = in.readString();
        labnel = in.readString();
        state = in.readString();
        content_type = in.readString();
        size = in.readLong();
        download_count = in.readInt();
        created_at = in.readString();
        updated_at = in.readString();
        uploader = in.readParcelable(User.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(browser_download_url);
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(labnel);
        dest.writeString(state);
        dest.writeString(content_type);
        dest.writeLong(size);
        dest.writeInt(download_count);
        dest.writeString(created_at);
        dest.writeString(updated_at);
        dest.writeParcelable(uploader, flags);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ReleaseAsset> CREATOR = new Parcelable.Creator<ReleaseAsset>() {
        @Override
        public ReleaseAsset createFromParcel(Parcel in) {
            return new ReleaseAsset(in);
        }

        @Override
        public ReleaseAsset[] newArray(int size) {
            return new ReleaseAsset[size];
        }
    };
}
