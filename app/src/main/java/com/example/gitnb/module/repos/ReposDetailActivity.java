package com.example.gitnb.module.repos;

import com.example.gitnb.R;
import com.example.gitnb.api.client.OKHttpClient;
import com.example.gitnb.api.RetrofitNetworkAbs;
import com.example.gitnb.app.BaseSwipeActivity;
import com.example.gitnb.model.Issue;
import com.example.gitnb.model.Repository;
import com.example.gitnb.module.custom.processor.BlurPostprocessor;
import com.example.gitnb.module.issue.IssueListActivity;
import com.example.gitnb.module.search.HotReposFragment;
import com.example.gitnb.module.search.HotUserFragment;
import com.example.gitnb.module.user.UserDetailActivity;
import com.example.gitnb.module.user.UserListActivity;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.joanzapata.iconify.widget.IconButton;
import com.joanzapata.iconify.widget.IconTextView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ReposDetailActivity extends BaseSwipeActivity{

	private String TAG = ReposDetailActivity.class.getName();
	public static String CONTENT_URL = "content_url";
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
	private ReposOperationAdapter operationAdapter;
	private SimpleDraweeView user_background;
	private SimpleDraweeView user_avatar;
    private RecyclerView recyclerView;
	private CoordinatorLayout layout;
	private IconButton faButton;
	private boolean isGetStar = false;
	private boolean isGetColor = false;
	private boolean isStar = false;
	private Repository repos;
	private int color = -1;

    protected void setTitle(TextView view){
        if(repos != null && !repos.getName().isEmpty()){
        	view.setText(repos.getName());
        }else{
        	view.setText("NULL");
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        repos = intent.getParcelableExtra(HotReposFragment.REPOS);
        setContentView(R.layout.activity_repo_detail);

		layout = (CoordinatorLayout)findViewById(R.id.layout);
        operationAdapter = new ReposOperationAdapter(this, null);
        operationAdapter.setOnItemClickListener(new ReposOperationAdapter.OnItemClickListener() {
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
				if (isStar) {
					unStarRepo();
				} else {
					starRepo();
				}
			}
		});

		user_avatar = (SimpleDraweeView)findViewById(R.id.user_avatar);
		AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
		appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //The Refresh must be only active when the offset is zero :
                getSwipeRefreshLayout().setEnabled(verticalOffset == 0);

                float alpha = Math.abs(verticalOffset / (appBarLayout.getHeight() -
                        getToolbar().getHeight()
                        - getResources().getDimension(R.dimen.system_ui_height)));
				if(user_avatar != null)
                	user_avatar.setAlpha(1 - alpha);
            }
        });

		if ( false && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			layout.setVisibility(View.INVISIBLE);
			layout.post(new Runnable() {
				@Override
				public void run() {
					layout.setVisibility(View.VISIBLE);
					float hypot = (float) Math.hypot(layout.getHeight(), layout.getWidth());
					Animator animator = ViewAnimationUtils
							.createCircularReveal(layout, 0, layout.getHeight(), 0, hypot);
					animator.setDuration(800);
					animator.start();
				}
			});
		}
	}

	private void initUserBackground(){
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mCollapsingToolbarLayout.setTitle(repos.getName());

        TextView title_name = (TextView)findViewById(R.id.title_name);
        title_name.setText(repos.getOwner().getLogin());

		user_avatar.setImageURI(Uri.parse(repos.getOwner().getAvatar_url()));
		user_avatar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (repos.getOwner() != null) {
					Intent intent = new Intent(ReposDetailActivity.this, UserDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putParcelable(HotUserFragment.USER, repos.getOwner());
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});

		color = getResources().getColor(R.color.orange_yellow);
		user_background = (SimpleDraweeView)findViewById(R.id.user_background);
		ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(repos.getOwner().getAvatar_url()))
				.setPostprocessor(new BlurPostprocessor(ReposDetailActivity.this))
				.build();
		PipelineDraweeController controller = (PipelineDraweeController)
				Fresco.newDraweeControllerBuilder()
						.setImageRequest(imageRequest)
						.setOldController(user_background.getController())
						.build();
		user_background.setController(controller);
		processImageWithPaletteApi(imageRequest, controller);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.repos_detail_menu, menu);
