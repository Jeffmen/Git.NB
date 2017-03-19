package com.example.gitnb.api.rxjava;

import com.example.gitnb.api.ApiRetrofit;

public class ApiRxJavaClient {
	private ApiRxJavaService searchService;

	private static class ClientHolder{
		private static ApiRxJavaClient client = new ApiRxJavaClient();
	}

	private ApiRxJavaClient(){
	    searchService = ApiRetrofit.getRetrofit().create(ApiRxJavaService.class);
	}
	
    public static ApiRxJavaClient getNewInstance() {
        return ClientHolder.client;
    }

	public ApiRxJavaService getService() {
		return searchService;
	}
}
