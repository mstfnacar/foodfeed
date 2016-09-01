package com.pem.mustafa.servertest.CustomObjects;

/**
 * Created by mustafa on 15.06.2016.
 */
public class FollowObj {

    private String username;
    private String photourl;

    public FollowObj(String username, String photourl) {
        this.username = username;
        this.photourl = photourl;
    }

    public String getUsername() {
        return username;
    }

    public String getPhotourl() {
        return photourl;
    }
}
