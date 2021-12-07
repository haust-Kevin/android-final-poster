package com.wdb.poster.entity;

import java.io.Serializable;

public class ResponseWrapper implements Serializable {

    public ResponseWrapper() {
        responseRaw = "";
        requestRaw = "";
        body = "";
        code = -1;
        message = "";
    }

    public String responseRaw;
    public String requestRaw;
    public String body;
    public int code;
    public String message;

}
