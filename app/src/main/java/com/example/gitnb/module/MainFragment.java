package com.example.gitnb.module;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gitnb.R;
import com.example.gitnb.module.notification.TabPagerAdapter;

public class MainFragment extends Fragment {
	private String TAG = MainFragment.class.getName();
    protected FloatingActionButton faButton;
    protected TabPagerAdapter pagerAdapter;
    protected CoordinatorLayout layout;
    protected ViewPager pager;
    protected TabLayout tabs;


    public interface TabClickListener{
        Void moveToUp();
    }

    public interface UpdateListener{
        Void update();
    }

    public static MainFragment newInstance() {
        MainFragment mainFragment = new MainFragment();
        return mainFragment;
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, container, false);

        faButton = (FloatingActionButton) view.findViewById(R.id.faButton);
        layout = (CoordinatorLayout) view.findViewById(R.id.layout);
        pager = (ViewPager) view.findViewById(R.id.pager);
        tabs = (TabLayout) view.findViewById(R.id.tabs);
        initView();
        setEvent();
        return view;
    }

    public void initView(){

    }

    public void setEvent(){
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                TabClickListener tabClickListener = (TabClickListener) pagerAdapter.getItem(pager.getCurrentItem());
                tabClickListener.moveToUp();
            }
        });
    }

    private void controlFloatButton(int position){
        if(position == 0|| position==2){
            faButtonAni(false);
        }
        else{
            faButtonAni(true);
        }
    }

    public void faButtonAni(final boolean visible){
        AnimatorSet bouncer = new AnimatorSet();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(faButton, "alpha", visible?1.0f:0.0f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(faButton, "scaleX", visible?1.0f:0.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(faButton, "scaleY", visible?1.0f:0.0f);
        bouncer.play(alpha).with(scaleX).with(scaleY);
        bouncer.setDuration(500);
        bouncer.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (visible) {
                    faButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!visible) {
                    faButton.setVisibility(View.INVISIBLE);
                }
            }
        });
        bouncer.start();
    }

}
