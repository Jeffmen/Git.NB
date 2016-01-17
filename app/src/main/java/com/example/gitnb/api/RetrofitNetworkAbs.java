package com.example.gitnb.api;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;


public abstract class RetrofitNetworkAbs {
    protected final String TAG = this.getClass().getSimpleName();
    protected NetworkListener networkListener;

    @SuppressWarnings("unchecked")
    protected <T extends RetrofitNetworkAbs> T setNetworkListener(NetworkListener networkListener, T type) {
        type.networkListener = networkListener;
        return type;
    }

    public abstract <T extends RetrofitNetworkAbs> T setNetworkListener(NetworkListener networkListener);
	
    @SuppressWarnings("unchecked")
	protected void execute(Call call){
		call.enqueue(new Callback() {

            @Override
            public void onFailure(Throwable t) {
                myOnFailure(t);
            }

			@Override
			public void onResponse(retrofit.Response response, Retrofit retrofit) {
                myOnResponse(response);
			}
        });
	}
	
    @SuppressWarnings("unchecked")
	protected boolean myOnResponse(retrofit.Response<? extends Object> response) {
        if (response.isSuccess()) {
            if (networkListener != null) {
                networkListener.onOK(response.body());
            }
            return true;
        } else {
            String mess = response.message();
            if (networkListener != null) {
                networkListener.onError(mess);
            }
            return false;
        }
    }

    /**
     * OnFailure
     */
    protected void myOnFailure(Throwable t) {
        if (networkListener != null) {
            networkListener.onError(t.getMessage());
        }
    }

    public interface NetworkListener<T extends Object> {
        void onOK(T ts);

        void onError(String Message);
    }

}
