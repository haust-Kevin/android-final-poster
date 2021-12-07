package com.wdb.poster.entity;

import com.wdb.poster.constraint.RequestMethod;

import java.util.HashMap;
import java.util.Map;


public class RequestWrapper {

    public RequestMethod method;
    public String url;
    public Map<String, String> headers;
    public String body;

    public RequestWrapper() {
        headers = new HashMap<>();
    }

    @Override
    public String toString() {
        return "RequestWrapper{" +
                "method=" + method +
                ", url='" + url + '\'' +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
