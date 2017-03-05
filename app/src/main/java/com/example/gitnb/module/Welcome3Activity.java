package com.example.gitnb.module;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.example.gitnb.R;
import com.example.gitnb.api.GitHub;
import com.example.gitnb.model.User;
import com.example.gitnb.utils.CurrentUser;

public class Welcome3Activity extends Activity{

    private static int FOR_AUTHORIZE = 300;
    private LottieAnimationView G;
    private LottieAnimationView N;
    private LottieAnimationView B;
    private LinearLayout button_ll;
    private int stopCount = 0;
    private Button buttonLeft;
    private User me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences read = getSharedPreferences(GitHub.NAME, Context.MODE_PRIVATE);
        boolean first_time = read.getBoolean("first_time", true);
        setContentView(R.layout.welcome3);
        G = (LottieAnimationView)findViewById(R.id.G);
        N = (LottieAnimationView)findViewById(R.id.N);
        B = (LottieAnimationView)findViewById(R.id.B);
        View icon = findViewById(R.id.icon);

        ObjectAnimator anim = ObjectAnimator.ofFloat(icon, "alpha", 1f, 0.1f, 1f);
        anim.setDuration(2000);
        anim.setInterpolator(new BounceInterpolator());

        if(first_time){
            me = CurrentUser.getInstance().getMe();
            initView();

            //PathView pathView = (PathView) findViewById(R.id.pathView);
            //pathView.getPathAnimator().
            //        delay(100).
            //        duration(3000).
            //       interpolator(new AccelerateDecelerateInterpolator()).
            //        start();
        }
        else{
            G.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    stopCount++;
                    jumpToManiActivity();
                }
            });
            N.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    stopCount++;
                    jumpToManiActivity();
                }
            });
            B.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    stopCount++;
                    jumpToManiActivity();
                }
            });
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    G.loop(false);
                    N.loop(false);
                    B.loop(false);
                }
            });
        }
        anim.start();
    }

    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
        if (requestCode == FOR_AUTHORIZE && resultCode == RESULT_OK) {
			jumpToManiActivity();
        }
    }

    private void initView() {
        button_ll = (LinearLayout) findViewById(R.id.button_ll);
        buttonLeft = (Button) findViewById(R.id.buttonLeft);
        button_ll.setVisibility(View.VISIBLE);
		if(me != null){
	    	buttonLeft.setText("WELCOME");
	    	buttonLeft.setOnClickListener(new View.OnClickListener(){
	
				@Override
				public void onClick(View arg0) {
                    stopCount = 3;
					jumpToManiActivity();
				}
	        	
	        });
		}
		else{
	    	buttonLeft.setText("LOGIN");
	    	buttonLeft.setOnClickListener(new View.OnClickListener(){
	
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(Welcome3Activity.this, GitHubAuthorizeActivity.class);
					startActivityForResult(intent, FOR_AUTHORIZE);
				}
	        	
	        });
		}
    }
    
    private void jumpToManiActivity() {
    	if(stopCount == 3){
			Intent intent = new Intent(Welcome3Activity.this, MainActivity.class);
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
			finish();
    	}
    }

}
