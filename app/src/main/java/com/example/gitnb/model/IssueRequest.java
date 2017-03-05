package com.example.gitnb.model;


/**
 * Created by Bernat on 23/08/2014.
 */
public class IssueRequest {
    public String title;
    public String body;
    public String assignee;
    public Integer milestone;
    public CharSequence[] labels;
	public IssueState state;
}
