package com.example.gitnb.model;

import java.util.List;

public class Commit extends ShaUrl {

    private static final int MAX_COMMIT_LENGHT = 80;

    public GitCommit commit;
    public User author;
    public List<ShaUrl> parents;
    public GitChangeStatus stats;
    public User committer;
    public String message;
    public boolean distinct;
    public GitCommitFiles files;
    public int days;
    public int comment_count;

    @Override
    public String toString() {
        return "[" + sha + "] " + commit.message;
    }

    public String shortMessage() {
        if (message != null) {
            int start = 0;
            int end = Math.min(MAX_COMMIT_LENGHT, message.length());

            return message.substring(start, end);
        }
        return null;
    }
}