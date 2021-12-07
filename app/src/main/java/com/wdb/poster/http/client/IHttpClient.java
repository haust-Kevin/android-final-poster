package com.wdb.poster.http.client;

import java.util.Map;

import okhttp3.Callback;

public interface IHttpClient {

    void get(String url, Map<String, String> headers, Callback callback);

    void post(String url, String body, Map<String, String> headers, Callback callback);

    void cancelAllRequest();
}
