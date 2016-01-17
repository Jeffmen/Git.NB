package com.example.gitnb.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Bernat on 30/09/2015.
 */
public class Payload {
    public String action;
    public Repository repository;
    public User sender;
    public int number;
    public PullRequest pull_request;

    @SerializedName("public")
    public boolean is_public;
    public Organization org;
    public String created_at;
    public Issue issue;
    public CommitComment comment;
    public Release release;
    public Team team;
    public long push_id;
    public int size;
    public int distinct_size;
    public String ref;
    public String head;
    public String before;
    public List<Commit> commits;
    public Repository forkee;
    public User member;
}
