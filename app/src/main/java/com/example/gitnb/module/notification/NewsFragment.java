package com.example.gitnb.module.notification;

import com.example.gitnb.module.MainFragment;
import com.example.gitnb.module.issue.IssuesFragment;

public class NewsFragment extends MainFragment {
	private String TAG = NewsFragment.class.getName();
	
	@Override
    public void initView(){
        super.initView();
        pagerAdapter = new TabPagerAdapter(getChildFragmentManager());
        pagerAdapter.addFragment(new ReceivedEventsFragment(), "News");
        pagerAdapter.addFragment(new NotificationsFragment(), "Notifications");
        pagerAdapter.addFragment(new IssuesFragment(), "Issues");
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(0);
        pager.setOffscreenPageLimit(3);

        tabs.setupWithViewPager(pager);
        //tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        //tabs.setOnPageChangeListener(new PageListener());
    }

}
