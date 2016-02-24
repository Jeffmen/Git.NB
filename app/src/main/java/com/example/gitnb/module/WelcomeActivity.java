package com.example.gitnb.module;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.example.gitnb.model.User;
import com.example.gitnb.utils.CurrentUser;
import com.example.gitnb.R;

public class WelcomeActivity extends AppCompatActivity {

    public static final String VIDEO_NAME = "welcome_video.mp4";
    private static int FOR_ANTHORIZE = 300;
    private boolean alreadyJump = false;
    private VideoView mVideoView;
    private ObjectAnimator anim;
    private Button buttonLeft;
    private TextView appName;
    private User me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		me = CurrentUser.get(WelcomeActivity.this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.welcome);

        findView();
        initView();

        File videoFile = getFileStreamPath(VIDEO_NAME);
        if (!videoFile.exists()) {
            videoFile = copyVideoFile();
        }

        playVideo(videoFile);
        playAnim();
    }

    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
        if (requestCode == FOR_ANTHORIZE && resultCode == RESULT_OK) { 
			jumpToManiActivity();
        }
    }

    private void findView() {
        mVideoView = (VideoView) findViewById(R.id.videoView);
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
					Intent intent = new Intent(WelcomeActivity.this, GitHubAuthorizeActivity.class);
					startActivityForResult(intent, FOR_ANTHORIZE);
				}
	        	
	        });
		}
    }

    private void playVideo(File videoFile) {
        mVideoView.setVideoPath(videoFile.getPath());
        mVideoView.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        });
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
			Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
			finish();
    	}
    }

    @NonNull
    private File copyVideoFile() {
        File videoFile;
        try {
            FileOutputStream fos = openFileOutput(VIDEO_NAME, MODE_PRIVATE);
            InputStream in = getResources().openRawResource(R.raw.welcome_video);
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = in.read(buff)) != -1) {
                fos.write(buff, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        videoFile = getFileStreamPath(VIDEO_NAME);
        if (!videoFile.exists())
            throw new RuntimeException("video file has problem, are you sure you have welcome_video.mp4 in res/raw folder?");
        return videoFile;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }

}
