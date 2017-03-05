package com.example.gitnb.utils;

import android.content.Context;

import com.example.gitnb.model.PersistenceHelper;
import com.example.gitnb.model.User;

public class CurrentUser {
	private static String FILE_NAME = "current_name";
	private static CurrentUser user;
	private Context mContext;
	private User me;

	private CurrentUser(Context context){
		this.mContext = context;
		this.me = getMe();
	}

	public static void init(Context context){
		user = new CurrentUser(context);
	}

	public static CurrentUser getInstance(){
		return user;
	}
	
	public void save(User user){
		me = user;
        PersistenceHelper.saveModel(mContext, user, FILE_NAME);
	}
	
	public User getMe(){
		if(me == null) {
			return PersistenceHelper.loadModel(mContext, FILE_NAME);
		}
		else{
			return me;
		}
	}
	
	public boolean delete(){
		me = null;
       return PersistenceHelper.deleteObject(mContext, FILE_NAME);
	}
}
