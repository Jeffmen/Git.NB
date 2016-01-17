package com.example.gitnb.api;

import java.io.IOException;

import com.example.gitnb.model.RequestTokenDTO;
import com.example.gitnb.model.Token;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class LoginClient extends RetrofitNetworkAbs{
	private LoginService loginService;
	
	private LoginClient() {
		loginService = OauthUrlRetrofit.getRetrofit().create(LoginService.class);
	}
	
    public static LoginClient getNewInstance() {
        return new LoginClient();
    }
    
	public void requestTokenAsync(){
        loginService.requestToken(getTokenDTO()).enqueue(new Callback<Token>() {

            @Override
            public void onFailure(Throwable t) {
                myOnFailure(t);
            }

			@Override
			public void onResponse(retrofit.Response<Token> response, Retrofit retrofit) {
                myOnResponse(response);
			}
        });

	}
	
	public Response<Token> requestTokenSync() throws IOException{
        Call<Token> call = loginService.requestToken(getTokenDTO());
        return call.execute();
	}

	private RequestTokenDTO getTokenDTO(){
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
