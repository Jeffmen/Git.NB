package com.example.gitnb.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 20/07/2014.
 */
public class ShaUrl implements Parcelable {
    private static final int MAX_SHA_LENGHT = 8;
    public String sha;
    public String url;
    public String html_url;

    public ShaUrl() {

    }

    protected ShaUrl(Parcel in) {
        sha = in.readString();
        url = in.readString();
        html_url = in.readString();
    }

    public static final Creator<ShaUrl> CREATOR = new Creator<ShaUrl>() {
        @Override
        public ShaUrl createFromParcel(Parcel in) {
            return new ShaUrl(in);
        }

        @Override
        public ShaUrl[] newArray(int size) {
            return new ShaUrl[size];
        }
    };

    public String shortSha() {
        int start = 0;
        int end = Math.min(MAX_SHA_LENGHT, sha.length());

        return sha.substring(start, end);
    }

    public static String shortShaStatic(String sha) {
        int start = 0;
        int end = Math.min(MAX_SHA_LENGHT, sha.length());

        return sha.substring(start, end);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sha);
        dest.writeString(url);
        dest.writeString(html_url);
    }
}
