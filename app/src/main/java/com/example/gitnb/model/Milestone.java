package com.example.gitnb.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bernat on 22/08/2014.
 */
public class Milestone extends ShaUrl{
    public String title;
    public int number;
    public MilestoneState state;
    public String description;
    public User creator;

    @SerializedName("open_issues")
    public int openIssues;
    @SerializedName("closes_issues")
    public int closedIssues;
    @SerializedName("created_at")
    public String createdAt;
    @SerializedName("updated_at")
    public String updatedAt;
    @SerializedName("due_on")
    public String dueOn;

    public Milestone(){
        super();
    }

    protected Milestone(Parcel in) {
        super(in);
        title = in.readString();
        number = in.readInt();
        try {
            state = MilestoneState.valueOf(in.readString());
        } catch (IllegalArgumentException x) {
            state = null;
        }
        description = in.readString();
        creator = in.readParcelable(User.class.getClassLoader());
        openIssues = in.readInt();
        closedIssues = in.readInt();
        createdAt = in.readString();
        updatedAt = in.readString();
        dueOn = in.readString();
    }

    public static final Creator<Milestone> CREATOR = new Creator<Milestone>() {
        @Override
        public Milestone createFromParcel(Parcel in) {
            return new Milestone(in);
        }

        @Override
        public Milestone[] newArray(int size) {
            return new Milestone[size];
        }
    };

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(title);
        dest.writeInt(number);
        dest.writeString(state != null ? state.toString() : "");
        dest.writeString(description);
        dest.writeParcelable(creator, flags);
        dest.writeInt(openIssues);
        dest.writeInt(closedIssues);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeString(dueOn);
    }
}
