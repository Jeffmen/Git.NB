package com.example.gitnb.module.search;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.PathInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import com.example.gitnb.R;
import com.example.gitnb.app.BaseFragment;
import com.example.gitnb.app.BaseSwipeActivity;
import com.example.gitnb.module.LanguageActivity;
import com.example.gitnb.module.LanguageAdapter;
import com.example.gitnb.module.MainActivity;
import com.example.gitnb.module.MainFragment;
import com.example.gitnb.module.custom.CustomLayoutAnimationController;
import com.example.gitnb.module.custom.processor.BlurPostprocessor;
import com.example.gitnb.utils.CurrentUser;
import com.example.gitnb.utils.Utils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

public class SearchActivity extends BaseSwipeActivity {
    private static int FOR_LANGUAGE = 200;
    private static int COLUMN_NUM = 4;
    private static int ITEM_SPACE = 20;
    private int item_space;
    private Map<String,String> mFragmentNameByType = new HashMap<>();
    private Map<String,BaseFragment> mBaseFragmentByName= new HashMap<>();
    private Spinner searchTypeSpinner;
    private RecyclerView recyclerView;
    private LinearLayout languageLL;
    private PopupWindow popupWindow;
    private LanguageAdapter adapter;
    private TextView languageText;
    private BaseFragment current;
    private View clear;
    private EditText searchText;
    private String searchType;
    private Toolbar toolbar;
    protected int page = 1;
    private String key, language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        item_space = Utils.dpToPx(this, ITEM_SPACE);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        searchText = (EditText)toolbar.findViewById(R.id.edit_text);
        searchTypeSpinner = (Spinner) toolbar.findViewById(R.id.searchType);
        languageLL = (LinearLayout) findViewById(R.id.languageLL);
        languageText = (TextView) findViewById(R.id.language);
        clear = findViewById(R.id.clear_button);
        language = "all";
        languageText.setText("All");

        SimpleDraweeView titleImage = (SimpleDraweeView)findViewById(R.id.user_background);
        Uri uri = Uri.parse("res://" + getPackageName() + "/" + R.drawable.title_bg_autumn);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setPostprocessor(new BlurPostprocessor(SearchActivity.this))
                .build();
        PipelineDraweeController controller = (PipelineDraweeController)
                Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setOldController(titleImage.getController())
                        .build();
        titleImage.setController(controller);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText.setText("");
            }
        });
        languageLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguagePop(v);
            }
        });
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_NEXT
                        || actionId == EditorInfo.IME_ACTION_GO
                        || actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (searchText.getText().length() > 0) {
                        key = searchText.getText().toString();
                    } else {
                        key = null;
                    }
                    if (current instanceof MainFragment.UpdateListener) {
                        ((MainFragment.UpdateListener) current).update();
                    }
                    hideSoftInput();
                }
                return false;
            }
        });
        searchTypeSpinner.setSelection(1);
        searchTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