//		shareActionProvider = (ShareActionProvider) item.getActionProvider();

		//返回true,显示菜单
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(isStar) {
			menu.findItem(R.id.menu_item_unstar).setVisible(true);
			menu.findItem(R.id.menu_item_star).setVisible(false);
		}
		else{
			menu.findItem(R.id.menu_item_star).setVisible(true);
			menu.findItem(R.id.menu_item_unstar).setVisible(false);
		}
		menu.findItem(R.id.menu_item_share).setVisible(true);
		menu.findItem(R.id.menu_item_fork).setVisible(true);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_unstar:
				unStarRepo();
				break;
			case R.id.menu_item_star:
				starRepo();
				break;
			case R.id.menu_item_fork:
				forkRepo();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public void toolBarColorChange(final int color){
		this.color = color;
		//mCollapsingToolbarLayout.setContentScrimColor(color);
        operationAdapter.setIconPrimaryColor(color);
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
							showStar();
						}
					}
				});
			}
		}, CallerThreadExecutor.getInstance());

		user_background.setController(controller);
	}

	private void showStar(){
		if(isGetColor && isGetStar) {
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
					if (isStar) {
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

    public void jumpToActivity(int position){
        Intent intent = null;
        Bundle bundle = new Bundle();

        switch(operationAdapter.getItemViewType(position)) {
            case ReposOperationAdapter.TYPE_STARGAZERS_VIEW:
                intent = new Intent(ReposDetailActivity.this, UserListActivity.class);
                bundle.putParcelable(HotReposFragment.REPOS, repos);
                intent.putExtras(bundle);
                intent.putExtra(UserListActivity.USER_TYPE, UserListActivity.USER_TYPE_STARGZER);
                break;
            case ReposOperationAdapter.TYPE_READ_ME_VIEW:
                intent = new Intent(ReposDetailActivity.this, ReposContentActivity.class);
				bundle.putParcelable(HotReposFragment.REPOS, repos);
				intent.putExtras(bundle);
                intent.putExtra(CONTENT_URL, repos.getUrl());
                break;
            case ReposOperationAdapter.TYPE_CONTRIBUTORS_VIEW:
                intent = new Intent(ReposDetailActivity.this, UserListActivity.class);
                bundle.putParcelable(HotReposFragment.REPOS, repos);
                intent.putExtras(bundle);
                intent.putExtra(UserListActivity.USER_TYPE, UserListActivity.USER_TYPE_CONTRIBUTOR);
                break;
            case ReposOperationAdapter.TYPE_SOURCES_VIEW:
                intent = new Intent(ReposDetailActivity.this, ReposContentsListActivity.class);
                bundle.putParcelable(HotReposFragment.REPOS, repos);
                intent.putExtras(bundle);
                break;
            case ReposOperationAdapter.TYPE_EVENTS_VIEW:
                intent = new Intent(ReposDetailActivity.this, EventListActivity.class);
                bundle.putParcelable(HotReposFragment.REPOS, repos);
                intent.putExtras(bundle);
                intent.putExtra(EventListActivity.EVENT_TYPE, EventListActivity.EVENT_TYPE_REPOS);
                break;
			case ReposOperationAdapter.TYPE_ISSUE_VIEW:
				intent = new Intent(ReposDetailActivity.this, IssueListActivity.class);
				bundle.putParcelable(HotReposFragment.REPOS, repos);
				intent.putExtras(bundle);
				intent.putExtra(IssueListActivity.ISSUE_TYPE, IssueListActivity.ISSUE_TYPE_REPOS);
				break;
        }
        startActivity(intent);
    }

    @Override
    protected void startRefresh(){
    	super.startRefresh();
        getRepositoryInfo();
    }

    @Override
    protected void endRefresh(){
    	super.endRefresh();
        initUserBackground();
        operationAdapter.updateReposotory(repos);
        recyclerView.setVisibility(View.VISIBLE);
    }
    
    private void getRepositoryInfo(){
    	OKHttpClient.getNewInstance().setNetworkListener(new RetrofitNetworkAbs.NetworkListener<Repository>() {

			@Override
			public void onOK(Repository ts) {
				repos = ts;
		        checkIfRepoIsStarred();
				endRefresh();
			}

			@Override
			public void onError(String Message) {
				endError(Message);
			}
			
    	}).request(repos.getUrl(), Repository.class);
    }
    
	private void checkIfRepoIsStarred(){
        getApiService().checkIfRepoIsStarred(repos.getOwner().getLogin(), repos.getName())
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Object>() {
					@Override
					public void onNext(Object result) {
						isGetStar = true;
						isStar = true;
						showStar();
					}

					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable error) {
						isGetStar = true;
						isStar = false;
						showStar();
					}
				});
	}

	private void forkRepo(){
		final Snackbar snackbar = Snackbar.make(getSwipeRefreshLayout(), "Forking ...", Snackbar.LENGTH_INDEFINITE);
		snackbar.show();
		getApiService().forkRepo(repos.getOwner().getLogin(), repos.getName())
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Object>() {
					@Override
					public void onNext(Object result) {
						snackbar.dismiss();
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
	
	private void starRepo(){
		final Snackbar snackbar = Snackbar.make(getSwipeRefreshLayout(), "Staring ...", Snackbar.LENGTH_INDEFINITE);
		snackbar.show();
        getApiService().starRepo(repos.getOwner().getLogin(), repos.getName())
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Object>() {
					@Override
					public void onNext(Object result) {
						snackbar.dismiss();
						isStar = true;
						showStar();
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
	
	private void unStarRepo(){
		final Snackbar snackbar = Snackbar.make(getSwipeRefreshLayout(), "UnStaring ...", Snackbar.LENGTH_INDEFINITE);
		snackbar.show();
        getApiService().unstarRepo(repos.getOwner().getLogin(), repos.getName())
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Object>() {
					@Override
					public void onNext(Object result) {
						snackbar.dismiss();
						isStar = false;
						showStar();
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
}
