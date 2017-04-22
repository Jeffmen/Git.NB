package com.example.gitnb.api.rxjava;

import com.example.gitnb.model.gank.GankData;

import retrofit.http.GET;
import retrofit.http.Path;

import rx.Observable;

/**
 * Created by Bernat on 08/08/2014.
 */
public interface GankRxJavaService {

    @GET("/day/{year}/{month}/{day}")
    Observable<GankData> getGankData(@Path("year")int year, @Path("month")int month, @Path("day")int day);
}
