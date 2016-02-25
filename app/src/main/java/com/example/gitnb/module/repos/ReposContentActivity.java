package com.example.gitnb.module.repos;

import com.example.gitnb.R;
import com.example.gitnb.api.OKHttpClient;
import com.example.gitnb.api.RetrofitNetworkAbs;
import com.example.gitnb.app.BaseSwipeActivity;
import com.example.gitnb.model.Content;
import com.example.gitnb.utils.MessageUtils;
import com.example.gitnb.widget.ProgressWebView;
import com.kyleduo.switchbutton.SwitchButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

public class ReposContentActivity extends BaseSwipeActivity {
    private SwitchButton switchBt;
	private TextView text_content;
	private ProgressWebView web_content;
    private Content content;
    private String content_url;
    private boolean isShowInWeb = true;
    
    protected void setTitle(TextView view){
        if(content == null || content.name.isEmpty()){
        	view.setText("Read me");
        }else{
        	view.setText(content.name);
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        content_url = intent.getStringExtra(ReposDetailActivity.CONTENT_URL);
        content = intent.getParcelableExtra(ReposContentsListActivity.CONTENT);
        setContentView(R.layout.activity_repo_content);
        text_content = (TextView) findViewById(R.id.text_content);
        web_content = (ProgressWebView) findViewById(R.id.web_content);

        switchBt = (SwitchButton) findViewById(R.id.switch_bt);
        switchBt.setChecked(this.isShowInWeb);
        switchBt.setVisibility(View.VISIBLE);
        switchBt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        
           @Override 
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        	   isShowInWeb = isChecked;
        	   updateContent();  
           }
        
        });
    }

    @Override
    protected void startRefresh(){
    	super.startRefresh();
        if(content_url != null){
            requestContents(content_url+"/readme");
        } 
        if(content != null){
            requestContents(content.url);
        }
    }

    @Override
    protected void endRefresh(){
    	super.endRefresh();
    	updateContent();
    }

    @Override
    protected void endError(){
    	super.endError();
    }
    
    
    private void updateContent(){
    	if(isShowInWeb){
    		text_content.setVisibility(View.GONE);
    		web_content.setVisibility(View.VISIBLE);
            web_content.getSettings().setJavaScriptEnabled(true);
            web_content.loadUrl(content.html_url);
    	}
    	else{
    		text_content.setVisibility(View.VISIBLE);
    		web_content.setVisibility(View.GONE);
    		text_content.setText(new String(Base64.decode(content.content, Base64.DEFAULT)));
    	}
    }
    
    private void requestContents(final String url){
    	OKHttpClient.getNewInstance().setNetworkListener(new RetrofitNetworkAbs.NetworkListener<Content>() {

			@Override
			public void onOK(Content ts) {
				content = ts;
                getRefreshandler().sendEmptyMessage(END_UPDATE);
			}

			@Override
			public void onError(String Message) {
				MessageUtils.showErrorMessage(ReposContentActivity.this, Message);
                getRefreshandler().sendEmptyMessage(END_ERROR);
			}
			
    	}).request(url, Content.class);
    }
}
