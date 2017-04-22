package com.example.gitnb.api;

import com.example.gitnb.app.Application;
import com.example.gitnb.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

public class GankRetrofit {
    public static final String HOST = "http://gank.io/api";
    private static GankRetrofit apiRetrofit;
    private Retrofit retrofit;

    public static Retrofit getRetrofit(){
        if(apiRetrofit == null){
            synchronized(GankRetrofit.class){
                if(apiRetrofit == null){
                    apiRetrofit = new GankRetrofit();
                }
            }
        }
        return apiRetrofit.retrofit;
    }

    private GankRetrofit(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();

        OkHttpClient okHttpClient = new TrustOkHttpClient();
        okHttpClient.interceptors().add(getInterceptor());
        okHttpClient.networkInterceptors().add(getNetWorkInterceptor());
        okHttpClient.setCache(getCache());

        retrofit = new Retrofit.Builder()
                .baseUrl(HOST)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    private Cache getCache() {
        int cacheSize = 10 * 1024 * 1024;
        File cacheDir = Application.mContext.getCacheDir();
        return new Cache(cacheDir,cacheSize);
    }

    private Interceptor getInterceptor(){
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!Utils.isNetworkAvailable(Application.mContext)) {
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                }
                return chain.proceed(request);
            }
        };
    }

    private Interceptor getNetWorkInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                if (Utils.isNetworkAvailable(Application.mContext))
                {
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxAge(1, TimeUnit.HOURS)
                            .build();
                    // 有网络时 设置缓存超时时间1个小时
                    response.newBuilder()
                            .header("Cache-Control", cacheControl.toString())
                            .removeHeader("Pragma")
                            .build();
                }
                else {
                    // 无网络时，设置超时为1周
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxAge(3, TimeUnit.DAYS)
                            .onlyIfCached()
                            .build();
                    response.newBuilder()
                            .header("Cache-Control", cacheControl.toString())
                            .removeHeader("Pragma")
                            .build();
                }
                return response;
            }
        };
    }
}
