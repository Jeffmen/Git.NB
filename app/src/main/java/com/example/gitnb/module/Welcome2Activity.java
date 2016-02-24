package com.example.gitnb.module;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.gitnb.R;
import com.example.gitnb.model.User;
import com.example.gitnb.utils.CurrentUser;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Welcome2Activity extends Activity{

    public static final String VIDEO_NAME = "welcome_video.mp4";
    private static int FOR_ANTHORIZE = 300;
    private boolean alreadyJump = false;
    private SimpleDraweeView gifView;
    private ObjectAnimator anim;
    private Button buttonLeft;
    private TextView appName;
    private User me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		me = CurrentUser.get(Welcome2Activity.this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.welcome2);

        findView();
        initView();

        playGif();
        playAnim();
    }

    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
        if (requestCode == FOR_ANTHORIZE && resultCode == RESULT_OK) { 
			jumpToManiActivity();
        }
    }

    private void findView() {
        gifView = (SimpleDraweeView) findViewById(R.id.gifView);
        buttonLeft = (Button) findViewById(R.id.buttonLeft);
        appName = (TextView) findViewById(R.id.appName);
    }

    private void initView() {
		if(me != null){
	    	buttonLeft.setText("WELCOME");
	    	buttonLeft.setOnClickListener(new View.OnClickListener(){
	
				@Override
				public void onClick(View arg0) {
					jumpToManiActivity();
				}
	        	
	        });
		}
		else{
	    	buttonLeft.setText("LOGIN");
	    	buttonLeft.setOnClickListener(new View.OnClickListener(){
	
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(Welcome2Activity.this, GitHubAuthorizeActivity.class);
					startActivityForResult(intent, FOR_ANTHORIZE);
				}
	        	
	        });
		}
    }

    private void playGif() {
        Uri path = (new Uri.Builder()).scheme("res").path(String.valueOf(R.drawable.welcome2)).build();
        DraweeController draweeController= Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                .setUri(path)
                .build();
        gifView.setController(draweeController);
    }

    private void playAnim() {
        anim = ObjectAnimator.ofFloat(appName, "alpha", 0, 1, 0);
        anim.setDuration(8000);
        anim.start();
		if(me != null){
	        anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    Log.i("", "onAnimationEnd");
                    jumpToManiActivity();
                }
            });
		}
    }
    
    private void jumpToManiActivity() {
    	if(!alreadyJump){
	    	alreadyJump = true;
			Intent intent = new Intent(Welcome2Activity.this, MainActivity.class);
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
			finish();
    	}
    }

}
