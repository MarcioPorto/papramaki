package com.papramaki.papramaki.models;

/**
 * Created by paulchery on 4/21/16.
 */


/**
 * Defines the user model.
 * The user has a uid (email), user_id from the API, and two other identifying strings:
 * the client and accessTokens. These string are used to make http requests.
 */
public class User {

    private String uid;
    private int user_id;
    private String client;
    private String accessToken;


    public User(String uid, String client, String accessToken, int user_id){
        this.user_id = user_id;
        this.uid = uid;
        this.client = client;
        this.accessToken = accessToken;

    }


    public User(){}


    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
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
