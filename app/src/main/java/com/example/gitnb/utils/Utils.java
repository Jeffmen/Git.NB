package com.example.gitnb.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;

public class Utils {
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	public static int dpToPx(Context context,int dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }
	
	public static Date getDate(String date) {
		try {
			return format.parse(date);
		} catch (ParseException e) {
	        return new Date();
		}
    }
	
	public static int fromNow(String date){
		Date from = getDate(date);
        Calendar cal = Calendar.getInstance();  
        cal.setTime(from);  
        long time1 = cal.getTimeInMillis();               
        cal.setTime(new Date());  
        long time2 = cal.getTimeInMillis();       
        long between_days=(time2-time1)/(1000*60);
          
       return Integer.parseInt(String.valueOf(between_days)); 
	}
	
    public static void setRefreshing(final SwipeRefreshLayout swipeRefreshLayout, final boolean isRefreshing)
    {
    	if(swipeRefreshLayout != null){
	        swipeRefreshLayout.post(new Runnable()
	        {
	            @Override
	            public void run()
	            {
	                swipeRefreshLayout.setRefreshing(isRefreshing);
	            }
	        });
    	}
    }
}
