package com.pem.mustafa.servertest.CustomObjects;

import android.graphics.drawable.Drawable;

/**
 * Created by mustafa on 19.04.2016.
 */
public class BadgeItemObject {

    public Drawable icon;
    public String title;

    public BadgeItemObject(String title, Drawable icon) {
        this.title = title;
        this.icon = icon;
    }
}
