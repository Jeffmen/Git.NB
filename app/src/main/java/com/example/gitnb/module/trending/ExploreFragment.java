package com.example.gitnb.module.trending;

import com.example.gitnb.R;
import com.example.gitnb.module.notification.TabPagerAdapter;
import com.example.gitnb.module.MainFragment;

public class ExploreFragment extends MainFragment {
	private String TAG = ExploreFragment.class.getName();

    @Override
    public void initView(){
        super.initView();
        pagerAdapter = new TabPagerAdapter(getChildFragmentManager());
        pagerAdapter.addFragment(new ShowCaseFragment(), "Showcase");
        pagerAdapter.addFragment(new TrendingReposFragment(), "Trending");
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(1);
        pager.setOffscreenPageLimit(2);

        tabs.setupWithViewPager(pager);
        //tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        //tabs.setSelectedTabIndicatorColor(Color.WHITE);
        //tabs.setTabTextColors(getResources().getColor(R.color.transparent_dark_gray), Color.WHITE);
        //tabs.setOnPageChangeListener(new PageListener());
    }

}
