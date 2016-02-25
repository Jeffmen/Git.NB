package com.example.gitnb.utils;

import com.example.gitnb.app.Application;
import com.example.gitnb.app.BaseSwipeActivity;
import com.example.gitnb.module.GitHubAuthorizeActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.widget.Toast;


/**
 * Created by yw on 2015/5/5.
 */
public class MessageUtils {

    public static void showErrorMessage(Context cxt, String errorString) {
        if(cxt != null) {
            if (errorString == "Requires authentication") {
                Intent intent = new Intent(cxt, GitHubAuthorizeActivity.class);
                cxt.startActivity(intent);
            } else {
                if (cxt instanceof BaseSwipeActivity) {
                    Snackbar.make(((BaseSwipeActivity)cxt).getSwipeRefreshLayout(), errorString, Snackbar.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(cxt, errorString, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public static void showMiddleToast(Context cxt, String msg) {
        if(cxt == null)
            cxt = Application.getContext();
        Toast toast = Toast.makeText(cxt, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
