package com.example.gitnb.app;

import src.com.example.gitnb.api.GitHub;
import com.facebook.drawee.backends.pipeline.Fresco;

import android.content.Context;

import src.com.example.gitnb.api.FakeX509TrustManager;

public class Application extends android.app.Application {

    private static Application mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        FakeX509TrustManager.allowAllSSL();
        Fresco.initialize(mContext);
        GitHub.initialize(mContext);
    }
    
    public static Context getContext() {
        return mContext;
    }
}