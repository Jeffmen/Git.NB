package com.example.gitnb.api.client;

import java.io.IOException;

import com.example.gitnb.api.GitHub;
import com.example.gitnb.api.service.LoginService;
import com.example.gitnb.api.OauthUrlRetrofit;
import com.example.gitnb.api.RetrofitNetworkAbs;
import com.example.gitnb.model.RequestTokenDTO;
import com.example.gitnb.model.Token;

import retrofit.Call;
import retrofit.Response;

public class LoginClient extends RetrofitNetworkAbs {
	private LoginService loginService;
	
	private LoginClient() {
		loginService = OauthUrlRetrofit.getRetrofit().create(LoginService.class);
	}
	
    public static LoginClient getNewInstance() {
        return new LoginClient();
    }
    
	public void requestTokenAsync(){
        execute(loginService.requestToken(getTokenDTO()));
	}
	
	public Response<Token> requestTokenSync() throws IOException{
        Call<Token> call = loginService.requestToken(getTokenDTO());
        return call.execute();
	}

	public RequestTokenDTO getTokenDTO(){
        RequestTokenDTO tokenDTO = new RequestTokenDTO();
        tokenDTO.client_id = GitHub.CLIENT_ID;
        tokenDTO.client_secret = GitHub.CLIENT_SECRET;
        tokenDTO.redirect_uri = GitHub.REDIRECT_URI;
        tokenDTO.code = GitHub.getInstance().getCode();
        return tokenDTO;
	}

	@Override
	public LoginClient setNetworkListener(NetworkListener networkListener) {
        return setNetworkListener(networkListener, this);
	}
}
