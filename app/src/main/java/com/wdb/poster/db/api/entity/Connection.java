package com.wdb.poster.db.api.entity;


import androidx.room.Entity;
import androidx.room.PrimaryKey;



@Entity
public class Connection {

    @PrimaryKey(autoGenerate = true)
    private int connId;

    private String connName;

    private String defaultWebsite;

    private boolean singleSession;

    public int getConnId() {
        return connId;
    }

    public void setConnId(int connId) {
        this.connId = connId;
    }

    public String getConnName() {
        return connName;
    }

    public void setConnName(String connName) {
        this.connName = connName;
    }

    public String getDefaultWebsite() {
        return defaultWebsite;
    }

    public void setDefaultWebsite(String defaultWebsite) {
        this.defaultWebsite = defaultWebsite;
    }

    public boolean isSingleSession() {
        return singleSession;
    }

    public void setSingleSession(boolean singleSession) {
        this.singleSession = singleSession;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "connId=" + connId +
                ", connName='" + connName + '\'' +
                ", defaultWebsite='" + defaultWebsite + '\'' +
                ", singleSession=" + singleSession +
                '}';
    }
}
