package com.example.gitnb.utils;

import com.example.gitnb.R;
import com.example.gitnb.app.Application;
import com.example.gitnb.app.BaseSwipeActivity;
import com.example.gitnb.module.GitHubAuthorizeActivity;
import com.joanzapata.iconify.widget.IconButton;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Looper;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;


/**
 * Created by yw on 2015/5/5.
 */
public class MessageUtils {
    private static final String TOAST_TYPEFACE = "sans-serif-condensed";

    public static void showErrorMessage(Context cxt, String errorString) {
        if(cxt != null) {
            if (errorString.equalsIgnoreCase("Requires authentication")) {
                Intent intent = new Intent(cxt, GitHubAuthorizeActivity.class);
                cxt.startActivity(intent);
            } else if (errorString.equalsIgnoreCase("Unsatisfiable Request (only-if-cached)")) {
                Toast.makeText(cxt, "No Network...", Toast.LENGTH_LONG).show();
            } else {
                if (cxt instanceof BaseSwipeActivity &&
                        ((BaseSwipeActivity)cxt).getSwipeRefreshLayout() != null) {
                    Snackbar.make(((BaseSwipeActivity)cxt).getSwipeRefreshLayout(), errorString, Snackbar.LENGTH_SHORT).show();
                }
                else{
                    if(Looper.myLooper() == null) {
                        Looper.prepare();
                    }
                    Toast.makeText(cxt, errorString, Toast.LENGTH_LONG).show();

                }
            }
        }
    }

    public static @CheckResult
    Toast getToast(@NonNull Context context, @NonNull String message) {
        final Toast currentToast = new Toast(context);
        final View toastLayout = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.toast_layout, null);
        final IconButton iconButton = (IconButton) toastLayout.findViewById(R.id.toast);

        if (message.contains("Unsatisfiable Request (only-if-cached)")) {
            message = "No Network...";
        }

        int tintColor;
        if(context instanceof BaseSwipeActivity){
            tintColor = ((BaseSwipeActivity)context).color;
        }
        else{
            tintColor = context.getResources().getColor(R.color.orange_yellow);
        }

        iconButton.setText("{fe-message 22sp}    "+message);
        iconButton.getBackground().setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
        iconButton.setTypeface(Typeface.create(TOAST_TYPEFACE, Typeface.NORMAL));

        currentToast.setView(toastLayout);
        currentToast.setDuration(Toast.LENGTH_SHORT);
        return currentToast;
    }

    public static void showMiddleToast(Context cxt, String msg) {
        if(cxt == null)
            cxt = Application.getContext();
        Toast toast = Toast.makeText(cxt, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
