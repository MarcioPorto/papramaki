package com.papramaki.papramaki.models;

/**
 * Created by paulchery on 4/21/16.
 */
public class User {

    private String uid;
    private String client;
    private String accessToken;

    public User(){
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
