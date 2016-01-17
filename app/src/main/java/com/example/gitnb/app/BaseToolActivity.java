package com.example.gitnb.app;

import com.example.gitnb.R;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public abstract class BaseToolActivity extends AppCompatActivity {
    public Toolbar mToolBar;
    private FrameLayout mContentView;
    private View mUserView;
    private LayoutInflater mInflater;

    private static int[] ATTRS = {
            R.attr.windowActionBarOverlay,
            R.attr.actionBarSize
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = LayoutInflater.from(this);
    }

    @Override
    public void setContentView(int layoutResId) {
        initContentView();
        initUserView(layoutResId);
        initToolBar();
        setContentView(getContentView());
        setSupportActionBar(getToolBar());
        onCreateCustomToolBar(getToolBar()) ;
    }

    private void initContentView() {
        mContentView = new FrameLayout(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
        		ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContentView.setLayoutParams(params);

    }

    private void initUserView(int id) {
        mUserView = mInflater.inflate(id, null);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TypedArray typedArray = this.getTheme().obtainStyledAttributes(ATTRS);
        boolean overly = typedArray.getBoolean(0, false);
        int toolBarSize = (int) typedArray.getDimension(1,(int) this.getResources().getDimension(R.dimen.abc_action_bar_default_height_material));
        typedArray.recycle();
        /*如果是悬浮状态，则不�?要设置间�?*/
        params.topMargin = overly ? 0 : toolBarSize;
        mContentView.addView(mUserView, params);

    }

    private void initToolBar() {
        View toolbar = mInflater.inflate(R.layout.toolbar, mContentView);
        mToolBar = (Toolbar) toolbar.findViewById(R.id.toolbar);
    }

    public FrameLayout getContentView() {
        return mContentView;
    }

    public Toolbar getToolBar() {
        return mToolBar;
    }
    
    public void onCreateCustomToolBar(Toolbar toolbar){
        toolbar.setContentInsetsRelative(0,0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true ;
        }
        return super.onOptionsItemSelected(item);
    }
}