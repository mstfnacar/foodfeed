package com.pem.mustafa.servertest.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.pem.mustafa.servertest.Adapters.FollowAdapter;
import com.pem.mustafa.servertest.CustomObjects.DataObject;
import com.pem.mustafa.servertest.CustomObjects.RequestDataObject;
import com.pem.mustafa.servertest.Other.LoginState;
import com.pem.mustafa.servertest.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OtherProfile extends AppCompatActivity {

    private static final String serverAddress = LoginState.getServerAdress();
    private static final String clientType = "1";
    private static Context applicationContext;
    private static final String TAG = OtherProfile.class.getName();

    private static String username;
    private static String userTitle;
    private static String description;
    private static int followFlag = 0;
    private static int hideFlag = 0;

    private TextView usernameTv;
    private TextView titleTv;
    private TextView descriptionTv;
    private Button followBttn;
    private TextView followingCount;
    private TextView followerCount;
    //private ImageView lockImage;
    private ImageView profileImage;

    //private ProgressWheel progressWheel;
    private LinearLayout profileImageProgress;
    private LinearLayout profilev2progresswheel;
    //private LinearLayout profileLayout;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList results = new ArrayList<DataObject>();
    private int adapterState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profilev2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.otherprofilev2toolbar);
        setSupportActionBar(toolbar);

        applicationContext = this.getApplicationContext();

        //profileLayout = (LinearLayout) findViewById(R.id.otherprofilelayout);
        //profileLayout.setVisibility(View.GONE);
        //progressWheel = (ProgressWheel) findViewById(R.id.otherprofileprogresswheel);
        //progressWheel.setVisibility(View.VISIBLE);
        profilev2progresswheel = (LinearLayout) findViewById(R.id.otherprofilev2progresswheel);

        profileImageProgress = (LinearLayout) findViewById(R.id.otherprofilev2profilepictureprogress);

        // new v2
        /*usernameTv = (TextView) findViewById(R.id.otherprofileUsername);
        titleTv = (TextView) findViewById(R.id.otherprofileTitle);
        descriptionTv = (TextView) findViewById(R.id.otherprofileDescription);
        followBttn = (Button) findViewById(R.id.otherprofilefollowbttn);
        followingCount = (TextView) findViewById(R.id.otherfollowingcounttv);
        followerCount = (TextView) findViewById(R.id.otherfollowercounttv);
        lockImage = (ImageView) findViewById(R.id.otherprofilelockimage);
        profileImage = (ImageView) findViewById(R.id.otherprofileImage);*/

        usernameTv = (TextView) findViewById(R.id.otherprofilev2Username);
        titleTv = (TextView) findViewById(R.id.otherprofilev2Title);
        descriptionTv = (TextView) findViewById(R.id.otherprofilev2Description);
        followBttn = (Button) findViewById(R.id.otherprofilev2followbttn);
        followingCount = (TextView) findViewById(R.id.otherprofilev2followingcounttv);
        followerCount = (TextView) findViewById(R.id.otherprofilev2followercounttv);
        profileImage = (ImageView) findViewById(R.id.otherprofilev2ProfileImage);

        mRecyclerView = (RecyclerView) findViewById(R.id.otherprofilev2postrecyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //lockImage.setVisibility(View.GONE);
        followBttn.setText("FOLLOw");

        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            username = extras.getString("foodownername");

            if(username != null)
            {
                username = username.replace(' ', '+');
                checkFollowStatus();
                checkHideStatus();
                getFollowCounts();
                getProfilInfo();
                getUserTitle();
                getProfilePhoto();
            }
        }



    }

    private void getProfilInfo()
    {

        String url = "http://" + serverAddress + "/getuserinfo/?client_type=" + clientType
                +"&username=" + username;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject responseObject) {

                        try {
                            description = responseObject.getString("bio");
                            String responseUsername = responseObject.getJSONObject("user").getString("username");
                            usernameTv.setText(responseUsername);
                            if(description.length() == 0)
                                descriptionTv.setText("An ambitious cook from HEART");
                            else
                                descriptionTv.setText(description);

                            profilev2progresswheel.setVisibility(View.GONE);
                            //profileLayout.setVisibility(View.VISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Couldn't get profile info(json)", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        profilev2progresswheel.setVisibility(View.GONE);
                        //profileLayout.setVisibility(View.VISIBLE);
                        //responseTv.setText("Login Failed \n" + error.toString());
                        Toast.makeText(getApplicationContext(), "Couldn't get profile info(server)", Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(OtherProfile.this).add(jsonRequest);
    }

    private void getFollowCounts()
    {
        String url = "http://" + serverAddress + "/followcounts/?client_type=" + clientType
                +"&username=" + username;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject responseObject) {
                        // the response is already constructed as a JSONObject!

                        try {

                            String fllwrCount = responseObject.getString("numberOfFollowers");
                            String fllwingCount = responseObject.getString("numberOfFollowing");
                            followerCount.setText(fllwrCount);
                            followingCount.setText(fllwingCount);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Couldn't get follow counts(json)", Toast.LENGTH_SHORT).show();
                        }



                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        //responseTv.setText("Login Failed \n" + error.toString());
                        Toast.makeText(getApplicationContext(), "Couldn't get follow counts(server)", Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(OtherProfile.this).add(jsonRequest);
    }

    public void followButtonOnClick(View v)
    {
        Log.d(TAG, "HIDE FLAG IS " + hideFlag);
        String url = "http://" + serverAddress + "/friendrequest/?client_type=" + clientType
                +"&requestownerusername=" + LoginState.getUserInfo(applicationContext) + "&requesteduserusername=" + username;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject responseObject) {

                    if(followFlag == 0)
                    {
                        //Log.d(TAG, "Followed");
                        if(hideFlag == 0)
                        {
                            followFlag = 1;
                            followBttn.setText("Unfollow");
                            //lockImage.setVisibility(View.GONE);
                            //followBttn.setBackgroundColor(Color.WHITE);
                        }
                        else if(hideFlag == 1)
                        {
                            followFlag = 1;
                            followBttn.setText("Request Sent");
                            //followBttn.setBackgroundColor(Color.rgb(172, 186, 75));
                        }

                    }
                    else if(followFlag == 1)
                    {
                        //Log.d(TAG, "Unfollowed");
                        if(hideFlag == 0)
                        {
                            followFlag = 0;
                            followBttn.setText("Follow");
                            //lockImage.setVisibility(View.GONE);
                            //followBttn.setBackgroundColor(Color.rgb(33, 150, 243));
                        }
                        else if(hideFlag == 1)
                        {
                            followFlag = 0;
                            followBttn.setText("Follow");
                            //lockImage.setVisibility(View.VISIBLE);
                            //followBttn.setBackgroundColor(Color.rgb(33, 150, 243));
                        }

                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                error.printStackTrace();
                //responseTv.setText("Login Failed \n" + error.toString());
                Toast.makeText(getApplicationContext(), "Couldn't get follow info(server)", Toast.LENGTH_SHORT).show();
                }
            });

        Volley.newRequestQueue(OtherProfile.this).add(jsonRequest);


    }

    private void checkFollowStatus()
    {

        String url = "http://" + serverAddress + "/isfollowed/?client_type=" + clientType
                +"&requestownerusername=" + LoginState.getUserInfo(applicationContext) + "&requesteduserusername=" + username;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject responseObject) {

                try {
                    followFlag = responseObject.getInt("followStatus");
                    if(followFlag == 0)
                    {
                        followBttn.setText("Follow");
                        //followBttn.setBackgroundColor(Color.rgb(33, 150, 243));
                    }
                    else if(followFlag == 1)
                    {
                        followBttn.setText("Unfollow");
                        //followBttn.setBackgroundColor(Color.WHITE);
                    }
                    else if(followFlag == 2)
                    {
                        followBttn.setText("Request Sent");
                        //followBttn.setBackgroundColor(Color.rgb(172, 186, 75));
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Couldn't get follow info(json)", Toast.LENGTH_SHORT).show();
                }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                error.printStackTrace();
                //responseTv.setText("Login Failed \n" + error.toString());
                Toast.makeText(getApplicationContext(), "Couldn't get follow info(server)", Toast.LENGTH_SHORT).show();
                }
            });

        Volley.newRequestQueue(OtherProfile.this).add(jsonRequest);

    }

    private void checkHideStatus()
    {

        String url = "http://" + serverAddress + "/islocked/?client_type=" + clientType
                +"&username=" + username + "&follower="+ LoginState.getUserInfo(applicationContext) +
                "&comparetype=1" + "&actiontype=0";
        //http://46.101.180.126/islocked/?client_type=1&username=mustafa&follower=mustafa&comparetype=0&actiontype=0

        JsonObjectRequest jsonRequest = new JsonObjectRequest
            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject responseObject) {

                    try {
                        hideFlag = responseObject.getInt("hideStatus");
                        if(hideFlag == 1)
                        {
                            if(followFlag == 0 || followFlag == 2 )
                            {
                                //lockImage.setVisibility(View.VISIBLE);
                            }
                            else if(followFlag == 1)
                            {
                                //lockImage.setVisibility(View.GONE);
                            }


                        }
                        else
                        {
                            //lockImage.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Couldn't get lock status info(json)", Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    //responseTv.setText("Login Failed \n" + error.toString());
                    Toast.makeText(getApplicationContext(), "Couldn't get lock status info(server)", Toast.LENGTH_SHORT).show();
                }
            });

        Volley.newRequestQueue(OtherProfile.this).add(jsonRequest);
    }

    public void otherfollowerListClick(View view)
    {
        Intent toFollowList = new Intent(OtherProfile.this, FollowListActivity.class);
        toFollowList.putExtra("followtype", "followers" );
        toFollowList.putExtra("username", username );

        startActivity(toFollowList);
    }

    public void otherfollowingListClick(View view)
    {
        Intent toFollowList = new Intent(OtherProfile.this, FollowListActivity.class);
        toFollowList.putExtra("followtype", "following" );
        toFollowList.putExtra("username", username );

        startActivity(toFollowList);
    }

    private void getProfilePhoto()
    {

        profileImage.setClickable(false);
        profileImage.setVisibility(View.INVISIBLE);
        profileImageProgress.setVisibility(View.VISIBLE);

        //username = username.replace(' ', '+');
        String url = "http://" + serverAddress + "/showphoto/?client_type=" + clientType
                +"&username=" + username;

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
                                Picasso.with(applicationContext).load(photoUrl)
                                        .error(R.drawable.cheficon3)
                                        .into(profileImage, new com.squareup.picasso.Callback() {
                                            @Override
                                            public void onSuccess() {
                                                profileImage.setClickable(true);
                                                profileImage.setVisibility(View.VISIBLE);
                                                profileImageProgress.setVisibility(View.GONE);
                                            }

                                            @Override
                                            public void onError() {
                                                profileImage.setClickable(true);
                                                profileImage.setVisibility(View.VISIBLE);
                                                profileImageProgress.setVisibility(View.GONE);
                                            }
                                        });
                            }
                            else
                            {
                                profileImage.setClickable(true);
                                profileImage.setVisibility(View.VISIBLE);
                                profileImageProgress.setVisibility(View.GONE);
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

        Volley.newRequestQueue(OtherProfile.this).add(jsonRequest);
    }

    private void getUserTitle()
    {
        final Resources resources = getResources();

        String url = "http://" + serverAddress + "/getbadges/?client_type=" + clientType
                +"&username=" + username;

        //http://46.101.180.126/getbadges/?client_type=1&username=Mustafa

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject responseObject) {
                        // the response is already constructed as a JSONObject!

                        try {

                            userTitle = responseObject.getString("usertitle");
                            if(userTitle.length() > 0)
                            {
                                titleTv.setText("'" + userTitle + "'");
                                titleTv.setVisibility(View.VISIBLE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Couldn't get user title(json)", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        //responseTv.setText("Login Failed \n" + error.toString());
                        Toast.makeText(getApplicationContext(), "Couldn't get user title(server)", Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(OtherProfile.this).add(jsonRequest);
    }

    /*private void getUserPosts()
    {
        String url = "http://" + serverAddress + "/profileposts/?username="+ username +"&client_type="+clientType;

        JsonArrayRequest jsonRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray responseArray) {
                        // the response is already constructed as a JSONObject!
                        results.clear();
                        String username;
                        int id;
                        //Log.d(TAG, "REFRESHING");
                        int index = 0;
                        for(int i=0; i < responseArray.length() ; i++)
                        {
                            try {
                                JSONObject tempObj = responseArray.getJSONObject(i);
                                username = tempObj.getString("username");

                                results.add(index, username);
                                index++;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }



                        if(adapterState != results.size()) {
                            adapterState = results.size();
                            Log.d(TAG, "" + results.size());
                            ArrayList<String> reversed = new ArrayList<>();
                            for (int i = results.size() - 1; i >= 0; i--)
                                reversed.add((String) results.get(i));
                            mAdapter = new FollowAdapter(reversed, applicationContext);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                        //mSwipeRefreshLayout.setRefreshing(false);
                        //progressWheel.setVisibility(View.GONE);
                        //recyclerLayout.setVisibility(View.VISIBLE);

                        if(results.size() == 0)
                        {
                            //emptyListAlert.setVisibility(View.VISIBLE);
                            //recyclerLayout.setVisibility(View.GONE);
                        }



                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        //responseTv.setText("Login Failed \n" + error.toString());
                        Toast.makeText(getApplicationContext(), "Couldn't get items(server)", Toast.LENGTH_SHORT).show();
                        //mSwipeRefreshLayout.setRefreshing(false);
                        //progressWheel.setVisibility(View.GONE);
                        //recyclerLayout.setVisibility(View.VISIBLE);

                    }
                });

        Volley.newRequestQueue(OtherProfile.this).add(jsonRequest);
    }*/

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_other_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.otherprofilemessagemenuitem) {

            Intent toMessageBody = new Intent(OtherProfile.this, MessageBodyActivity.class);
            toMessageBody.putExtra("username", username );

            startActivity(toMessageBody);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
