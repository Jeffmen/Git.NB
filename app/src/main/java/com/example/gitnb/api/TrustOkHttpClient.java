package com.example.gitnb.api;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

/**
 * Created by Rain on 16/3/9.
 */
public class TrustOkHttpClient extends OkHttpClient {
    static final int CONNECT_TIMEOUT_MILLIS = 30 * 1000;
    static final int READ_TIMEOUT_MILLIS = 30 * 1000;

    public TrustOkHttpClient(Interceptor interceptor){
        this.setConnectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        this.setReadTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        this.interceptors().add(interceptor);
        //ignore https certificate validation
        SSLContext context = null;
        TrustManager[] trustManagers = new TrustManager[] { new FakeX509TrustManager() };
        try {
            context = SSLContext.getInstance("TLS");
            context.init(null, trustManagers, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        this.setSslSocketFactory(context.getSocketFactory());
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        this.setHostnameVerifier(new HostnameVerifier() {

            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        });
    }
}
