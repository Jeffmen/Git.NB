package com.example.gitnb.api;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;

import com.example.gitnb.app.BaseFragment;
import com.example.gitnb.module.GitHubAuthorizeActivity;
import com.example.gitnb.module.WelcomeActivity;
import com.example.gitnb.utils.CurrentUser;

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
            if(mess.equals("Unauthorized") && networkListener instanceof BaseFragment){
            	Activity context = ((BaseFragment)networkListener).getActivity();
				CurrentUser.detete(context);
                sureToAuthorize(context);
                networkListener.onError("Please refresh again");
            }
            else if (networkListener != null) {
                networkListener.onError(mess);
            }
            return false;
        }
    }

    private void sureToAuthorize(final Activity context){
		Dialog dialog = new AlertDialog.Builder(context).setTitle("Caution")
				.setMessage("The GitHup request is unauthorized. Please authorizing again?")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
						Intent intent = new Intent(context, GitHubAuthorizeActivity.class);
						context.startActivity(intent);
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						context.finish();
						Intent intent = new Intent(context, WelcomeActivity.class);
						context.startActivity(intent);
					}
				})
				.setCancelable(false).create();
		dialog.show();
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
