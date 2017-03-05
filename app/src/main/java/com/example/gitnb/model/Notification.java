package com.example.gitnb.model;

/**
 * Created by Bernat on 18/02/2015.
 */
public class Notification{
	public long id;
	public Repository repository;
	public NotificationSubject subject;
	public String reason;
	public boolean unread;
	public String updated_at;
	public String last_read_at;
	public String url;
	public Long adapter_repo_parent_id;

}
