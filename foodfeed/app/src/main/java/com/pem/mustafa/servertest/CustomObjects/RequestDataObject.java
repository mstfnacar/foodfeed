package com.pem.mustafa.servertest.CustomObjects;

/**
 * Created by mustafa on 30.12.2015.
 */
public class RequestDataObject {

    private String foodname;
    private String price;
    private String username;
    private int id;

    public RequestDataObject (String foodname, String price, String username, int id){
        this.foodname = foodname;
        this.price = price;
        this.username = username;
        this.id = id;
    }

    public String getFoodname() {return foodname; }
    public String getPrice() { return price; }
    public String getUsername() { return username; }
    public int getRequestId() { return id; }
}
