package com.example.gitnb.app;

import com.example.gitnb.R;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class BaseNormalActivity  extends AppCompatActivity {
	protected static int START_UPDATE = 100;
	protected static int END_UPDATE = 200;
	protected static int END_ERROR = 300;
	protected View rootView;
	private Toolbar toolbar;

	protected Handler refreshHandler = new Handler(){ 
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == START_UPDATE)
            {
            	startRefresh();
            }
            else if(msg.what == END_UPDATE)
            {
                endRefresh();
            }
            else if(msg.what == END_ERROR)
            {
                endError();
            }
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatus();
    }
    
    @Override
    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);   
        rootView = ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
        	@Override            
        	public void onGlobalLayout() {                
        		rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this); 
                refreshHandler.sendEmptyMessage(START_UPDATE);
        	}        
        });
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null){
	        setTitle((TextView) toolbar.findViewById(R.id.title));         
	        setSupportActionBar(toolbar);
	        //setNavigationOnClickListener must be at the back of setSupportActionBar and the function is valid
	        toolbar.setNavigationIcon(getNavigationIcon());
	        toolbar.setNavigationOnClickListener(getNavigationOnClickListener());
        }
        //refreshHandler.sendEmptyMessageDelayed(START_UPDATE, 300);
    }
    
    protected void setTitle(TextView view){
    	
    }
    
    protected int getNavigationIcon(){
    	return R.drawable.ic_arrow_back_white_48dp;
    }
    
    protected View.OnClickListener getNavigationOnClickListener(){
    	return new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		};
    }
    
    private void setStatus(){
        //getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if(VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }
    
    protected void startRefresh(){
    	
    }
    
    protected void endRefresh(){
    	
    }
    
    protected void endError(){
    	
    }
}
