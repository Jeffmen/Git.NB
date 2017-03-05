package com.example.gitnb.api.client;

import com.example.gitnb.api.ApiRetrofit;
import com.example.gitnb.api.RetrofitNetworkAbs;
import com.example.gitnb.api.service.SearchService;

public class SearchClient extends RetrofitNetworkAbs {
	private SearchService searchService;
	
	private SearchClient(){
	    searchService = ApiRetrofit.getRetrofit().create(SearchService.class);
	}
	
    public static SearchClient getNewInstance() {
        return new SearchClient();
    }
    
	public void users(String query, int page){
		execute(searchService.usersPaginated(query, page));
	}
	
	public void users(String query, String sort, String order, int page){
		execute(searchService.usersPaginated(query, sort, order, page));
	}
	
	public void repos(String query, int page){
		execute(searchService.reposPaginated(query, page));
	}
	
	public void repos(String query, String sort, String order, int page){
		execute(searchService.reposPaginated(query, sort, order, page));
	}
	
	@Override
	public SearchClient setNetworkListener(NetworkListener networkListener) {
        return setNetworkListener(networkListener, this);
	}

}
