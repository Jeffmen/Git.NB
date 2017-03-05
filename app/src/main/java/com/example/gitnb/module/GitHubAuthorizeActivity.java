package com.example.gitnb.module;

import com.example.gitnb.R;
import com.example.gitnb.api.GitHub;
import com.example.gitnb.api.rxjava.ApiRxJavaClient;
import com.example.gitnb.api.rxjava.LoginRxJavaClient;
import com.example.gitnb.app.BaseSwipeActivity;
import com.example.gitnb.model.Token;
import com.example.gitnb.model.User;
import com.example.gitnb.module.custom.processor.BlurPostprocessor;
import com.example.gitnb.utils.CurrentUser;
import com.example.gitnb.utils.MessageUtils;
import com.example.gitnb.module.custom.view.ProgressWebView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class GitHubAuthorizeActivity extends BaseSwipeActivity {
	private ProgressWebView web_content;
	private SimpleDraweeView loading_gif;
	private FrameLayout loading_bg;
    
    protected void setTitle(TextView view) {
        view.setText("Authorize");
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_github_authorize);
        web_content = (ProgressWebView) findViewById(R.id.web_content);
        loading_gif = (SimpleDraweeView) findViewById(R.id.loading_gif);
        loading_bg = (FrameLayout) findViewById(R.id.loading_bg);

        SimpleDraweeView titleImage = (SimpleDraweeView)findViewById(R.id.user_background);
        Uri uri = Uri.parse("res://" + getPackageName() + "/" + R.drawable.title_bg_autumn);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setPostprocessor(new BlurPostprocessor(GitHubAuthorizeActivity.this))
                .build();
        PipelineDraweeController controller = (PipelineDraweeController)
                Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setOldController(titleImage.getController())
                        .build();
        titleImage.setController(controller);

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
                if (url.contains(GitHub.REDIRECT_URI)) {
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
		final LoginRxJavaClient client = LoginRxJavaClient.getNewInstance();
		client.requestToken()
                .subscribeOn(Schedulers.io())
				.flatMap(new Func1<Token, Observable<User>>() {
                    @Override
                    public Observable<User> call(Token token) {
                        GitHub.getInstance().setToken(token.access_token);
                        return ApiRxJavaClient.getNewInstance().getService().me();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        MessageUtils.showErrorMessage(GitHubAuthorizeActivity.this, e.getMessage());
                        setResult(Activity.RESULT_CANCELED, null);
                        finish();
                    }

                    @Override
                    public void onNext(User me) {
                        CurrentUser.getInstance().save(me);
                        SharedPreferences read = getSharedPreferences(GitHub.NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = read.edit();
                        editor.putBoolean("first_time", false);
                        editor.commit();
                        jumpToManiActivity();
                    }
                });
    }

    private void jumpToManiActivity() {
        Intent intent = new Intent(GitHubAuthorizeActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }
}
