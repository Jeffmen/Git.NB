package com.example.gitnb.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Bernat on 04/09/2014.
 */
public class Organization extends ShaUrl {

	public int id;
	public String login;
	public String name;
	public String company;

	public Date created_at;
	public Date updated_at;

	public String avatar_url;
	public String gravatar_id;
	public String blog;
	public String bio;
	public String email;
	public String description;

	public String location;
	public UserType type;

	public boolean site_admin;

	public int public_repos;
	public int public_gists;
	public int followers;
	public int following;

	protected Organization(Parcel in) {
		super(in);
		id = in.readInt();
		login = in.readString();
		name = in.readString();
		company = in.readString();
		long tmpCreated_at = in.readLong();
		created_at = tmpCreated_at != -1 ? new Date(tmpCreated_at) : null;
		long tmpUpdated_at = in.readLong();
		updated_at = tmpUpdated_at != -1 ? new Date(tmpUpdated_at) : null;
		avatar_url = in.readString();
		gravatar_id = in.readString();
		blog = in.readString();
		bio = in.readString();
		email = in.readString();
		location = in.readString();
		description = in.readString();
		try {
			type = UserType.valueOf(in.readString());
		} catch (IllegalArgumentException x) {
			type = null;
		}
		site_admin = in.readByte() != 0x00;
		public_repos = in.readInt();
		public_gists = in.readInt();
		followers = in.readInt();
		following = in.readInt();
	}

	public static final Creator<Organization> CREATOR = new Creator<Organization>() {
		@Override
		public Organization createFromParcel(Parcel in) {
			return new Organization(in);
		}

		@Override
		public Organization[] newArray(int size) {
			return new Organization[size];
		}
	};

	public Organization() {
		super();
	}

	@Override
	public int describeContents() {
		return super.describeContents();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(id);
		dest.writeString(login);
		dest.writeString(name);
		dest.writeString(company);
		dest.writeLong(created_at != null ? created_at.getTime() : -1L);
		dest.writeLong(updated_at != null ? updated_at.getTime() : -1L);
		dest.writeString(avatar_url);
		dest.writeString(gravatar_id);
		dest.writeString(blog);
		dest.writeString(bio);
		dest.writeString(email);
		dest.writeString(location);
		dest.writeString(description);
		dest.writeString(type != null ? type.toString() : "");
		dest.writeByte((byte) (site_admin ? 0x01 : 0x00));
		dest.writeInt(public_repos);
		dest.writeInt(public_gists);
		dest.writeInt(followers);
		dest.writeInt(following);
	}
}
