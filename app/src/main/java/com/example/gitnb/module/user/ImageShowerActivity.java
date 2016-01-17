package com.example.gitnb.module.user;

import com.example.gitnb.R;
import com.facebook.drawee.view.SimpleDraweeView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;

public class ImageShowerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_shower);
        Intent intent = getIntent();
        String url = intent.getStringExtra(UserDetailActivity.AVATAR_URL);
        SimpleDraweeView user_avatar = (SimpleDraweeView) findViewById(R.id.user_avatar);
        user_avatar.setImageURI(Uri.parse(url));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

}
