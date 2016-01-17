package com.example.gitnb.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bernat on 31/08/2014.
 */
public class Event {
	public long id;
	public EventType type = EventType.Unhandled;
	public String name;
	public User actor;
	public User org;
	public Repository repo;
	public Payload payload;

	@SerializedName("public")
	public boolean public_event;

	public String created_at;

	public EventType getType() {
		return type != null ? type : EventType.Unhandled;
	}
}
