package com.example.gitnb.module.user;

import com.example.gitnb.R;
import com.example.gitnb.app.BaseSwipeActivity;
import com.example.gitnb.model.User;
import com.example.gitnb.module.custom.processor.BlurPostprocessor;
import com.example.gitnb.module.custom.processor.MaskPostprocessor;
import com.example.gitnb.module.repos.EventListActivity;
import com.example.gitnb.module.repos.ReposListActivity;
import com.example.gitnb.module.search.HotUserFragment;
import com.example.gitnb.wxapi.WeiXin;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.joanzapata.iconify.widget.IconButton;
import com.joanzapata.iconify.widget.IconTextView;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserDetailActivity extends BaseSwipeActivity implements PopupMenu.OnMenuItemClickListener {

	private String TAG = UserDetailActivity.class.getName();
	public static String AVATAR_URL = "avatar_url";
	private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private UserOperationAdapter operationAdapter;
    private SimpleDraweeView user_background;
    private SimpleDraweeView user_avatar;
	private RecyclerView recyclerView;
	private IconButton faButton;
	private boolean isGetFollow = false;
	private boolean isGetColor = false;
	private boolean isFollow = false;
	private int color = -1;
    private IWXAPI api;
	private User user;
	
    protected void setTitle(TextView view){
        if(user != null && !user.getLogin().isEmpty()){
        	view.setText(user.getLogin());
        }else{
        	view.setText("NULL");
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        user = intent.getParcelableExtra(HotUserFragment.USER);
        setContentView(R.layout.activity_user_detail);
        user_avatar = (SimpleDraweeView)findViewById(R.id.user_avatar);

		operationAdapter = new UserOperationAdapter(this, null);
		operationAdapter.setOnItemClickListener(new UserOperationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                jumpToActivity(position);
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(operationAdapter);

		faButton = (IconButton) findViewById(R.id.faButton);
		faButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isFollow) {
                    unFollowUser();
                } else {
                    followUser();
                }
            }
        });

		user_avatar = (SimpleDraweeView)findViewById(R.id.user_avatar);
		user_avatar.setImageURI(Uri.parse(user.getAvatar_url()));
        initUserBackground();

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //The Refresh must be only active when the offset is zero :
                getSwipeRefreshLayout().setEnabled(verticalOffset == 0);

                float alpha = Math.abs(verticalOffset / (appBarLayout.getHeight() -
                        getToolbar().getHeight()
                        - getResources().getDimension(R.dimen.system_ui_height)));
                user_avatar.setAlpha(1 - alpha);
            }
        });
        api = WXAPIFactory.createWXAPI(this, WeiXin.AppID, true);
        api.registerApp(WeiXin.AppID);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
//		if(isFollow) {
//			menu.findItem(R.id.menu_item_unfollow).setVisible(true);
//			menu.findItem(R.id.menu_item_follow).setVisible(false);
//		}
//		else{
//			menu.findItem(R.id.menu_item_follow).setVisible(true);
//			menu.findItem(R.id.menu_item_unfollow).setVisible(false);
//		}
		MenuItem more = menu.findItem(R.id.menu_item_more);
		more.getActionView().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				PopupMenu popupMenu = new PopupMenu(UserDetailActivity.this, v);
				popupMenu.setOnMenuItemClickListener(UserDetailActivity.this);
				popupMenu.inflate(R.menu.user_detail_menu);
				if(isFollow) {
					popupMenu.getMenu().findItem(R.id.menu_item_follow).setVisible(false);
					//popupMenu.getMenu().findItem(R.id.menu_item_unfollow).setVisible(true);
				}
				else{
					//popupMenu.getMenu().findItem(R.id.menu_item_follow).setVisible(true);
					popupMenu.getMenu().findItem(R.id.menu_item_unfollow).setVisible(false);
				}
				//popupMenu.getMenu().findItem(R.id.menu_item_share).setVisible(true);
				/*
				try {
					Field field = popupMenu.getClass().getDeclaredField("mPopup");
					field.setAccessible(true);
					MenuPopupHelper mHelper = (MenuPopupHelper) field.get(popupMenu);
					mHelper.setForceShowIcon(true);
				} catch (Exception e) {
					e.printStackTrace();
				}*/
				popupMenu.show();
			}
		});
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_follow:
				if (isFollow) {
					unFollowUser();
				} else {
					followUser();
				}
				break;
			case R.id.menu_item_share:
				PopupMenu popupMenu = new PopupMenu(this, item.getActionView());
				popupMenu.setOnMenuItemClickListener(this);
				popupMenu.inflate(R.menu.repos_detail_menu);
				popupMenu.show();
