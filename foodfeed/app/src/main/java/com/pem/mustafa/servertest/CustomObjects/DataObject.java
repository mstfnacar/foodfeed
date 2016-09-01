package com.pem.mustafa.servertest.CustomObjects;

/**
 * Created by mustafa on 09.12.2015.
 */
public class DataObject {

    private String foodname;
    private String price;
    private String location;
    private String username;
    private String rating;
    private String imageurl;
    private boolean hasPhoto;
    private boolean userHasPhoto;
    String userImageUrl;
    private int id;

    public DataObject (String foodname, String price, String location, String username, String rating, String url, boolean hasPhoto,int id, boolean userhasphoto, String userimageurl){
        this.foodname = foodname;
        this.price = price;
        this.location = location;
        this.username = username;
        this.rating = rating;
        this.id = id;
        this.imageurl = url;
        this.hasPhoto = hasPhoto;
        this.userHasPhoto = userhasphoto;
        this.userImageUrl = userimageurl;
    }

    public String getFoodname() {
        return foodname;
    }
    public String getPrice() {
        return price;
    }
    public String getLocation() {
        return location;
    }
    public String getUsername() {
        return username;
    }
    public String getRating() {
        return rating;
    }
    public int getFoodId() { return id; }
    public String getImageUrl(){return imageurl;}
    public boolean isHasPhoto(){return hasPhoto;}
    public boolean isUserHasPhoto() {return userHasPhoto;}
    public String getUserImageUrl() { return userImageUrl;}
}
