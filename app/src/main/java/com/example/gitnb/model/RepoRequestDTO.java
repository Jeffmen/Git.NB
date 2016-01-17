package com.example.gitnb.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bernat on 13/10/2014.
 */
public class RepoRequestDTO implements Parcelable {
	public String name;
	public String description;
	public String homepage;

	@SerializedName("private")
	public boolean isPrivate;
	public boolean has_issues;
	public boolean has_wiki;
	public boolean has_downloads;
	public String default_branch;

	public boolean auto_init;
	public String gitignore_template;
	public String license_template;

	public int team_id;

	public RepoRequestDTO() {

	}

	protected RepoRequestDTO(Parcel in) {
		name = in.readString();
		description = in.readString();
		homepage = in.readString();
		isPrivate = in.readByte() != 0x00;
		has_issues = in.readByte() != 0x00;
		has_wiki = in.readByte() != 0x00;
		has_downloads = in.readByte() != 0x00;
		default_branch = in.readString();
		auto_init = in.readByte() != 0x00;
		gitignore_template = in.readString();
		license_template = in.readString();
		team_id = in.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(description);
		dest.writeString(homepage);
		dest.writeByte((byte) (isPrivate ? 0x01 : 0x00));
		dest.writeByte((byte) (has_issues ? 0x01 : 0x00));
		dest.writeByte((byte) (has_wiki ? 0x01 : 0x00));
		dest.writeByte((byte) (has_downloads ? 0x01 : 0x00));
		dest.writeString(default_branch);
		dest.writeByte((byte) (auto_init ? 0x01 : 0x00));
		dest.writeString(gitignore_template);
		dest.writeString(license_template);
		dest.writeInt(team_id);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<RepoRequestDTO> CREATOR = new Parcelable.Creator<RepoRequestDTO>() {
		@Override
		public RepoRequestDTO createFromParcel(Parcel in) {
			return new RepoRequestDTO(in);
		}

		@Override
		public RepoRequestDTO[] newArray(int size) {
			return new RepoRequestDTO[size];
		}
	};

	public boolean isValid() {
		return !isEmpty(name);
	}

	private boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}
}
