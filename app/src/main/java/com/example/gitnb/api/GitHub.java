package com.example.gitnb.api;

import android.content.Context;
import android.content.SharedPreferences;

public class GitHub{
    public static String NAME = "Github";
	public static String CLIENT_ID = "a4220ecd856ed8c01689";
	public static String CLIENT_SECRET = "cdf66e915c257e7e657be966c823b7b64151cf15";
	public static String REDIRECT_URI = "https://github.com/Jeffmen/GitNB";
	public static String API_AUTHORIZE_URL = "https://github.com/login/oauth/authorize/";
    public static String API_OAUTH_URL = "https://github.com/";
    public static String API_URL = "https://api.github.com/";
    public static String TOKEN_KEY = "token";
    public static String CODE_KEY = "code";
    public static String STATE = "2015";
    public static String SCOPE = "user,repo,gist,delete_repo,notifications";
    private static GitHub me;
    private Context context;
    private String token;
    private String code;

    private GitHub(Context context) { 
    	this.context = context;
        SharedPreferences read = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        token = read.getString(TOKEN_KEY, "1f5d67aed88429ae5d655eabba8193fc260c5477");
        code = read.getString(CODE_KEY, "b5a76a4a14ae09c059fa");
    }
    
	public static GitHub getInstance(){
		return me;
	}
	 
	public static void initialize(Context context) {
		me = new GitHub(context);
	}

    public String getCode() {
		return code;
    }
    
    public void setCode(String value){
    	code = value;
        SharedPreferences.Editor editor = context.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit();
        editor.putString(CODE_KEY, value);
        editor.commit();
    }
    
    public String getToken() {
		return token;
    }
    
    public void setToken(String value){
    	token = value;
        SharedPreferences.Editor editor = context.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit();
        editor.putString(TOKEN_KEY, value);
        editor.commit();
    }
}
