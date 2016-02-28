package com.example.gitnb.module.user;

import java.text.SimpleDateFormat;

import com.example.gitnb.R;
import com.example.gitnb.api.RetrofitNetworkAbs;
import com.example.gitnb.api.UsersClient;
import com.example.gitnb.app.BaseSwipeActivity;
import com.example.gitnb.model.Organization;
import com.example.gitnb.module.repos.EventListActivity;
import com.example.gitnb.module.repos.ReposListActivity;
import com.example.gitnb.utils.MessageUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

public class OrganizationDetailActivity extends BaseSwipeActivity{

	private String TAG = "OrganizationDetailActivity";
	public static String ORGS = "orgs";
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private LinearLayout main;
	private Organization orgs;
    private Switch swithBt;
	
    protected void setTitle(TextView view){
        if(orgs != null && !orgs.login.isEmpty()){
        	view.setText(orgs.login);
        }else{
        	view.setText("NULL");
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        orgs = (Organization) intent.getParcelableExtra(ORGS);
        setContentView(R.layout.activity_organization_detail);
        main = (LinearLayout) findViewById(R.id.main);
        main.setVisibility(View.GONE);

        swithBt = (Switch) findViewById(R.id.switch_bt);  
        swithBt.setVisibility(View.GONE);
    }
    
    private void setOrganization(){
    	TextView repos_description = (TextView) findViewById(R.id.repos_description);
    	TextView orga_name = (TextView) findViewById(R.id.orga_name);
    	SimpleDraweeView orga_avatar = (SimpleDraweeView) findViewById(R.id.orga_avatar);
    	
		if(orgs != null){
			orga_name.setText(orgs.name);
			repos_description.setText(orgs.description);
			orga_avatar.setImageURI(Uri.parse(orgs.avatar_url));
		}
		
    	TextView members = (TextView) findViewById(R.id.members);
    	TextView events = (TextView) findViewById(R.id.events);
    	TextView repositories = (TextView) findViewById(R.id.repositories);
    	members.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(OrganizationDetailActivity.this, UserListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable(ORGS, orgs);
				intent.putExtras(bundle);
				intent.putExtra(UserListActivity.USER_TYPE, UserListActivity.USER_TYPE_MEMBER);
				startActivity(intent);
				
			}
		});
    	repositories.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(OrganizationDetailActivity.this, ReposListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable(ORGS, orgs);
				intent.putExtras(bundle);
				intent.putExtra(ReposListActivity.REPOS_TYPE, ReposListActivity.REPOS_TYPE_ORGS);
				startActivity(intent);
			}
		});
    	events.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(OrganizationDetailActivity.this, EventListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable(ORGS, orgs);
				intent.putExtras(bundle);
				intent.putExtra(EventListActivity.EVENT_TYPE, EventListActivity.EVENT_TYPE_ORGS);
				startActivity(intent);
			}
		});
    }
    
    @Override
    protected void startRefresh(){
    	super.startRefresh();
    	getOrganizationInfo();
    }

    @Override
    protected void endRefresh(){
    	super.endRefresh();
        main.setVisibility(View.VISIBLE);
        setOrganization();
    }

    @Override
    protected void endError(){
    	super.endError();
    }
    
    private void getOrganizationInfo(){
    	UsersClient.getNewInstance().setNetworkListener(new RetrofitNetworkAbs.NetworkListener<Organization>() {

			@Override
			public void onOK(Organization ts) {
				orgs = ts;
				getRefreshHandler().sendEmptyMessage(END_UPDATE);
			}

			@Override
			public void onError(String Message) {
				MessageUtils.showErrorMessage(OrganizationDetailActivity.this, Message);
				getRefreshHandler().sendEmptyMessage(END_ERROR);
			}
			
    	}).orgs(orgs.login);
    }
}
