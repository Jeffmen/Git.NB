package com.example.gitnb.model;

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;

public class ShowCase implements Parcelable{
    @SerializedName("name")
	public String name;
    @SerializedName("slug")
	public String slug;
    @SerializedName("description")
	public String description;
    @SerializedName("image_url")
	public String image_url;
	
	public static final Creator<ShowCase> CREATOR = new Creator<ShowCase>() {
        @Override
        public ShowCase createFromParcel(Parcel in) {
            return new ShowCase(in);
        }

        @Override
        public ShowCase[] newArray(int size) {
            return new ShowCase[size];
        }
    };
    
    public ShowCase() {}
    
    protected ShowCase(Parcel in) {
    	name = in.readString();
    	slug = in.readString();
    	description = in.readString();
    	image_url = in.readString();
    }
    
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(slug);
        dest.writeString(description);
        dest.writeString(image_url);
		
	}
}
