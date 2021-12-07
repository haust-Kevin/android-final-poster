package com.wdb.poster.db.api.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;


@Entity(foreignKeys = {@ForeignKey(entity = Connection.class, parentColumns = "connId", childColumns = "connId", onDelete = CASCADE)})
public class Request {

    @PrimaryKey(autoGenerate = true)
    private int reqId;

    private int connId;

    private String reqMethod;

    private String reqName;

    private String reqPath;

    private String reqParams;

    private String reqBody;

    private String reqHeaders;

    public int getReqId() {
        return reqId;
    }

    public void setReqId(int reqId) {
        this.reqId = reqId;
    }

    public int getConnId() {
        return connId;
    }

    public void setConnId(int connId) {
        this.connId = connId;
    }

    public String getReqMethod() {
        return reqMethod;
    }

    public void setReqMethod(String reqMethod) {
        this.reqMethod = reqMethod;
    }

    public String getReqName() {
        return reqName;
    }

    public void setReqName(String reqName) {
        this.reqName = reqName;
    }

    public String getReqPath() {
        return reqPath;
    }

    public void setReqPath(String reqPath) {
        this.reqPath = reqPath;
    }

    public String getReqParams() {
        return reqParams;
    }

    public void setReqParams(String reqParams) {
        this.reqParams = reqParams;
    }

    public String getReqBody() {
        return reqBody;
    }

    public void setReqBody(String reqBody) {
        this.reqBody = reqBody;
    }

    public String getReqHeaders() {
        return reqHeaders;
    }

    public void setReqHeaders(String reqHeaders) {
        this.reqHeaders = reqHeaders;
    }

    @Override
    public String toString() {
        return "Request{" +
                "reqId=" + reqId +
                ", connId=" + connId +
                ", reqMethod='" + reqMethod + '\'' +
                ", reqName='" + reqName + '\'' +
                ", reqPath='" + reqPath + '\'' +
                ", reqParams='" + reqParams + '\'' +
                ", reqBody='" + reqBody + '\'' +
                ", reqHeaders='" + reqHeaders + '\'' +
                '}';
    }
}
