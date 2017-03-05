package com.example.gitnb.api.rxjava;

import com.example.gitnb.api.ApiRetrofit;

public class ApiRxJavaClient {
    private static ApiRxJavaClient client;
	private ApiRxJavaService searchService;

	private ApiRxJavaClient(){
	    searchService = ApiRetrofit.getRetrofit().create(ApiRxJavaService.class);
	}
	
    public static ApiRxJavaClient getNewInstance() {
        client = new ApiRxJavaClient();
        return client;
    }

	public ApiRxJavaService getService() {
		return searchService;
	}
}
