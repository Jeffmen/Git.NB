package com.example.gitnb.api;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class ApiRetrofit implements Interceptor{

    private static ApiRetrofit apiRetrofit;
    private Retrofit retrofit;

    public static Retrofit getRetrofit(){
        if(apiRetrofit == null){
            synchronized(ApiRetrofit.class){
                if(apiRetrofit == null){
                    apiRetrofit = new ApiRetrofit();
                }
            }
        }
        return apiRetrofit.retrofit;
    }

    private ApiRetrofit(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(new TrustOkHttpClient(this)).build();
    }

    public static String getBaseUrl() {
        return GitHub.API_URL;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request().newBuilder()
                .header("Accept", "application/vnd.github.v3+json")
                .addHeader("User-Agent", "Git.NB")
                .addHeader("Authorization", "token " + GitHub.getInstance().getToken())
                //.addHeader("Last-Modified", getLastModified())
                //.addHeader("X-Poll-Interval", "60")
                .build();
        Response response = chain.proceed(request);
        return response;
    }

    private String getLastModified(){
        Calendar cd = Calendar.getInstance();
        cd.add(Calendar.DAY_OF_YEAR, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT")); // 设置时区为GMT
        return sdf.format(cd.getTime());
    }
}
