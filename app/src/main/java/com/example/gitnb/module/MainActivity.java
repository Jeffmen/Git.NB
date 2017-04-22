package com.example.gitnb.module;

import com.example.gitnb.R;
import com.example.gitnb.api.GitHub;
import com.example.gitnb.api.rxjava.ApiRxJavaClient;
import com.example.gitnb.model.Notification;
import com.example.gitnb.model.User;
import com.example.gitnb.module.custom.RedPointDrawable;
import com.example.gitnb.module.custom.processor.BlurPostprocessor;
import com.example.gitnb.module.notification.NotificationActivity;
import com.example.gitnb.module.notification.ReceivedEventsFragment;
import com.example.gitnb.module.notification.TabPagerAdapter;
import com.example.gitnb.module.repos.ReposListActivity;
import com.example.gitnb.module.search.HotUserFragment;
import com.example.gitnb.module.search.SearchActivity;
import com.example.gitnb.module.trending.ShowCaseFragment;
import com.example.gitnb.module.trending.TrendingReposFragment;
import com.example.gitnb.module.user.UserDetailActivity;
import com.example.gitnb.utils.CurrentUser;
import com.example.gitnb.utils.Utils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import android.Manifest;
import android.animation.Animator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private Map<Integer,Integer> mFragmentNameByDrawerId = new HashMap<>();
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 12345;
    private static int FOR_NOTIFICATION = 300;
	private NavigationView navigationView;
    private TabPagerAdapter pagerAdapter;
    private DrawerLayout drawerLayout;
    private ViewPager pager;
    private TabLayout tabs;
    private ImageView menu;
    private ImageView search;
    private User me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_main);

        me = CurrentUser.getInstance().getMe();
        if(me == null){
            Intent intent = new Intent(MainActivity.this, GitHubAuthorizeActivity.class);
            startActivity(intent);
            finish();
            return;
        }
		drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        search = (ImageView) findViewById(R.id.search);
        pager = (ViewPager) findViewById(R.id.pager);
        tabs = (TabLayout) findViewById(R.id.tabs);
        menu = (ImageView) findViewById(R.id.menu);

        menu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

