package com.example.gitnb.api.client;

import com.example.gitnb.api.ApiRetrofit;
import com.example.gitnb.api.service.RepoActionsService;
import com.example.gitnb.api.RetrofitNetworkAbs;

public class RepoActionsClient extends RetrofitNetworkAbs {

	private RepoActionsService repoActionsService;
	
	private RepoActionsClient(){
		repoActionsService = ApiRetrofit.getRetrofit().create(RepoActionsService.class);
	}
	
    public static RepoActionsClient getNewInstance() {
        return new RepoActionsClient();
    }	
    
    public void checkIfRepoIsStarred(String owner, String repo){
		execute(repoActionsService.checkIfRepoIsStarred(owner, repo));
	}
    
    public void starRepo(String owner, String repo){
		execute(repoActionsService.starRepo(owner, repo));
	}    
    
    public void unStarRepo(String owner, String repo){
		execute(repoActionsService.unstarRepo(owner, repo));
	}
	
	@Override
	public RepoActionsClient setNetworkListener(NetworkListener networkListener) {

        return setNetworkListener(networkListener, this);
	}

}