//				final Intent intent = new Intent(Intent.ACTION_SEND);
//				intent.setType("text/plain")
//				.putExtra(Intent.EXTRA_TEXT, user.getName());
//				startActivity(intent);
//				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void toolBarColorChange(int color){
		//mCollapsingToolbarLayout.setContentScrimColor(color);
	}

	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_follow:
				if (isFollow) {
					unFollowUser();
				} else {
					followUser();
				}
				return true;
			case R.id.menu_item_share:
				share2weixin(1);
				return true;
		}
		return false;
	}

    private void initUserBackground(){
		mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
		mCollapsingToolbarLayout.setTitle(user.getLogin());

		//mCollapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);//设置还没收缩时状态下字体颜色
		//mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);//设置收缩后Toolbar上字体的颜色
		//mCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ToolbarTitleAppearance);
		//mCollapsingToolbarLayout.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
		color = getResources().getColor(R.color.orange_yellow);
		user_background = (SimpleDraweeView)findViewById(R.id.user_background);
		//user_background.setImageURI(Uri.parse(user.getAvatar_url()));
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(user.getAvatar_url()))
                .setPostprocessor(new BlurPostprocessor(this))
                .build();

        PipelineDraweeController controller = (PipelineDraweeController)
                Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setOldController(user_background.getController())
                        .build();
        user_background.setController(controller);
		processImageWithPaletteApi(request, controller);
    }

	private void processImageWithPaletteApi(ImageRequest request, final DraweeController controller) {
		DataSource<CloseableReference<CloseableImage>> dataSource =
				Fresco.getImagePipeline().fetchDecodedImage(request, user_background.getContext());
		dataSource.subscribe(new BaseBitmapDataSubscriber() {
			@Override
			protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

			}

			@Override protected void onNewResultImpl(@Nullable Bitmap bitmap) {
				Palette.from(bitmap).maximumColorCount(24).generate(new Palette.PaletteAsyncListener() {
					@Override
					public void onGenerated(Palette palette) {
						isGetColor = true;
						if (palette != null) {
							if(palette.getMutedSwatch() != null) {
								color = palette.getMutedColor(color);
							}
							else if(palette.getVibrantSwatch() != null) {
								color = palette.getVibrantColor(color);
							}
							else if(palette.getDominantSwatch() != null) {
								color = palette.getDominantColor(color);
							}
							showFollowButton();
						}
					}
				});
			}
		}, CallerThreadExecutor.getInstance());

		user_background.setController(controller);
	}

	private void showFollowButton(){
		if(isGetColor && isGetFollow) {
			AnimatorSet bouncer = new AnimatorSet();
			ObjectAnimator alpha = ObjectAnimator.ofFloat(faButton, "alpha", 0.0f, 1.0f);
			ObjectAnimator scaleX = ObjectAnimator.ofFloat(faButton, "scaleX", 0.0f, 1.0f);
			ObjectAnimator scaleY = ObjectAnimator.ofFloat(faButton, "scaleY", 0.0f, 1.0f);
			bouncer.play(alpha).with(scaleX).with(scaleY);
			bouncer.setDuration(500);
			bouncer.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationStart(Animator animation) {
					faButton.setVisibility(View.VISIBLE);
					if (isFollow) {
						faButton.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
						faButton.setTextColor(Color.WHITE);
					} else {
						faButton.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
						faButton.setTextColor(color);
					}
				}
			});
			bouncer.start();
			invalidateOptionsMenu();
		}
	}

	private void jumpToActivity(int position){
		Intent intent = null;
		Bundle bundle = new Bundle();

		switch(operationAdapter.getItemViewType(position)) {
			case UserOperationAdapter.TYPE_EVENTS_VIEW:
				intent = new Intent(UserDetailActivity.this, EventListActivity.class);
				bundle.putParcelable(HotUserFragment.USER, user);
				intent.putExtras(bundle);
				intent.putExtra(EventListActivity.EVENT_TYPE, EventListActivity.EVENT_TYPE_USER);
				break;
			case UserOperationAdapter.TYPE_ORGANIZATIONS_VIEW:
				intent = new Intent(UserDetailActivity.this, OrganizationListActivity.class);
				bundle.putParcelable(HotUserFragment.USER, user);
				intent.putExtras(bundle);
				intent.putExtra(OrganizationListActivity.ORGANIZATION_TYPE, OrganizationListActivity.ORGANIZATION_TYPE_USER);
				break;
			case UserOperationAdapter.TYPE_STARRED_VIEW:
				intent = new Intent(UserDetailActivity.this, ReposListActivity.class);
				bundle.putParcelable(HotUserFragment.USER, user);
				intent.putExtras(bundle);
				intent.putExtra(ReposListActivity.REPOS_TYPE, ReposListActivity.REPOS_TYPE_USER_STARRED);
				break;
			case UserOperationAdapter.TYPE_FOLLOWERS_VIEW:
				intent = new Intent(UserDetailActivity.this, UserListActivity.class);
				bundle.putParcelable(HotUserFragment.USER, user);
				intent.putExtras(bundle);
				intent.putExtra(UserListActivity.USER_TYPE, UserListActivity.USER_TYPE_FOLLOWER);
				break;
			case UserOperationAdapter.TYPE_FOLLOWING_VIEW:
				intent = new Intent(UserDetailActivity.this, UserListActivity.class);
				bundle.putParcelable(HotUserFragment.USER, user);
				intent.putExtras(bundle);
				intent.putExtra(UserListActivity.USER_TYPE, UserListActivity.USER_TYPE_FOLLOWING);
				break;
			case UserOperationAdapter.TYPE_REPOSITORIES_VIEW:
				intent = new Intent(UserDetailActivity.this, ReposListActivity.class);
				bundle.putParcelable(HotUserFragment.USER, user);
				intent.putExtras(bundle);
				intent.putExtra(ReposListActivity.REPOS_TYPE, ReposListActivity.REPOS_TYPE_USER_REPOS);
				break;
		}
		startActivity(intent);
	}

	@Override
    protected void startRefresh(){
		super.startRefresh();
		getSingleUser();
		checkFollowing();
	}

	@Override
    protected void endRefresh(){
		super.endRefresh();
        TextView title_name = (TextView)findViewById(R.id.title_name);
        title_name.setText(user.getName());
		operationAdapter.updateUser(user);
        recyclerView.setVisibility(View.VISIBLE);
	}

    private void share2weixin(int flag) {
        if (!api.isWXAppInstalled()) {
            Toast.makeText(UserDetailActivity.this, "您还未安装微信客户端",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        WXWebpageObject webPage = new WXWebpageObject();
		webPage.webpageUrl = user.getHtml_url();
        WXMediaMessage msg = new WXMediaMessage(webPage);

        msg.title = user.getLogin();
        msg.description = user.getName();
        msg.setThumbImage(convertViewToBitmap(user_avatar));
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }

	private void getSingleUser(){
		getApiService().getSingleUser(user.getLogin())
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<User>() {
					@Override
					public void onNext(User result) {
						user = result;
						endRefresh();
					}

					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable error) {
						endError(error.getMessage());
					}
				});
	} 
	
	private void checkFollowing(){
		getApiService().checkFollowing(user.getLogin())
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Object>() {
					@Override
					public void onNext(Object result) {
						isGetFollow = true;
						isFollow = true;
						showFollowButton();
					}

					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable error) {
						isGetFollow = true;
						isFollow = false;
						showFollowButton();
					}
				});
	}
	
	private void followUser(){
		final Snackbar snackbar = Snackbar.make(getSwipeRefreshLayout(), "Following ...", Snackbar.LENGTH_INDEFINITE);
		snackbar.show();
		getApiService().followUser(user.getLogin())
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Object>() {
					@Override
					public void onNext(Object result) {
						snackbar.dismiss();
						isFollow = true;
						showFollowButton();
					}

					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable error) {
						snackbar.dismiss();
						endError(error.getMessage());
					}
				});
	}	
	
	private void unFollowUser(){
		final Snackbar snackbar = Snackbar.make(getSwipeRefreshLayout(), "UnFollowing ...", Snackbar.LENGTH_INDEFINITE);
		snackbar.show();
		getApiService().unfollowUser(user.getLogin())
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Object>() {
					@Override
					public void onNext(Object result) {
						snackbar.dismiss();
						isFollow = false;
						showFollowButton();
					}

					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable error) {
						snackbar.dismiss();
						endError(error.getMessage());
					}
				});
	}

	public static Bitmap convertViewToBitmap(View view){
//		view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}
}
