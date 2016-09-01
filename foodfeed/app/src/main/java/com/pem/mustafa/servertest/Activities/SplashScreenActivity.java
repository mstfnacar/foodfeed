package com.pem.mustafa.servertest.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.pem.mustafa.servertest.Other.LoginState;
import com.pem.mustafa.servertest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashScreenActivity extends AppCompatActivity {


    private static Context applicationContext;
    private static final String serverAddress = LoginState.getServerAdress();
    private static final String clientType = "1";

    //// LOCATION VARS ////
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    ////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screenv2);

        applicationContext = SplashScreenActivity.this.getApplicationContext();

        //getLocation();
        getProfilePhotoAndNavigate();

        Log.d("LOCATION CHECK : ", "getLocation called");
        if (mGoogleApiClient == null) {
            Log.d("LOCATION CHECK : ", "First if statement");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {


                        @Override
                        public void onConnected(@Nullable Bundle bundle) {

                            Log.d("LOCATION CHECK : ", "Connected to api client");
                            if (ContextCompat.checkSelfPermission(applicationContext,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                    && ContextCompat.checkSelfPermission(applicationContext,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.


                                Log.d("LOCATION CHECK : ", "PERMISSION DENIED");
                                LoginState.setLocationInfo(applicationContext, 0, "-1", "-1");


                            }
                            else
                            {
                                Log.d("LOCATION CHECK : ", "PERMISSION GRANTED");
                                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                        mGoogleApiClient);
                                if (mLastLocation != null) {
                                    LoginState.setLocationInfo(applicationContext, 1,
                                            String.valueOf(mLastLocation.getLatitude()),
                                            String.valueOf(mLastLocation.getLongitude()));

                                    Log.d("Location : " , String.valueOf(mLastLocation.getLatitude()));
                                    Log.d("Location : ", String.valueOf(mLastLocation.getLongitude()));

                                }
                            }




                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            Log.d("LOCATION CHECK : ", "CONNECTION SUSPENDED");
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Log.d("LOCATION CHECK : ", "CONNECTION FAILED");
                        }
                    })
                    .addApi(LocationServices.API)
                    .build();
        }






    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void getProfilePhotoAndNavigate()
    {

        if(LoginState.checkLoginState(applicationContext))
        {
            String username = LoginState.getUserInfo(applicationContext);
            //username = username.replace(' ', '+');
            String url = "http://" + serverAddress + "/showphoto/?client_type=" + clientType
                    + "&username=" + username;

            JsonArrayRequest jsonRequest = new JsonArrayRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray responseArray) {
                            // the response is already constructed as a JSONObject!

                            try {

                                if (responseArray.length() > 0) {
                                    JSONObject responseObj = responseArray.getJSONObject(0);
                                    String photoUrl = responseObj.getString("photo");

                                    LoginState.setHasPhoto(applicationContext, 1, photoUrl);

                                } else {
                                    LoginState.setHasPhoto(applicationContext, 0, "");
                                }


                            } catch (JSONException e) {
                                //e.printStackTrace();
                            } finally {

                                final Handler transitionHandler = new Handler();

                                transitionHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        Intent newIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                                        startActivity(newIntent);
                                        finish();
                                    }
                                }, 1500);
                            }


                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();

                            if (error instanceof NoConnectionError) {
                                Toast.makeText(getApplicationContext(), "Please check your connection and relaunch app", Toast.LENGTH_SHORT).show();
                                final Handler transitionHandler = new Handler();

                                transitionHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 2500);
                            }

                            //responseTv.setText("Login Failed \n" + error.toString());
                            //Toast.makeText(getApplicationContext(), "Couldn't get profile image(server)", Toast.LENGTH_SHORT).show();
                        }
                    });

            Volley.newRequestQueue(SplashScreenActivity.this).add(jsonRequest);
        }
        else if (!LoginState.checkLoginState(applicationContext)) {
            final Handler transitionHandler = new Handler();

            transitionHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent newIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(newIntent);
                    finish();
                }
            }, 1500);
        }
    }

    private void getLocation()
    {
        Log.d("LOCATION CHECK : ", "getLocation called");
        if (mGoogleApiClient == null) {
            Log.d("LOCATION CHECK : ", "First if statement");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {


                        @Override
                        public void onConnected(@Nullable Bundle bundle) {

                            Log.d("LOCATION CHECK : ", "Connected to api client");
                            if (ContextCompat.checkSelfPermission(applicationContext,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                    && ContextCompat.checkSelfPermission(applicationContext,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.


                                Log.d("LOCATION CHECK : ", "PERMISSION DENIED");
                                LoginState.setLocationInfo(applicationContext, 0, "-1", "-1");


                            }
                            else
                            {
                                Log.d("LOCATION CHECK : ", "PERMISSION GRANTED");
                                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                        mGoogleApiClient);
                                if (mLastLocation != null) {
                                    LoginState.setLocationInfo(applicationContext, 1,
                                            String.valueOf(mLastLocation.getLatitude()),
                                            String.valueOf(mLastLocation.getLongitude()));

                                    Log.d("Location : " , String.valueOf(mLastLocation.getLatitude()));
                                    Log.d("Location : ", String.valueOf(mLastLocation.getLongitude()));

                                }
                            }




                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            Log.d("LOCATION CHECK : ", "CONNECTION SUSPENDED");
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Log.d("LOCATION CHECK : ", "CONNECTION FAILED");
                        }
                    })
                    .addApi(LocationServices.API)
                    .build();
        }
    }


}
