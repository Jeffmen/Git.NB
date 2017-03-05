package com.example.gitnb.module.search;

import com.example.gitnb.module.MainFragment;
import com.example.gitnb.module.notification.TabPagerAdapter;

public class SearchFragment extends MainFragment {
	private String TAG = SearchFragment.class.getName();

    @Override
    public void initView(){
        super.initView();
        pagerAdapter = new TabPagerAdapter(getChildFragmentManager());
        pagerAdapter.addFragment(new HotUserFragment(), "User");
        pagerAdapter.addFragment(new HotReposFragment(), "Repository");
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(1);
        pager.setOffscreenPageLimit(2);

        tabs.setupWithViewPager(pager);
        //tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        //tabs.setOnPageChangeListener(new PageListener());
    }

}
