package com.example.gitnb.model;

import android.os.Parcel;

/**
 * Created by Bernat on 22/08/2014.
 */
public class Label extends ShaUrl{
    public String name;
    public String color;

    protected Label(Parcel in) {
        super(in);
        name = in.readString();
        color = in.readString();
    }

    public static final Creator<Label> CREATOR = new Creator<Label>() {
        @Override
        public Label createFromParcel(Parcel in) {
            return new Label(in);
        }

        @Override
        public Label[] newArray(int size) {
            return new Label[size];
        }
    };

    public Label() {
        super();
    }

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(name);
        dest.writeString(color);
    }
}
