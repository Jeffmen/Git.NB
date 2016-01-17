package com.example.gitnb.model;

import java.util.List;

public class GitCommit extends ShaUrl {

    private static final int MAX_COMMIT_LENGHT = 80;

    public User committer;
    public List<ShaUrl> parents;
    public User author;
    public String message;
    public ShaUrl tree;
    public int comment_count;

    public String shortMessage() {
        if (message != null) {
            int start = 0;
            int end = Math.min(MAX_COMMIT_LENGHT, message.length());

            return message.substring(start, end);
        }
        return null;
    }
}
