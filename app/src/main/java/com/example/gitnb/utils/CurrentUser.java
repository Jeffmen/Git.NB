package com.example.gitnb.utils;

import android.content.Context;

import com.example.gitnb.model.PersistenceHelper;
import com.example.gitnb.model.User;

public class CurrentUser {
	private static String FILE_NAME = "current_name";
	
	public static void save(Context context, User user){ 
        PersistenceHelper.saveModel(context, user, FILE_NAME);
	}
	
	public static User get(Context context){
       return PersistenceHelper.loadModel(context, FILE_NAME); 
	}
	
	public static boolean detete(Context context){
       return PersistenceHelper.deleteObject(context, FILE_NAME); 
	}
}
