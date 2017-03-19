package com.example.gitnb.app;

import com.example.gitnb.api.GitHub;
import com.example.gitnb.module.custom.iconify.FontelloMoule;
import com.example.gitnb.utils.CurrentUser;
import com.facebook.drawee.backends.pipeline.Fresco;

import android.content.Context;

import com.example.gitnb.api.FakeX509TrustManager;
import com.joanzapata.iconify.Iconify;
import com.squareup.leakcanary.LeakCanary;

public class Application extends android.app.Application {

    public static Application mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        FakeX509TrustManager.allowAllSSL();
        Fresco.initialize(mContext);
        GitHub.initialize(mContext);
        CurrentUser.init(this);
        Iconify.with(new FontelloMoule());
    }
    
    public static Context getContext() {
        return mContext;
    }
}