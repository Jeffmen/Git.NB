package com.example.gitnb.api.rxjava;

import com.example.gitnb.model.RequestTokenDTO;
import com.example.gitnb.model.Token;

import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

public interface LoginRxJavaService {

    @POST("login/oauth/access_token")
    Observable<Token> requestToken(@Body RequestTokenDTO requestTokenDTO);

}
