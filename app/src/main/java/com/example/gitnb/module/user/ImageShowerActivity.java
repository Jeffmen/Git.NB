package com.example.gitnb.module.user;

import com.example.gitnb.R;
import com.facebook.drawee.view.SimpleDraweeView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;

public class ImageShowerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_shower);
        Intent intent = getIntent();
        String url = intent.getStringExtra(UserDetailActivity.AVATAR_URL);
        final SimpleDraweeView user_avatar = (SimpleDraweeView) findViewById(R.id.user_avatar);
        user_avatar.setImageURI(Uri.parse(url));
        user_avatar.post(new Runnable() {
            @Override
            public void run() {
                startAni(user_avatar);
            }
        });
    }

    private void startAni(View v){
        int x = v.getWidth()/2;
        int y = v.getHeight()/2;
        int radius = x<y?x:y;

        Animator anim = ViewAnimationUtils.createCircularReveal(v, x, y, 0, radius);
        anim.setDuration(700);
        anim.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

}
