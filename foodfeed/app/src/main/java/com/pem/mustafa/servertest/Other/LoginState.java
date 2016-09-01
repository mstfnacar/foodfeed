package com.pem.mustafa.servertest.Other;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mustafa on 02.12.2015.
 */
public final class LoginState {


    private static int LoggedIn = 1;
    private static int LoggedOut = 0;
    private static int HAS_PHOTO = 1;
    private static int NO_PHOTO = 0;
    private static String PrefName = "com.pem.servertest.loginstatepreferences";
    private static String loginState = "loginstate";
    private static String usernameKey = "username";
    private static String latitudeKey = "latitude";
    private static String longitudeKey = "longitude";
    private static String hasLocationKey = "haslocation";
    private static String photoKey = "hasphoto";
    private static String photoUrlKey = "photourl";
    private static String locationIntervalKey = "locationinterval";
    private static String locationChangeFlagKey = "locationchangeflag";

    private static final String serverAdress = "serveradress";
    // dummy private constructor - to prevent creating a class instance
    private LoginState(){}

    /**
     * returns login state
    */
    public static boolean checkLoginState(Context activityContext)
    {
        boolean state = false;

        SharedPreferences sharedPref;

        sharedPref = activityContext.getSharedPreferences(PrefName, Context.MODE_PRIVATE);

        //SharedPreferences.Editor editor = sharedPref.edit();

        int defaultValue = sharedPref.getInt(loginState, 0); // default is logged out

        if(defaultValue == 1)
            state = true;
        else
            state = false;

        return state;
    }

    public static void userLogout(Context activityContext)
    {
        SharedPreferences sharedPref;
        sharedPref = activityContext.getSharedPreferences(PrefName, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(loginState, LoggedOut);
        editor.commit();
    }

    public static void userLogin(Context activityContext)
    {
        SharedPreferences sharedPref;
        sharedPref = activityContext.getSharedPreferences(PrefName, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(loginState, LoggedIn);
        editor.commit();
    }

    public static void setUserInfo(Context activityContext, String username)
    {
        SharedPreferences sharedPref;
        sharedPref = activityContext.getSharedPreferences(PrefName, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(usernameKey, username);
        editor.commit();
    }

    public static void setHasPhoto(Context activityContext, int photoState, String photoUrl)
    {
        SharedPreferences sharedPref;
        sharedPref = activityContext.getSharedPreferences(PrefName, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(photoKey, photoState);
        editor.putString(photoUrlKey, photoUrl);
        editor.commit();
    }

    public static int getHasPhoto(Context activityContext)
    {
        SharedPreferences sharedPref;
        sharedPref = activityContext.getSharedPreferences(PrefName, Context.MODE_PRIVATE);

        int result = sharedPref.getInt(photoKey, NO_PHOTO);

        return result;
    }

    public static String getUserPhoto(Context activityContext)
    {
        SharedPreferences sharedPref;
        sharedPref = activityContext.getSharedPreferences(PrefName, Context.MODE_PRIVATE);

        String result = sharedPref.getString(photoUrlKey, "");

        return result;
    }

    public static String getUserInfo(Context activityContext)
    {
        SharedPreferences sharedPref;
        sharedPref = activityContext.getSharedPreferences(PrefName, Context.MODE_PRIVATE);

        String result = sharedPref.getString(usernameKey, "default");

        return result;
    }

    public static void setLocationInfo(Context activityContext, int hasLocation, String latitude, String longitude)
    {
        if(hasLocation == 1)
        {
            SharedPreferences sharedPref;
            sharedPref = activityContext.getSharedPreferences(PrefName, Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(latitudeKey, latitude);
            editor.putString(longitudeKey, longitude);
            editor.putInt(hasLocationKey, hasLocation);
            editor.commit();
        }
        else
        {
            SharedPreferences sharedPref;
            sharedPref = activityContext.getSharedPreferences(PrefName, Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(latitudeKey, "-1");
            editor.putString(longitudeKey, "-1");
            editor.putInt(hasLocationKey, 0);
        }

    }

    public static String getLocationLatitude(Context activityContext)
    {
        SharedPreferences sharedPref;
        sharedPref = activityContext.getSharedPreferences(PrefName, Context.MODE_PRIVATE);

        String result = sharedPref.getString(latitudeKey, "-1");

        return result;
    }

    public static String getLocationLongitude(Context activityContext)
    {
        SharedPreferences sharedPref;
        sharedPref = activityContext.getSharedPreferences(PrefName, Context.MODE_PRIVATE);

        String result = sharedPref.getString(longitudeKey, "-1");

        return result;
    }

    public static int getHasLocation(Context activityContext)
    {
        SharedPreferences sharedPref;
        sharedPref = activityContext.getSharedPreferences(PrefName, Context.MODE_PRIVATE);

        int result = sharedPref.getInt(hasLocationKey, 0);

        return result;
    }

    public static void setLocationInterval(Context activityContext, int interval)
    {
        SharedPreferences sharedPref;
        sharedPref = activityContext.getSharedPreferences(PrefName, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(locationIntervalKey, interval);
        editor.commit();
    }

    public static int getLocationInterval(Context activityContext)
    {
        SharedPreferences sharedPref;
        sharedPref = activityContext.getSharedPreferences(PrefName, Context.MODE_PRIVATE);

        int result = sharedPref.getInt(locationIntervalKey, 5);

        return result;
    }

    public static void setLocationIntervalChange(Context activityContext, int flag)
    {
        SharedPreferences sharedPref;
        sharedPref = activityContext.getSharedPreferences(PrefName, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(locationChangeFlagKey, flag);
        editor.commit();
    }

    public static int isIntervalChanged(Context activityContext)
    {
        SharedPreferences sharedPref;
        sharedPref = activityContext.getSharedPreferences(PrefName, Context.MODE_PRIVATE);

        int result = sharedPref.getInt(locationChangeFlagKey, 0);

        return result;
    }

    public static String getServerAdress()
    {
        return serverAdress;
    }








}
