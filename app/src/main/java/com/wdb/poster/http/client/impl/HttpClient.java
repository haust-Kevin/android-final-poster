package com.wdb.poster.http.client.impl;


import android.util.Log;

import com.wdb.poster.http.client.IHttpClient;
import com.wdb.poster.http.cookie.PersistentCookieJar;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpClient implements IHttpClient {

    private final String TAG = "HttpClient";
    private final static int TIMEOUT_SECONDS = 5;

    private OkHttpClient client;
    private boolean keepAlive;

    public HttpClient() {
        client = new OkHttpClient();
        keepAlive = false;
    }

    private OkHttpClient buildClient(boolean singleSession) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()/*.connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))*/;
        if (singleSession) builder.cookieJar(new PersistentCookieJar());
        return builder.build();
    }


    public void setSingleSession(boolean keepAlive) {
        if (keepAlive) {
            if (!this.keepAlive) {
                client = buildClient(true);
            }
        } else {
            client = buildClient(false);
        }
        this.keepAlive = keepAlive;
    }

    @Override
    public void get(String url, Map<String, String> headers, Callback callback) {
        System.out.println("GET " + url);
        System.out.println("headers=" + headers);
        Request.Builder builder = new Request.Builder().url(url);
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                builder.addHeader(header.getKey(), header.getValue());
            }
        }
        Request request = builder.build();
        execute(request, callback);
    }

    @Override
    public void post(String url, String jsonBody, Map<String, String> headers, Callback callback) {
        System.out.println("POST " + url);
        System.out.println("jsonBody=" + jsonBody);
        System.out.println("headers=" + headers);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.create(null, "");
        if (jsonBody != null)
            requestBody = RequestBody.create(JSON, jsonBody);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(requestBody);
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                builder.addHeader(header.getKey(), header.getValue());
            }
        }

        Request request = builder.build();
        execute(request, callback);
    }

    private void execute(Request request, Callback callback) {
        Call call = client.newCall(request);
        if (callback != null) {
            call.enqueue(callback);
        } else {
            new Thread(() -> {
                try {
                    call.execute();
                } catch (IOException e) {
                    Log.e(TAG, "request error: " + e.getMessage());
                }
            }).start();
        }
    }

    @Override
    public void cancelAllRequest() {
        client.dispatcher().cancelAll();
    }
}