//        ImageView titleImage = (ImageView)findViewById(R.id.title_background_image);
//        ColorMatrix colorMatrix = new ColorMatrix();
//        colorMatrix.setSaturation(0);
//        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
//        titleImage.setColorFilter(colorFilter);

        SimpleDraweeView titleImage = (SimpleDraweeView)findViewById(R.id.title_background_image);
        Uri uri = Uri.parse("res://" + getPackageName() + "/" + R.drawable.title_bg_autumn);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setPostprocessor(new BlurPostprocessor(MainActivity.this))
                .build();
        PipelineDraweeController controller = (PipelineDraweeController)
                Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setOldController(titleImage.getController())
                        .build();
        titleImage.setController(controller);

        initTabs();
        initDrawerMap();
        initNavigationView();

        drawerLayout.setVisibility(View.INVISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawerLayout.post(new Runnable() {
                @Override
                public void run() {
                    drawerLayout.setVisibility(View.VISIBLE);
                    float hypot = (float) Math.hypot(drawerLayout.getHeight(), drawerLayout.getWidth());
                    Animator animator = ViewAnimationUtils
                            .createCircularReveal(drawerLayout, drawerLayout.getWidth()/2,
                                    drawerLayout.getHeight()-Utils.dpToPx(MainActivity.this, 50), 0, hypot);
                    animator.setDuration(800);
                    animator.start();
                }
            });
        }
        getNotifications();
        checkPermission();
    }

    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        PERMISSIONS_REQUEST_READ_PHONE_STATE);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    private void initDrawerMap() {
        mFragmentNameByDrawerId.put(R.id.nav_showcase, 0);
        mFragmentNameByDrawerId.put(R.id.nav_news, 1);
        mFragmentNameByDrawerId.put(R.id.nav_trending, 2);
    }

    private void initTabs(){
        pagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new ShowCaseFragment(), "ShowCase");
        pagerAdapter.addFragment(new ReceivedEventsFragment(), "News");
        //pagerAdapter.addFragment(new NotificationsFragment(), "Notifications");
        pagerAdapter.addFragment(new TrendingReposFragment(), "Trending");
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(1);
        pager.setOffscreenPageLimit(3);

        tabs.setupWithViewPager(pager);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //controlFloatButton(tab.getPosition());
                pager.setCurrentItem(tab.getPosition());
                navigationView.getMenu().getItem(tab.getPosition()).setChecked(true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                MainFragment.TabClickListener tabClickListener = (MainFragment.TabClickListener)
                        pagerAdapter.getItem(pager.getCurrentItem());
                tabClickListener.moveToUp();
            }
        });
    }

    private void initNavigationView() {
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.nav_star) {
                    Intent intent = new Intent(MainActivity.this, ReposListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(HotUserFragment.USER, me);
                    intent.putExtras(bundle);
                    intent.putExtra(ReposListActivity.REPOS_TYPE, ReposListActivity.REPOS_TYPE_USER_STARRED);
                    startActivity(intent);
                } else if (menuItem.getItemId() == R.id.nav_notification) {
                    Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                    startActivityForResult(intent, FOR_NOTIFICATION);
                } else if (menuItem.getItemId() == R.id.sub_exit) {
                    Dialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle("Caution")
                            .setMessage("Are you sure to sign out?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    CurrentUser.getInstance().delete();
                                    me = null;
                                    SharedPreferences read = getSharedPreferences(GitHub.NAME, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = read.edit();
                                    editor.putBoolean("first_time", true);
                                    editor.commit();
                                    finish();
                                    Intent intent = new Intent(MainActivity.this, Welcome3Activity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("No", null)
                            .setCancelable(false).create();
                    dialog.show();
                } else if (menuItem.getItemId() == R.id.sub_about) {
                    Intent intent = new Intent(MainActivity.this, ReposListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(HotUserFragment.USER, me);
                    intent.putExtras(bundle);
                    intent.putExtra(ReposListActivity.REPOS_TYPE, ReposListActivity.REPOS_TYPE_USER_STARRED);
                    startActivity(intent);
                } else {
                    menuItem.setChecked(true);
                    pager.setCurrentItem(mFragmentNameByDrawerId.get(menuItem.getItemId()));
                    drawerLayout.closeDrawers();
                }
                return true;
            }
        });
        View headView = navigationView.getHeaderView(0);
        SimpleDraweeView me_background = (SimpleDraweeView) headView.findViewById(R.id.me_background);
        SimpleDraweeView me_avatar = (SimpleDraweeView) headView.findViewById(R.id.me_avatar);
        TextView me_login = (TextView) headView.findViewById(R.id.me_login);
        if(me == null){
            me_login.setText("Login...");
            me_avatar.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(MainActivity.this, GitHubAuthorizeActivity.class);
                    startActivity(intent);
                }

            });
        }
        else {
            me_login.setText(me.getLogin());
            me_login.setOnClickListener(null);

            me_avatar.setImageURI(Uri.parse(me.getAvatar_url()));
            me_avatar.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(MainActivity.this, UserDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(HotUserFragment.USER, me);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            });

            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(me.getAvatar_url()))
                    .setPostprocessor(new BlurPostprocessor(MainActivity.this))
                    .build();

            PipelineDraweeController controller = (PipelineDraweeController)
                    Fresco.newDraweeControllerBuilder()
                            .setImageRequest(request)
                            .setOldController(me_background.getController())
                            .build();
            me_background.setController(controller);
        }
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == FOR_NOTIFICATION){
            getNotifications();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onBackPressed() {
        Intent MyIntent = new Intent(Intent.ACTION_MAIN);
        MyIntent.addCategory(Intent.CATEGORY_HOME);
        startActivity(MyIntent);
    }

    public void updateRedPoint(){
        RedPointDrawable redPointDrawable1 = RedPointDrawable.wrap(this, menu.getDrawable());
        redPointDrawable1.setGravity(Gravity.RIGHT);
        menu.setImageDrawable(redPointDrawable1);
        /*
        Menu menu = navigationView.getMenu();
        int size = menu.size();
        for (int i = 0; i < size; i++) {
            MenuItem item = menu.getItem(i);
            if (item.getItemId() == R.id.nav_notification) {
                RedPointDrawable redPointDrawable = RedPointDrawable.wrap(this, item.getIcon());
                redPointDrawable.setGravity(Gravity.RIGHT);
                item.setIcon(redPointDrawable);
            }
        }*/
    }

    private void setMenuCounter(@IdRes int itemId, int count) {
        TextView view = (TextView) navigationView.getMenu().findItem(itemId)
                .getActionView().findViewById(R.id.msg);
        if(count > 0) {
            updateRedPoint();
            view.setText(String.valueOf(count > 99 ? 99 : count));
        }
        else{
            menu.setImageResource(R.drawable.ic_menu_white_24dp);
            view.setVisibility(View.GONE);
        }
    }

    public void getNotifications(){
        ApiRxJavaClient.getNewInstance().getService().getNotifications()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<Notification>>() {
                    @Override
                    public void onNext(ArrayList<Notification> result) {
                        setMenuCounter(R.id.nav_notification, result.size());
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable error) {

                    }
                });
    }
}
