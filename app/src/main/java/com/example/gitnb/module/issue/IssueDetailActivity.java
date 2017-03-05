package com.example.gitnb.module.issue;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.gitnb.R;
import com.example.gitnb.app.BaseSwipeActivity;
import com.example.gitnb.model.CommentRequest;
import com.example.gitnb.model.GithubComment;
import com.example.gitnb.model.Issue;
import com.example.gitnb.model.IssueRequest;
import com.example.gitnb.model.IssueState;
import com.example.gitnb.model.Repository;
import com.example.gitnb.module.custom.processor.BlurPostprocessor;
import com.example.gitnb.module.search.HotReposFragment;
import com.example.gitnb.module.search.HotUserFragment;
import com.example.gitnb.module.user.UserDetailActivity;
import com.example.gitnb.utils.CurrentUser;
import com.example.gitnb.utils.Utils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import retrofit.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IssueDetailActivity extends BaseSwipeActivity implements IssueCommentListAdapter.OnItemClickListener{

	private String TAG = IssueDetailActivity.class.getName();
	public static String ISSUE_URL = "issue_url";
	public static String ISSUE = "issue";
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
	private IssueCommentListAdapter adapter;
	private SimpleDraweeView user_background;
	private SimpleDraweeView user_avatar;
    private RecyclerView recyclerView;
	private PopupWindow popupWindow;
	private TextView issue_title;
	private boolean isLoadingMore;
	private Repository repos;
	private String issueUrl;
	private int page = 1;
	private Issue issue;

	private Observer<ArrayList<GithubComment>> observer = new Observer<ArrayList<GithubComment>>() {
		@Override
		public void onNext(ArrayList<GithubComment> result) {
			onOK(result);
		}

		@Override
		public void onCompleted() {
		}

		@Override
		public void onError(Throwable error) {
			endError(error.getMessage());
		}
	};

    protected void setTitle(TextView view){
        if(issue != null && !issue.title.isEmpty()){
        	view.setText(issue.title);
        }else{
        	view.setText("NULL");
        }
		view.setSelected(true);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
		issueUrl = intent.getStringExtra(ISSUE_URL);
		issue = intent.getParcelableExtra(ISSUE);
		repos = intent.getParcelableExtra(HotReposFragment.REPOS);
        setContentView(R.layout.activity_issue_detail);

		recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

		mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
		user_background = (SimpleDraweeView)findViewById(R.id.user_background);
		user_avatar = (SimpleDraweeView)findViewById(R.id.user_avatar);
		issue_title = (TextView)findViewById(R.id.issue_title);
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
				if(issue_title != null)
					issue_title.setAlpha(1 - alpha);
				if(alpha>0.8f){
					mCollapsingToolbarLayout.setTitle(issue.title);
				}
				else{
					mCollapsingToolbarLayout.setTitle("");
				}
            }
        });
	}

	private void initUserBackground(){
		invalidateOptionsMenu();
		issue_title.setText(issue.title);
		user_avatar.setImageURI(Uri.parse(issue.user.getAvatar_url()));
		user_avatar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (issue.user != null) {
					Intent intent = new Intent(IssueDetailActivity.this, UserDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putParcelable(HotUserFragment.USER, issue.user);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});

		ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(issue.user.getAvatar_url()))
				.setPostprocessor(new BlurPostprocessor(IssueDetailActivity.this))
				.build();
		PipelineDraweeController controller = (PipelineDraweeController)
				Fresco.newDraweeControllerBuilder()
						.setImageRequest(request)
						.setOldController(user_background.getController())
						.build();
		user_background.setController(controller);
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
		if(issue != null) {
			if (issue.state.toString().equals("open")) {
				if (issue.user.getId() == CurrentUser.getInstance().getMe().getId()) {
					menu.findItem(R.id.menu_item_edit_issue).setVisible(true);
				}
				if (repos.getOwner().getId() == CurrentUser.getInstance().getMe().getId()
						|| issue.user.getId() == CurrentUser.getInstance().getMe().getId()) {
					menu.findItem(R.id.menu_item_close_issue).setVisible(true);
				}
				menu.findItem(R.id.menu_item_add_comment).setVisible(true);
			}
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_add_comment:
				showAddCommentPop(recyclerView);
				break;
			case R.id.menu_item_edit_issue:
				showEditIssuePop(recyclerView);
				break;
			case R.id.menu_item_close_issue:
				closeIssue();
				break;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onItemClick(View v, final int position) {
		switch (v.getId()) {
			case R.id.operation:
				showOperationPop(recyclerView, position);
				break;
			case R.id.edit:
				showEditCommentPop(recyclerView, position);
				break;
			case R.id.delete:
				showDeletePop(v, position);
				break;
			case R.id.user_avatar:
				if (repos.getOwner() != null) {
					Intent intent = new Intent(IssueDetailActivity.this, UserDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putParcelable(HotUserFragment.USER, adapter.getItem(position).user);
					intent.putExtras(bundle);
					startActivity(intent);
				}
				break;
		}
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
	public void onBackPressed() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}else{
			super.onBackPressed();
		}
	}

	@Override
	public void toolBarColorChange(int color){
		//mCollapsingToolbarLayout.setContentScrimColor(color);
	}

    @Override
    protected void startRefresh(){
    	super.startRefresh();
		if(!TextUtils.isEmpty(issueUrl)){
			getIssueDetail();
		}
		else if(issue != null){
			initUserBackground();
			getIssueComments();
		}
    }

	@Override
	protected void endRefresh(){
		super.endRefresh();
		isLoadingMore = false;
	}

	@Override
	protected void endError(String Message){
		super.endError(Message);
		isLoadingMore = false;
	}

	private void updateAdapter(ArrayList<GithubComment> ts){
		if(adapter == null) {
			adapter = new IssueCommentListAdapter(this, repos, issue);
			adapter.update(ts);
			adapter.setOnItemClickListener(this);
			adapter.setOnLoadMoreClickListener(new IssueCommentListAdapter.OnItemClickListener() {

				@Override
				public void onItemClick(View view, int position) {
					if(isLoadingMore){
						Log.d(TAG,"ignore manually update!");
					} else{
						page++;
						isLoadingMore = true;
						startRefresh();
					}
				}
			});
			ScaleInAnimationAdapter scaleInAdapter = new ScaleInAnimationAdapter(adapter);
			SlideInBottomAnimationAdapter slideInAdapter = new SlideInBottomAnimationAdapter(scaleInAdapter);
			slideInAdapter.setDuration(300);
			slideInAdapter.setInterpolator(new OvershootInterpolator());
			recyclerView.setAdapter(slideInAdapter);
			recyclerView.scheduleLayoutAnimation();
		}
		else{
			if(page == 1){
				adapter.update(ts);
				recyclerView.scrollToPosition(0);
			}
			else{
				isLoadingMore = false;
				adapter.insertAtBack(ts);
			}
		}
	}

	public void onOK(ArrayList<GithubComment> ts) {
		updateAdapter(ts);
		endRefresh();
	}

	private void showDeleteSnackbar(final int position){
		Snackbar snackBar = Snackbar.make(recyclerView, R.string.delete_comment_tips, Snackbar.LENGTH_LONG);
		snackBar.setAction("YES", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteComment(adapter.getItem(position).id, position);
			}
		});
		View view = snackBar.getView();
		view.setBackgroundColor(getResources().getColor(R.color.orange_yellow));
		TextView tv = (TextView) view.findViewById(R.id.snackbar_text);
		tv.setTextColor(Color.WHITE);
		snackBar.setActionTextColor(Color.BLACK);
		final WindowManager.LayoutParams wlBackground = getWindow().getAttributes();
		wlBackground.alpha = 0.75f;
		getWindow().setAttributes(wlBackground);
		snackBar.setCallback(new Snackbar.Callback() {
			@Override
			public void onDismissed(Snackbar snackbar, int event) {
				wlBackground.alpha = 1.0f;
				getWindow().setAttributes(wlBackground);
				super.onDismissed(snackbar, event);
			}
		});
		snackBar.show();
	}

	private void showOperationPop(View parent, final int position){
		View contentView = LayoutInflater.from(this).inflate(R.layout.comment_operation_popup_layout, null, false);
		final TextView edit = (TextView)contentView.findViewById(R.id.edit);
		final TextView delete = (TextView)contentView.findViewById(R.id.delete);

		edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (popupWindow != null && popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
				showEditCommentPop(recyclerView, position);
			}
		});
		delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (popupWindow != null && popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
				showDeleteSnackbar(position);
			}
		});
		showCommentPop(parent, contentView);
	}

	private void showEditIssuePop(View parent){
		View contentView = LayoutInflater.from(this).inflate(R.layout.add_issue_popup_layout, null, false);
		final EditText title = (EditText)contentView.findViewById(R.id.title);
		final EditText comment = (EditText)contentView.findViewById(R.id.comment);
		title.setText(issue.title);
		comment.setText(issue.body);
		TextView send = (TextView) contentView.findViewById(R.id.send);
		send.setText("Edit");
		send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editIssue(title.getText().toString(), comment.getText().toString());
				if (popupWindow != null && popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
			}
		});
		showCommentPop(parent, contentView);
	}

	private void showAddCommentPop(View parent) {
		View contentView = LayoutInflater.from(this).inflate(R.layout.add_comment_popup_layout, null, false);
		final EditText comment = (EditText) contentView.findViewById(R.id.comment);

		TextView send = (TextView) contentView.findViewById(R.id.send);
		send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addComment(comment.getText().toString());
				if (popupWindow != null && popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
			}
		});
		showCommentPop(parent, contentView);
	}

	private void showEditCommentPop(View parent, final int position) {
		View contentView = LayoutInflater.from(this).inflate(R.layout.add_comment_popup_layout, null, false);
		final EditText comment = (EditText) contentView.findViewById(R.id.comment);
		comment.setText(adapter.getItem(position).body);

		TextView send = (TextView) contentView.findViewById(R.id.send);
		send.setText("Edit");
		send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				adapter.getItem(position).body = comment.getText().toString();
				adapter.notifyItemChanged(position);

				editComment(adapter.getItem(position).id, comment.getText().toString());
				if (popupWindow != null && popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
			}
		});
		showCommentPop(parent, contentView);
	}

	private void showCommentPop(View parent, View contentView){
		ImageView back = (ImageView)contentView.findViewById(R.id.cancel);
		if(back != null) {
			back.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (popupWindow != null && popupWindow.isShowing()) {
						popupWindow.dismiss();
					}
				}
			});
		}
		getCommentPop(contentView, LinearLayout.LayoutParams.MATCH_PARENT);
		popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
	}

	private void showDeletePop(View view, final int position){
		View contentView = LayoutInflater.from(this).inflate(R.layout.delete_popup_layout, null, false);
		final TextView deleteButton = (TextView)contentView.findViewById(R.id.delete);
		deleteButton.setText("Delete the comment?");
		deleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteComment(adapter.getItem(position).id, position);
				if (popupWindow != null && popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
			}
		});
		getCommentPop(contentView, LinearLayout.LayoutParams.WRAP_CONTENT);

		int[] location = new  int[2] ;
		//view.getLocationInWindow(location); //获取在当前窗口内的绝对坐标
		view.getLocationOnScreen(location);
		WindowManager wm = this.getWindowManager();
		int height = wm.getDefaultDisplay().getHeight();
		int j = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		contentView.measure(j,j);

		if(height-location[1]-view.getHeight()>contentView.getMeasuredHeight()) {
			//popupWindow.showAsDropDown(view);
			popupWindow.showAtLocation(view,Gravity.NO_GRAVITY,
					location[0]-contentView.getMeasuredWidth(),
					location[1]+view.getHeight());
		}
		else{
			popupWindow.showAtLocation(view,Gravity.NO_GRAVITY,
					location[0]-contentView.getMeasuredWidth(),
					location[1]-contentView.getMeasuredHeight());
		}
	}

	private void getCommentPop(View contentView, int width){
		popupWindow = new PopupWindow(contentView,
				width,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
		final WindowManager.LayoutParams wlBackground = getWindow().getAttributes();
		wlBackground.alpha = 0.75f;
		getWindow().setAttributes(wlBackground);
		popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				wlBackground.alpha = 1.0f;
				getWindow().setAttributes(wlBackground);
			}
		});
		popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
		popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popupWindow.setAnimationStyle(R.style.anim_popup_bottombar);
	}

	private void getIssueDetail(){
		if(issueUrl != null && issueUrl.lastIndexOf("/") > 0) {
			String idStr = issueUrl.substring(issueUrl.lastIndexOf("/")+1);
			getApiService().detailIssue(repos.getOwner().getLogin(), repos.getName(), Integer.parseInt(idStr))
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(new Observer<Issue>() {
						@Override
						public void onNext(Issue result) {
							issue = result;
							initUserBackground();
							getIssueComments();
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
	}

	private void getIssueComments(){
		getApiService().comments(repos.getOwner().getLogin(), repos.getName(), issue.number, page)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer);
	}

	private void editIssue(String title, String body){
		IssueRequest issueRequest = new IssueRequest();
		issueRequest.title = title;
		issueRequest.body = body;
		issue.title = title;
		issue.body = body;
		adapter.notifyItemChanged(0);

		getApiService().editIssue(repos.getOwner().getLogin(), repos.getName(), issue.number, issueRequest)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Issue>() {
					@Override
					public void onNext(Issue result) {
						startRefresh();
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

	private void closeIssue(){
		IssueRequest issueRequest = new IssueRequest();
		issueRequest.state = IssueState.closed;
		getApiService().closeIssue(repos.getOwner().getLogin(), repos.getName(), issue.number, issueRequest)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Issue>() {
					@Override
					public void onNext(Issue result) {
						startRefresh();
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

	private void editComment(String id, String body){
		CommentRequest commentRequest = new CommentRequest(body);
		getApiService().editComment(repos.getOwner().getLogin(), repos.getName(), id, commentRequest)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<GithubComment>() {
					@Override
					public void onNext(GithubComment result) {
						getIssueComments();
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

	private void addComment(String body){
		GithubComment githubComment = new GithubComment();
		githubComment.body = body;
		githubComment.user = CurrentUser.getInstance().getMe();
		githubComment.updated_at = Utils.now();
		adapter.add(githubComment);
        recyclerView.smoothScrollToPosition(adapter.getItemCount());

		getApiService().addComment(repos.getOwner().getLogin(), repos.getName(), issue.number, githubComment)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<GithubComment>() {
					@Override
					public void onNext(GithubComment result) {
						getIssueComments();
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

	private void deleteComment(String id, final int position){
		adapter.delete(position);

		getApiService().deleteComment(repos.getOwner().getLogin(), repos.getName(), id)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Response<GithubComment>>() {
					@Override
					public void onNext(Response<GithubComment> result) {
						getIssueComments();
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
}