//                ((TextView) view).setTextColor(getResources().getColor(R.color.orange_yellow));
//                ((TextView) view).setGravity(Gravity.CENTER);
//                ((TextView) view).setTextSize(18);
                String[] searchTypes = getResources().getStringArray(R.array.search_type);
                searchType = searchTypes[pos];
                if(searchText.getText().length() > 0) {
                    hideSoftInput();
                }
                loadFragment();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        initFragmentMap();
        getSupportActionBar().setElevation(0);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        );
        languageLL.post(new Runnable() {
            @Override
            public void run() {
                noticeAni();
            }
        });
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FOR_LANGUAGE && resultCode == RESULT_OK) {
            language = data.getStringExtra(LanguageActivity.LANGUAGE_KEY);
            languageText.setText(language);
            if(current instanceof MainFragment.UpdateListener){
                ((MainFragment.UpdateListener)current).update();
            }
        }
    }

    private void showLanguagePop(View view){
        hideSoftInput();
        languageLL.clearAnimation();
        View contentView = LayoutInflater.from(this).inflate(R.layout.language_popup_layout, null, false);
        initPopRecyclerView(contentView);
        popupWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        // 设置PopupWindow以外部分的背景颜色  有一种变暗的效果
        final WindowManager.LayoutParams wlBackground = getWindow().getAttributes();
        wlBackground.alpha = 0.75f;
        getWindow().setAttributes(wlBackground);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                wlBackground.alpha = 1.0f;
                getWindow().setAttributes(wlBackground);
                languageAnim(true);
            }
        });
        //popupWindow.setAnimationStyle(R.style.anim_popup_bottombar);
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        languageAnim(false);
    }

    private void hideSoftInput(){
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void initPopRecyclerView(View contentView){
        recyclerView = (RecyclerView) contentView.findViewById(R.id.recyclerView);
        adapter = new LanguageAdapter(this);
        adapter.SetOnItemClickListener(new LanguageAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                popupWindow.dismiss();
                languageAnim(true);
                language = adapter.getItemValue(position);
                if(TextUtils.isEmpty(language)){
                    languageText.setText("All");
                }
                else {
                    languageText.setText(adapter.getItemName(position));
                }
                if(current instanceof MainFragment.UpdateListener){
                    ((MainFragment.UpdateListener)current).update();
                }
            }
        });
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(COLUMN_NUM, item_space, true));
        recyclerView.setLayoutManager(new GridLayoutManager(this, COLUMN_NUM));
        recyclerView.setAdapter(adapter);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
        CustomLayoutAnimationController layoutAnimationController =
                new CustomLayoutAnimationController(animation, 0.15f, 0.15f);
        layoutAnimationController.setInterpolator(new AccelerateInterpolator());
        layoutAnimationController.setOrder(CustomLayoutAnimationController.ORDER_CUSTOM);
        recyclerView.setLayoutAnimation(layoutAnimationController);
        recyclerView.startLayoutAnimation();
    }

    public void noticeAni(){
        ObjectAnimator startYTranslate = ObjectAnimator.ofFloat(languageLL, "translationX", languageLL.getWidth(),0);
        startYTranslate.setDuration(2000);
        startYTranslate.setInterpolator(new BounceInterpolator());
        startYTranslate.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                languageLL.setVisibility(View.VISIBLE);
            }
        });
        startYTranslate.start();

        ObjectAnimator bounceTranslate = ObjectAnimator.ofFloat(languageLL, "translationX",0, languageLL.getWidth()*0.3f, 0);
        bounceTranslate.setDuration(1200);
        bounceTranslate.setRepeatCount(-1);
    }

    private void languageAnim(final boolean isVisible){
        int startX = 0, endX = 0;
        if(isVisible){
            startX = languageLL.getWidth();
        }
        else{
            startX = languageLL.getScrollX();
            endX = languageLL.getWidth();
        }
        Animation translateAnimation=new TranslateAnimation(startX,endX,0,0);
        translateAnimation.setDuration(300);
        translateAnimation.setFillAfter(true);
        languageLL.startAnimation(translateAnimation);
    }

    public String getSearchText(){
        return key;
    }

    public String getLanguageText(){
        return language.replace("all", "");
    }

    private void initFragmentMap() {
        mFragmentNameByType.put("User", HotUserFragment.class.getName());
        mFragmentNameByType.put("Repos", HotReposFragment.class.getName());
    }

    private BaseFragment getFragment(String fragmentName) {
        BaseFragment baseFragment = mBaseFragmentByName.get(fragmentName);
        if(baseFragment==null) {
            try {
                baseFragment=(BaseFragment)Class.forName(fragmentName).newInstance();
            } catch (Exception e) {
                baseFragment=null;
            }
            mBaseFragmentByName.put(fragmentName,baseFragment);
        }
        return baseFragment;
    }

    private void loadFragment(){
        transactionSupportFragment(current, getFragment(mFragmentNameByType.get(searchType)));
    }

    private void transactionSupportFragment(BaseFragment from, BaseFragment to) {
        if(from == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.main_content, to).commit();
        }
        else {
            if (from != to) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        android.R.anim.fade_in, android.R.anim.fade_out);
                if (!to.isAdded()) {
                    transaction.hide(from).add(R.id.main_content, to).commit();
                } else {
                    transaction.hide(from).show(to).commit();
                }
            }
            else{
                to.startRefresh();
            }
        }
        current = to;
    }
}
