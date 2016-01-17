package com.example.gitnb.api;

import java.io.IOException;

import android.os.Handler;
import android.os.Looper;

import com.example.gitnb.api.RetrofitNetworkAbs.NetworkListener;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;


public class OKHttpClient{
	private static String TAG = "OKHttpClient";
    protected NetworkListener networkListener;
    private Gson gson = new Gson();
    private Handler mDelivery = new Handler(Looper.getMainLooper());
	private OKHttpClient(){
	}
	
    public static OKHttpClient getNewInstance() {
        return new OKHttpClient();
    }	
	
    public <T> void request(String url, final Class<T> className){
    	final Request request = new Request.Builder().url(url).build();
        ApiRetrofit.getRetrofit().client().newCall(request)
    	.enqueue(new Callback() {

			@Override
			public void onFailure(Request request, final IOException exception) {
		        if (networkListener != null) {
	        		mDelivery.post(new Runnable() {
	        			@Override
	        			public void run() {
	    		            networkListener.onError(exception.getMessage());
	        			}
	        		});
		        }
			}

			@Override
			public void onResponse(com.squareup.okhttp.Response response)
					throws IOException {
		        if (response.isSuccessful()) {
		            if (networkListener != null) {	
		            	String reponse = response.body().string();
		            	final Object o = gson.fromJson(reponse, className);
		        		mDelivery.post(new Runnable() {
		        			@Override
		        			public void run() {
				                networkListener.onOK(o);
		        			}
		        		});
		            }
		        } else {
		            final String mess = response.message();
		            if (networkListener != null) {	
		                mDelivery.post(new Runnable() {
		        			@Override
		        			public void run() {
				                networkListener.onError(mess);	
		        			}
		        		});
		            }
		        }
			}
		});
	}
    
	public OKHttpClient setNetworkListener(NetworkListener networkListener) {
        this.networkListener = networkListener;
        return this;
	}
}
