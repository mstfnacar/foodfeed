package com.pem.mustafa.servertest.CustomObjects;

/**
 * Created by mustafa on 13.05.2016.
 */

public class TopTenDataObj {

    private String username;
    private String rank;
    private String title;
    private boolean hasPhoto;
    private String photoUrl;


    public TopTenDataObj (String username, String rank, String title, boolean hasPhoto, String photoUrl){

        this.username = username;
        this.rank = rank;
        this.title = title;
        this.hasPhoto = hasPhoto;
        this.photoUrl = photoUrl;

    }

    public String getName() {
        return username;
    }

    public String getTitle() {
        return title;
    }

    public String getRank() {
        return rank;
    }

    public boolean isHasPhoto() {
        return hasPhoto;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
