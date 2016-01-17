package com.example.gitnb.api;

import com.example.gitnb.model.RequestTokenDTO;
import com.example.gitnb.model.Token;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;

public interface LoginService {

    @POST("login/oauth/access_token")
    Call<Token> requestToken(@Body RequestTokenDTO requestTokenDTO);

}
