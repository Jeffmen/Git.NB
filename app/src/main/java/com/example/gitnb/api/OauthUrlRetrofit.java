package com.example.gitnb.api;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class OauthUrlRetrofit{

    private static OauthUrlRetrofit apiRetrofit;
    private Retrofit retrofit;

    public static Retrofit getRetrofit(){
        if(apiRetrofit == null){
            synchronized(OauthUrlRetrofit.class){
                if(apiRetrofit == null){
                    apiRetrofit = new OauthUrlRetrofit();
                }
            }
        }
        return apiRetrofit.retrofit;
    }

    private OauthUrlRetrofit(){
        OkHttpClient okHttpClient = new TrustOkHttpClient();
        okHttpClient.interceptors().add(getInterceptor());

        retrofit = new Retrofit.Builder()
                .baseUrl(GitHub.API_OAUTH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient).build();
    }

    private Interceptor getInterceptor(){
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .header("Accept", "application/json")
                        .build();
                return chain.proceed(request);
            }
        };
    }
}
