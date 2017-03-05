package com.example.gitnb.model;

import android.os.Parcel;

/**
 * Created by Bernat on 30/05/2015.
 */
public class PullRequest extends Issue{

    /*public String body;

    public IssueState state;

    public String commits_url;

    //public Links _links;

    public String issue_url;

    public String id;

    public Milestone milestone;

    public String mergeable_state;


    public String title;

    public String comments_url;

    public String created_at;

    public String review_comment_url;

    public int commits;

    public int review_comments;


    public String merged_at;

    public String closed_at;

    public String review_comments_url;

    public User assignee;

    public int number;

    public String url;

    public String html_url;

    public String updated_at;

    public User merged_by;

    public String statuses_url;

    public User user;

    public String merge_commit_sha;

    public int comments;*/

    public Head head;

    public Head base;

    public int additions;

    public int deletions;

    public int commits;

    public int changed_files;

    public boolean merged;

    public boolean mergeable;

    public String patch_url;

    public String diff_url;

    public PullRequest(Parcel in) {
        super(in);
        head = in.readParcelable(Head.class.getClassLoader());
        base = in.readParcelable(Head.class.getClassLoader());
        additions = in.readInt();
        deletions = in.readInt();
        commits = in.readInt();
        changed_files = in.readInt();
        merged = in.readByte() != 0x00;
        mergeable = in.readByte() != 0x00;
        patch_url = in.readString();
        diff_url = in.readString();
    }

    public PullRequest() {
        super();
    }

    public static final Creator<PullRequest> CREATOR = new Creator<PullRequest>() {
        @Override
        public PullRequest createFromParcel(Parcel in) {
            return new PullRequest(in);
        }

        @Override
        public PullRequest[] newArray(int size) {
            return new PullRequest[size];
        }
    };

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(head, flags);
        dest.writeParcelable(base, flags);
        dest.writeInt(additions);
        dest.writeInt(deletions);
        dest.writeInt(commits);
        dest.writeInt(changed_files);
        dest.writeByte((byte) (merged ? 0x01 : 0x00));
        dest.writeByte((byte) (mergeable ? 0x01 : 0x00));
        dest.writeString(patch_url);
        dest.writeString(diff_url);
    }
}
