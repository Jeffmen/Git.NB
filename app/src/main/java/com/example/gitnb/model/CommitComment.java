package com.example.gitnb.model;

/**
 * Created by Bernat on 30/05/2015.
 */
public class CommitComment extends GithubComment {

    public int position;
    public int line;
    public String commit_id;
    public String path;
}
