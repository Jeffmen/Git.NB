package com.example.gitnb.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 20/07/2014.
 */
public class Links implements Parcelable {
    public String html;
    public String self;
    public String git;

    public Links() {}
    
    protected Links(Parcel in) {
    	html = in.readString();
    	self = in.readString();
    	git = in.readString();
    }
    
    public static final Creator<Links> CREATOR = new Creator<Links>() {
        @Override
        public Links createFromParcel(Parcel in) {
            return new Links(in);
        }

        @Override
        public Links[] newArray(int size) {
            return new Links[size];
        }
    };
    
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(html);
        dest.writeString(self);
        dest.writeString(git);
	}
}
