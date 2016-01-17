package com.example.gitnb.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;

import java.lang.reflect.Field;


public class StatusActivity extends AppCompatActivity {
	private LinearLayout mStatusBackground;
	//private LinearLayout mSplitBackground;
	private boolean fullScreenFlag = false;
	private boolean statusFlag = true;
	private final int STATUS_HEIGHT = 75;
	private int statusHeight = STATUS_HEIGHT;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().hide();
		if(!fullScreenFlag && statusFlag){
			mStatusBackground = new LinearLayout(this);
			//mSplitBackground = new LinearLayout(this);
			statusHeight = getStatusBarHeight();
			initSystemBar();
		}
	}
	
	private void initSystemBar() {
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	    	Window window = getWindow();
	    	window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	    	window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	        //setTranslucentStatus(true);
//	        SystemBarTintManager tintManager = new SystemBarTintManager(this);
//	        tintManager.setStatusBarTintEnabled(true);
//	        tintManager.setStatusBarTintResource(R.color.actionbar_bg);
	    }
	}
	
	public void setContentView(int layoutResID) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(layoutResID, null);
		setContentView(view, null);
	}

	public void addContentView(View view, ViewGroup.LayoutParams params) {
		super.addContentView(view, params);
	}
	
	public void setContentView(View view, ViewGroup.LayoutParams params) {
		View tempView;
		if (statusFlag) {
			if (fullScreenFlag) {
//				FrameLayout layout = new FrameLayout(this);
//				layout.addView(view, new FrameLayout.LayoutParams(
//						FrameLayout.LayoutParams.MATCH_PARENT,
//						FrameLayout.LayoutParams.WRAP_CONTENT));
//				layout.addView(mStatusBackground, new FrameLayout.LayoutParams(
//						FrameLayout.LayoutParams.MATCH_PARENT,
//						FrameLayout.LayoutParams.WRAP_CONTENT));
                tempView = view;

			} else {
				LinearLayout mainLayout = new LinearLayout(this);
				mainLayout.setOrientation(LinearLayout.VERTICAL);
				mainLayout.addView(mStatusBackground, new LinearLayout.LayoutParams(
						 LinearLayout.LayoutParams.MATCH_PARENT,
						 statusHeight+getActionBarHeight()));
				if (view.getParent() != null) {
					ViewGroup vgroup = (ViewGroup) view.getParent();
					vgroup.removeView(view);
				}
				mainLayout.addView(view, new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT, 1));
//				if (mSplitBackground.getParent() != null) {
//					ViewGroup vgroup = (ViewGroup) mSplitBackground.getParent();
//					vgroup.removeView(mSplitBackground);
//				}
//				mainLayout.addView(mSplitBackground);
				tempView = mainLayout;
//				if(params == null){
//					super.setContentView(mainLayout);
//				}
//				else{
//					super.setContentView(mainLayout, params);
//				}
			}
		} else {
			tempView = view;
		}
		if(params == null){
			super.setContentView(tempView);
		}
		else{
			super.setContentView(tempView, params);
		}
	}



	public void setIndicatorColor(int color) {
		if(!fullScreenFlag && statusFlag)
		{
			/*if (getActionBar() == null) {
				mStatusBackground.setLayoutParams(new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT, 
						statusHeight));
				mStatusBackground.setBackgroundColor(color);
			} else {
				mStatusBackground.setLayoutParams(new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT, 
						statusHeight+getActionBarHeight()));
				mStatusBackground.setBackgroundColor(color);
				getActionBar().setBackgroundDrawable(new ColorDrawable(color));
				getActionBar().setDisplayUseLogoEnabled(false);
				
				mSplitBackground.setBackgroundColor(SPLIT_ACTIONBAR_COLOR);
				mSplitBackground.setLayoutParams(new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT, 
						dpToPx(this, mSplitActionbarHeight)));
			}
		} else {*/
			if(getActionBar()==null)
			 {
				mStatusBackground.setLayoutParams(new LinearLayout.LayoutParams(
						 LinearLayout.LayoutParams.MATCH_PARENT,
						 statusHeight));
				mStatusBackground.setBackgroundColor(color);
			 }else{
				 mStatusBackground.setLayoutParams(new LinearLayout.LayoutParams(
						 LinearLayout.LayoutParams.MATCH_PARENT,
						 statusHeight+getActionBarHeight()));
			     mStatusBackground.setBackgroundColor(color);
			     getActionBar().setBackgroundDrawable(new ColorDrawable(color));
			     getActionBar().setDisplayUseLogoEnabled(false);

//			     mSplitBackground.setBackgroundColor(SPLIT_ACTIONBAR_COLOR);
//			     mSplitBackground.setLayoutParams(new LinearLayout.LayoutParams(
//			    		 LinearLayout.LayoutParams.MATCH_PARENT,
//			    		 dpToPx(this, mSplitActionbarHeight)));
			 }
		}
	}
	
	public void setFullScreenDisplay(boolean isShow) {
		fullScreenFlag = isShow;
	}

	public void setIndicatorViewGone(boolean isShow) {
		if(mStatusBackground != null){
			if (isShow) {
				mStatusBackground.setVisibility(View.GONE);
			} else {
				mStatusBackground.setVisibility(View.VISIBLE);
			}
		}
	}

	public void setIndicatorFlag(boolean isShow) {
		statusFlag = isShow;
	}
	
	public static int dpToPx(Context context,int dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }
	
	private int getStatusBarHeight() {  
        Class<?> c = null;  
        Object obj = null;  
        Field field = null;  
        int x = 0;  
        try {  
            c = Class.forName("com.android.internal.R$dimen");  
            obj = c.newInstance();  
            field = c.getField("status_bar_height");  
            x = Integer.parseInt(field.get(obj).toString());  
            return getResources().getDimensionPixelSize(x);  
        } catch (Exception e1) {  
            e1.printStackTrace();  
            return STATUS_HEIGHT;  
        }  
    }
	
	private int getActionBarHeight() {
		if(getActionBar() == null) {
			return 0;
		}
		else{
		    int actionBarHeight = getActionBar().getHeight();
		    if (actionBarHeight != 0)
		        return actionBarHeight;
		    final TypedValue tv = new TypedValue();
		    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
		            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
		    } 
		    return actionBarHeight;
		}
	}
	
}
