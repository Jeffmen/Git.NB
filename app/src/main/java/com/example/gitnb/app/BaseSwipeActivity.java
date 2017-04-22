package com.example.gitnb.app;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import com.example.gitnb.R;
import com.example.gitnb.api.rxjava.ApiRxJavaClient;
import com.example.gitnb.api.rxjava.ApiRxJavaService;
import com.example.gitnb.module.custom.processor.BlurPostprocessor;
import com.example.gitnb.utils.MessageUtils;
import com.example.gitnb.utils.Utils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public abstract class BaseSwipeActivity  extends SwipeBackActivity implements BlurPostprocessor.ColorChangeListener {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ApiRxJavaService apiRxJavaService;
    private SimpleDraweeView user_background;
	private Toolbar toolbar;
    public int color = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatus();
    }
    
    @Override
    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);
        apiRxJavaService = ApiRxJavaClient.getNewInstance().getService();
        user_background = (SimpleDraweeView)findViewById(R.id.user_background);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        color = getResources().getColor(R.color.orange_yellow);
        if(toolbar != null) {
            TextView title = (TextView) toolbar.findViewById(R.id.title);
            if(title != null) {
                setTitle(title);
            }
            setSupportActionBar(toolbar);
            //setNavigationOnClickListener must be at the back of setSupportActionBar and the function is valid
//            toolbar.setNavigationIcon(getNavigationIcon());
            ActionBar actionBar =  getSupportActionBar();
            if(actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowTitleEnabled(false);
            }
            toolbar.setNavigationOnClickListener(getNavigationOnClickListener());
        }


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        if(mSwipeRefreshLayout != null){
	        mSwipeRefreshLayout.setColorSchemeResources(
                    android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
	        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    startRefresh();
                }
            });
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    startRefresh();
                }
            });
        }
    }

    protected void setTitle(TextView view){

    }

    protected void setUserBackground(String uri){
        if(!TextUtils.isEmpty(uri) && user_background != null) {
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri))
                    .setPostprocessor(new BlurPostprocessor(this))
                    .build();
            PipelineDraweeController controller = (PipelineDraweeController)
                    Fresco.newDraweeControllerBuilder()
                            .setImageRequest(request)
                            .setOldController(user_background.getController())
                            .build();
            user_background.setController(controller);
        }
    }

    public void toolBarColorChange(int color){
        if (color == 0) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float cl = 0.9f;
            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);
            hsv[2] *= cl;
            int primaryDark = Color.HSVToColor(hsv);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(primaryDark);
        }
    }

    public void setStatus(){
        //getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if(VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    public int getNavigationIcon(){
    	return R.drawable.ic_back_arrow;
    }

    public View.OnClickListener getNavigationOnClickListener(){
    	return new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		};
    }

    public Toolbar getToolbar(){
        return toolbar;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout(){
        return mSwipeRefreshLayout;
    }

    public ApiRxJavaService getApiService(){
        return apiRxJavaService;
    }
    
    protected void startRefresh(){
		Utils.setRefreshing(getSwipeRefreshLayout(), true);
    }
    
    protected void endRefresh(){
		Utils.setRefreshing(getSwipeRefreshLayout(), false);
    }
    
    protected void endError(String errorMessage){
        Utils.setRefreshing(getSwipeRefreshLayout(), false);
        //MessageUtils.showErrorMessage(this, errorMessage);
        MessageUtils.getToast(this, errorMessage).show();
    }
}
