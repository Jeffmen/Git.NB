package com.example.gitnb.model;

import android.os.Parcel;

/**
 * Created by Bernat on 30/05/2015.
 */
public class Head extends ShaUrl {

    public String ref;

    public Repository repo;

    public String label;

    public User user;

    public Head(Parcel in) {
        super(in);
        ref = in.readString();
        repo = in.readParcelable(Repository.class.getClassLoader());
        label = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
    }

    public Head() {
        super();
    }

    public static final Creator<Head> CREATOR = new Creator<Head>() {
        @Override
        public Head createFromParcel(Parcel in) {
            return new Head(in);
        }

        @Override
        public Head[] newArray(int size) {
            return new Head[size];
        }
    };

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(ref);
        dest.writeParcelable(repo, flags);
        dest.writeString(label);
        dest.writeParcelable(user, flags);
    }
}
