package com.example.gitnb.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 20/07/2014.
 */
public enum ContentType  implements Parcelable{
    file,
    dir,
    submodule;
    
    
    public static final Creator<ContentType> CREATOR = new Creator<ContentType>() {  
        @Override  
        public ContentType createFromParcel(final Parcel source) {  
            return ContentType.values()[source.readInt()];  
        }  
  
        @Override  
        public ContentType[] newArray(final int size) {  
            return new ContentType[size];  
        }  
    };
    
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ordinal());  
	}
}
