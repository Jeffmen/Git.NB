package com.example.gitnb.module;

import com.example.gitnb.R;
import com.example.gitnb.api.GitHub;
import com.example.gitnb.api.LoginClient;
import com.example.gitnb.api.RetrofitNetworkAbs;
import com.example.gitnb.api.UsersClient;
import com.example.gitnb.app.BaseSwipeActivity;
import com.example.gitnb.model.Token;
import com.example.gitnb.model.User;
import com.example.gitnb.utils.CurrentUser;
import com.example.gitnb.utils.MessageUtils;
import com.example.gitnb.widget.ProgressWebView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class GitHubAuthorizeActivity extends BaseSwipeActivity {
	private ProgressWebView web_content;
	private SimpleDraweeView loading_gif;
	private FrameLayout loading_bg;
    
    protected void setTitle(TextView view){
        view.setText("Authorize");
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_github_authorize);
        web_content = (ProgressWebView) findViewById(R.id.web_content);
        loading_gif = (SimpleDraweeView) findViewById(R.id.loading_gif);
        loading_bg = (FrameLayout) findViewById(R.id.loading_bg);

        String url = GitHub.API_AUTHORIZE_URL;
        url += "?client_id=" + GitHub.CLIENT_ID;
        url += "&state=" + GitHub.STATE;
        url += "&redirect_uri=" + GitHub.REDIRECT_URI;
        url += "&scope=" + GitHub.SCOPE;
        web_content.getSettings().setJavaScriptEnabled(true);
        web_content.loadUrl(url);
        web_content.setUrlLoadingListener(new ProgressWebView.UrlLoadingListener() {
			
			@Override
			public void loading(String url) {
				if(url.contains(GitHub.REDIRECT_URI)){
					Uri uri = Uri.parse(url);
					String code = uri.getQueryParameter(GitHub.CODE_KEY);
					GitHub.getInstance().setCode(code);
					
					loading_gif.post(new Runnable(){

						@Override
						public void run() {
							loadingInfo();
						}
						
					});
					getToken();
				}
			}
		});
    }

    private void loadingInfo(){
    	loading_bg.setVisibility(View.VISIBLE);
		Uri path = (new Uri.Builder()).scheme("res").path(String.valueOf(R.drawable.github_loading)).build();
		DraweeController  draweeController= Fresco.newDraweeControllerBuilder()
				.setAutoPlayAnimations(true)
                .setUri(path)
                .build();
		loading_gif.setController(draweeController);
    }

    private void getToken(){
		LoginClient.getNewInstance().setNetworkListener(new RetrofitNetworkAbs.NetworkListener<Token>() {

			@Override
			public void onOK(Token token) {
				GitHub.getInstance().setToken(token.access_token);
				getMeInfo();
			}

			@Override
			public void onError(String Message) {
		        MessageUtils.showErrorMessage(GitHubAuthorizeActivity.this, Message);
			    setResult(Activity.RESULT_CANCELED, null);
				finish();
			}
			
    	}).requestTokenAsync();
    }
    
    private void getMeInfo(){
		UsersClient.getNewInstance().setNetworkListener(new RetrofitNetworkAbs.NetworkListener<User>() {

			@Override
			public void onOK(User user) {
				CurrentUser.save(GitHubAuthorizeActivity.this, user);
			    setResult(Activity.RESULT_OK, null);
			    finish();
			}

			@Override
			public void onError(String Message) {
		        MessageUtils.showErrorMessage(GitHubAuthorizeActivity.this, Message);
			    setResult(Activity.RESULT_CANCELED, null);
				finish();
			}
			
    	}).me();
    }
    
    @Override
    protected void startRefresh(){
    }

    @Override
    protected void endRefresh(){
    }

    @Override
    protected void endError(){
    }
    
}
