package com.example.gitnb.api;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class OauthUrlRetrofit implements Interceptor{

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
        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(new TrustOkHttpClient(this)).build();
    }

	public static String getBaseUrl() {
		return GitHub.API_OAUTH_URL;
	}

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request().newBuilder()
                .header("Accept", "application/json")
                .build();
        return chain.proceed(request);
    }
}
