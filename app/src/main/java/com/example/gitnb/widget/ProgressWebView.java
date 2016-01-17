package com.example.gitnb.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.webkit.SslErrorHandler;
import com.example.gitnb.R;

@SuppressWarnings("deprecation")
public class ProgressWebView extends WebView {

	public interface UrlLoadingListener {
        void loading(String url);
    }
	
    private ProgressBar progressbar;
    private UrlLoadingListener urlLoadingListener;

	public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        progressbar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressbar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_color));
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 5, 0, 0));
        addView(progressbar);
		setWebViewClient(new WebViewClient(){  
            @Override  
            public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);  
                    //页面下载完毕,却不代表页面渲染完毕显示出来  
                    //WebChromeClient中progress==100时也是一样  
                    if (getContentHeight() != 0) {  
                    //这个时候网页才显示  
                    }  
            }  
            
            //https://github.com/login/oauth/authorize/?client_id=a4220ecd856ed8c01689&state=2015&redirect_uri=https://github.com/Jeffmen/GitNB&scope=user,public_repo
            //https://github.com/Jeffmen/GitNB?code=4b665f4f23462c2b91eb&state=2015
            @Override  
            public boolean shouldOverrideUrlLoading(WebView view, String url) { 
                    //自身加载新链接,不做外部跳转  
                    view.loadUrl(url); 
                    if(urlLoadingListener != null)
                    	urlLoadingListener.loading(url);
                    return true;  
            }  

            //fix for webview can not open the https
            @Override  
            public void onReceivedSslError(WebView view, SslErrorHandler handler, android.net.http.SslError error) {
            	handler.proceed();
            }         
		});  
		
		setWebChromeClient(new WebChromeClient(){  
		    @Override  
		    public void onProgressChanged(WebView view, int newProgress) {
	            super.onProgressChanged(view, newProgress);  
	            if (newProgress == 100) {
	                progressbar.setProgress(newProgress);
	                progressbar.postDelayed(new Runnable(){

						@Override
						public void run() {
			                progressbar.setVisibility(GONE);
						}
	                	
	                }, 200);
	            } else {
	                if (progressbar.getVisibility() == GONE)
	                    progressbar.setVisibility(VISIBLE);
	                progressbar.setProgress(newProgress);
	            }
		    }  
		}); 
    }

	public void setUrlLoadingListener(final UrlLoadingListener listener){
		urlLoadingListener = listener;
	}

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }
}
