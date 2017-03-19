package com.example.gitnb.api.rxjava;

import com.example.gitnb.api.GitHub;
import com.example.gitnb.api.OauthUrlRetrofit;
import com.example.gitnb.model.RequestTokenDTO;
import com.example.gitnb.model.Token;

import rx.Observable;

public class LoginRxJavaClient {
	private LoginRxJavaService loginService;

	private static class Holder{
		private static LoginRxJavaClient instance = new LoginRxJavaClient();
	}

	private LoginRxJavaClient(){
		loginService = OauthUrlRetrofit.getRetrofit().create(LoginRxJavaService.class);
	}
	
    public static LoginRxJavaClient getNewInstance() {
        return Holder.instance;
    }

	public LoginRxJavaService getService() {
		return loginService;
	}

    public Observable<Token> requestToken(){
		return loginService.requestToken(getTokenDTO());
	}

	private  RequestTokenDTO getTokenDTO(){
		RequestTokenDTO tokenDTO = new RequestTokenDTO();
		tokenDTO.client_id = GitHub.CLIENT_ID;
		tokenDTO.client_secret = GitHub.CLIENT_SECRET;
		tokenDTO.redirect_uri = GitHub.REDIRECT_URI;
		tokenDTO.code = GitHub.getInstance().getCode();
		return tokenDTO;
	}
}
