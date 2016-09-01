package com.pem.mustafa.servertest.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pem.mustafa.servertest.Other.LoginState;
import com.pem.mustafa.servertest.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PostDetailActivity extends AppCompatActivity {

    private static final String serverAddress = LoginState.getServerAdress();
    private static final String clientType = "1";
    private static Context appContext;

    private static TextView foodnameTv;
    private static TextView priceTv;
    private static TextView descriptionTv;
    private static TextView usernameTv;
    //private static TextView titleTv;
    private static Button requestBttn;
    private static Button reportBttn;
    //private static View divider;
    private static ImageView foodImage;
    private static ImageView userImage;

    private static String foodname;
    private static String price;
    private static String location;
    private static String foodownername;
    private static String rating;
    private static int foodId;
    private boolean hasPhoto;
    private String imageUrl;

    private static LinearLayout requestButtonProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detailv2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.postdetailtoolbarv2);
        setSupportActionBar(toolbar);

        appContext = this.getApplicationContext();

        foodnameTv = (TextView) findViewById(R.id.postdetailfoodnamev2);
        priceTv = (TextView) findViewById(R.id.postdetailpricev2);
        //descriptionTv = (TextView) findViewById(R.id.postdetaildescription);
        usernameTv = (TextView) findViewById(R.id.postdetailusernamev2);
        //titleTv = (TextView) findViewById(R.id.postdetailusertitlev2);
        requestBttn = (Button) findViewById(R.id.postdetailrequestbuttonv2);
        //reportBttn = (Button) findViewById(R.id.postdetailreportbutton);
        //divider = (View) findViewById(R.id.postdetailthirddivider);
        requestButtonProgress = (LinearLayout) findViewById(R.id.postdetailrequestprogressv2);
        foodImage = (ImageView) findViewById(R.id.postdetailfoodphotov2);
        userImage = (ImageView) findViewById(R.id.postdetailuserphotov2);

        requestButtonProgress.setVisibility(View.GONE);


        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            foodname = extras.getString("foodname");
            price = extras.getString("price");
            location = extras.getString("location");
            foodownername = extras.getString("username");
            rating = extras.getString("rating");
            foodId = extras.getInt("foodid");
            hasPhoto = extras.getBoolean("hasphoto");
            imageUrl = extras.getString("foodimageurl");


            //Toast.makeText(getApplicationContext(), "User : " + username + ", Food : " + foodname, Toast.LENGTH_SHORT).show();

            if(foodname != null && price != null && foodownername != null)
            {
                foodnameTv.setText(foodname);
                priceTv.setText(price + " TL");
                usernameTv.setText(foodownername);
                if (hasPhoto)
                {
                    Picasso.with(appContext).load(imageUrl).into(foodImage);
                }
                getProfilePhoto();

            }
        }

        if(foodownername.equals(LoginState.getUserInfo(appContext)))
        {
            requestBttn.setVisibility(View.GONE);
            //reportBttn.setVisibility(View.GONE);
            //divider.setVisibility(View.GONE);
        }
    }

    public void requestButtonClick(View v)
    {
        //submitrequest/?username=mustafa&foodowner=mustafa55&client_type=1&foodid=1
        //Toast.makeText(getApplicationContext(), "Request succesful", Toast.LENGTH_SHORT).show();
        requestBttn.setClickable(false);
        requestBttn.setVisibility(View.GONE);
        requestButtonProgress.setVisibility(View.VISIBLE);
        String username = LoginState.getUserInfo(appContext);

        String url = "http://" + serverAddress + "/submitrequest/?username="+ username +
                "&client_type=" + clientType+
                "&foodowner=" + foodownername.replace(' ', '+')+
                "&foodid=" + foodId;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        Toast.makeText(getApplicationContext(), "Request Successful", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        requestBttn.setClickable(true);
                        requestBttn.setVisibility(View.VISIBLE);
                        requestButtonProgress.setVisibility(View.GONE);
                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Request Failed(server)", Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(PostDetailActivity.this).add(jsonRequest);
    }

    public void userPhotoOnClick(View v)
    {
        String currentUsername = LoginState.getUserInfo(appContext);
        if(foodownername.equals(currentUsername))
        {
            Intent toMyProfile = new Intent(PostDetailActivity.this, ProfileActivity.class);
            startActivity(toMyProfile);
        }
        else
        {
            Intent toOtherProfile = new Intent(PostDetailActivity.this, OtherProfile.class);
            toOtherProfile.putExtra("foodownername", foodownername);
            startActivity(toOtherProfile);
        }
    }

    private void getProfilePhoto()
    {
        //username = username.replace(' ', '+');
        String url = "http://" + serverAddress + "/showphoto/?client_type=" + clientType
                +"&username=" + foodownername;

        JsonArrayRequest jsonRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray responseArray) {
                        // the response is already constructed as a JSONObject!

                        try {

                            if(responseArray.length() > 0)
                            {
                                JSONObject responseObj = responseArray.getJSONObject(0);
                                String photoUrl = responseObj.getString("photo");
                                Log.d("PostDetailActivity", foodownername + " " + photoUrl);
                                Picasso.with(appContext).load(photoUrl)
                                        .error(R.drawable.cheficon3)
                                        .into(userImage, new com.squareup.picasso.Callback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onError() {

                                            }
                                        });
                            }
                            else
                            {

                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Couldn't get profile image(json)", Toast.LENGTH_SHORT).show();
                        }



                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        //responseTv.setText("Login Failed \n" + error.toString());
                        Toast.makeText(getApplicationContext(), "Couldn't get profile image(server)", Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(PostDetailActivity.this).add(jsonRequest);
    }
}
