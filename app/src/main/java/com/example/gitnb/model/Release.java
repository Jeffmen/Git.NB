package com.example.gitnb.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Bernat on 20/07/2014.
 */
public class Release implements Parcelable {
    public String body;
    public String upload_url;
    public String assets_url;
    public String tag_name;
    public String url;
    public String published_at;
    public String html_url;
    public String id;
    public String target_commitish;
    public List<ReleaseAsset> assets;
    public boolean draft;
    public User author;
    public String zipball_url;
    public boolean prerelease;
    public String tarball_url;
    public String name;
    public Date created_at;

    public Release() {

    }

    protected Release(Parcel in) {
        body = in.readString();
        upload_url = in.readString();
        assets_url = in.readString();
        tag_name = in.readString();
        url = in.readString();
        published_at = in.readString();
        html_url = in.readString();
        id = in.readString();
        target_commitish = in.readString();
        if (in.readByte() == 0x01) {
            assets = new ArrayList<ReleaseAsset>();
            in.readList(assets, ReleaseAsset.class.getClassLoader());
        } else {
            assets = null;
        }
        draft = in.readByte() != 0x00;
        author = in.readParcelable(User.class.getClassLoader());
        zipball_url = in.readString();
        prerelease = in.readByte() != 0x00;
        tarball_url = in.readString();
        name = in.readString();
        long tmpCreated_at = in.readLong();
        created_at = tmpCreated_at != -1 ? new Date(tmpCreated_at) : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(body);
        dest.writeString(upload_url);
        dest.writeString(assets_url);
        dest.writeString(tag_name);
        dest.writeString(url);
        dest.writeString(published_at);
        dest.writeString(html_url);
        dest.writeString(id);
        dest.writeString(target_commitish);
        if (assets == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(assets);
        }
        dest.writeByte((byte) (draft ? 0x01 : 0x00));
        dest.writeParcelable(author, flags);
        dest.writeString(zipball_url);
        dest.writeByte((byte) (prerelease ? 0x01 : 0x00));
        dest.writeString(tarball_url);
        dest.writeString(name);
        dest.writeLong(created_at != null ? created_at.getTime() : -1L);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Release> CREATOR = new Parcelable.Creator<Release>() {
        @Override
        public Release createFromParcel(Parcel in) {
            return new Release(in);
        }

        @Override
        public Release[] newArray(int size) {
            return new Release[size];
        }
    };
}
