package com.example.gitnb.api.rxjava;

import com.example.gitnb.api.ApiRetrofit;

public class GankRxJavaClient {
	private GankRxJavaService gankRxJavaService;

	private static class ClientHolder{
		private static GankRxJavaClient client = new GankRxJavaClient();
	}

	private GankRxJavaClient(){
		gankRxJavaService = ApiRetrofit.getRetrofit().create(GankRxJavaService.class);
	}
	
    public static GankRxJavaClient getNewInstance() {
        return ClientHolder.client;
    }

	public GankRxJavaService getService() {
		return gankRxJavaService;
	}
}
