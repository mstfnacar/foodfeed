package com.pem.mustafa.servertest.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pem.mustafa.servertest.Adapters.ProfileRequestFeedAdapter;
import com.pem.mustafa.servertest.CustomObjects.RequestDataObject;
import com.pem.mustafa.servertest.Other.BaseVolleyRequest;
import com.pem.mustafa.servertest.Other.DividerItemDecoration;
import com.pem.mustafa.servertest.Other.LoginState;
import com.pem.mustafa.servertest.Other.VolleySingleton;
import com.pem.mustafa.servertest.R;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private static final String serverAddress = LoginState.getServerAdress();
    private static final String clientType = "1";
    private static Context applicationContext;
    private static final String TAG = ProfileActivity.class.getName();

    private static String username;
    private static String title;
    private static String description;

    private TextView usernameTv;
    private TextView titleTv;
    private TextView descriptionTv;
    private TextView followingCount;
    private TextView followerCount;
    private TextView emptylistTv;
    //private ProgressWheel progressWheel;
    private LinearLayout recyclerprogresswheel;
    private LinearLayout profileImageProgress;
    //private LinearLayout profileLayout;
    private ImageView profileImage;

    private LinearLayout profilev3progresswheel;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList results = new ArrayList<RequestDataObject>();
    private int adapterState = 0;

    private Bitmap profileBitmap;

    ////

    String mimeType;
    DataOutputStream dos = null;
    String lineEnd = "\r\n";
    String boundary = "apiclient-" + System.currentTimeMillis();
    String twoHyphens = "--";
    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 1024 * 1024;

    ////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_v3);

        //new v3
        Toolbar toolbar = (Toolbar) findViewById(R.id.profilev3toolbar);
        setSupportActionBar(toolbar);

        //new v3
        //profileLayout = (LinearLayout) findViewById(R.id.profilelayout);
        //profileLayout.setVisibility(View.GONE);
        //progressWheel = (ProgressWheel) findViewById(R.id.profileprogresswheel);
        //progressWheel.setVisibility(View.VISIBLE);

        //new v3
        profilev3progresswheel = (LinearLayout) findViewById(R.id.profilev3progresswheel);
        profilev3progresswheel.setVisibility(View.VISIBLE);

        recyclerprogresswheel = (LinearLayout) findViewById(R.id.profilev3rcyclerprogresswheel);
        recyclerprogresswheel.setVisibility(View.VISIBLE);

        applicationContext = this.getApplicationContext();

        //new v3
        /*usernameTv = (TextView) findViewById(R.id.profileUsername);
        titleTv = (TextView) findViewById(R.id.profileTitle);
        descriptionTv = (TextView) findViewById(R.id.profileDescription);
        followingCount = (TextView) findViewById(R.id.followingcounttv);
        followerCount = (TextView) findViewById(R.id.followercounttv);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        profileImageProgress = (LinearLayout) findViewById(R.id.profilepictureprogress);*/

        usernameTv = (TextView) findViewById(R.id.profilev3Username);
        titleTv = (TextView) findViewById(R.id.profilev3Title);
        descriptionTv = (TextView) findViewById(R.id.profilev3Description);
        followingCount = (TextView) findViewById(R.id.followingv3counttv);
        followerCount = (TextView) findViewById(R.id.followerv3counttv);
        profileImage = (ImageView) findViewById(R.id.profilev3ProfileImage);
        profileImageProgress = (LinearLayout) findViewById(R.id.profilev3profilepictureprogress);
        //emptylistTv = (TextView) findViewById(R.id.profileemptylisttext);
        //emptylistTv.setVisibility(View.GONE);

        profileImageProgress.setVisibility(View.GONE);
        titleTv.setVisibility(View.GONE);

        getFollowCounts();
        getProfilInfo();
        getProfilePhoto();
        getUserTitle();


        mRecyclerView = (RecyclerView) findViewById(R.id.profilev3requestrecyclerview);
        mRecyclerView.setVisibility(View.GONE);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //ArrayList<DataObject> results = getDataSet();
        getDataSet();
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        //mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(ProfileActivity.this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


    }

    private void getProfilInfo()
    {
        username  = LoginState.getUserInfo(applicationContext);
        //username = username.replace(' ', '+');
        String url = "http://" + serverAddress + "/getuserinfo/?client_type=" + clientType
                                                            +"&username=" + username;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject responseObject) {
                        // the response is already constructed as a JSONObject!

                    try {
                        description = responseObject.getString("bio");
                        String responseUsername = responseObject.getJSONObject("user").getString("username");

                        usernameTv.setText(responseUsername);
                        if(description.length() == 0)
                            descriptionTv.setText("An ambitious cook from HEART");
                        else
                            descriptionTv.setText(description);
                        profilev3progresswheel.setVisibility(View.GONE);
                        //profileLayout.setVisibility(View.VISIBLE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Couldn't get profile info(json)", Toast.LENGTH_SHORT).show();
                        profilev3progresswheel.setVisibility(View.GONE);
                        //profileLayout.setVisibility(View.VISIBLE);
                    }



                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        //responseTv.setText("Login Failed \n" + error.toString());
                        Toast.makeText(getApplicationContext(), "Couldn't get profile info(server)", Toast.LENGTH_SHORT).show();
                        profilev3progresswheel.setVisibility(View.GONE);
                        //profileLayout.setVisibility(View.VISIBLE);
                    }
                });

        Volley.newRequestQueue(ProfileActivity.this).add(jsonRequest);
    }


    private void getDataSet()
    {
        // getrequests/?client_type=1&username=mustafa&request_type=0
        username  = LoginState.getUserInfo(applicationContext);
        //username = username.replace(' ', '+');
        String url = "http://" + serverAddress + "/getrequests/?client_type=" + clientType
                                                    + "&username=" + username
                                                    + "&request_type=0";

        JsonArrayRequest jsonRequest = new JsonArrayRequest
            (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray responseArray) {
                    // the response is already constructed as a JSONObject!
                    results.clear();
                    String foodName;
                    String price;
                    String foodownername;
                    int id;

                    int index = 0;
                    for(int i=0; i < responseArray.length() ; i++)
                    {
                        try {
                            JSONObject tempObj = responseArray.getJSONObject(i);
                            foodName = tempObj.getString("foodname");
                            //price = tempObj.getString("price");
                            foodownername = tempObj.getString("requestownername");
                            id = tempObj.getInt("requestkey");
                            RequestDataObject obj = new RequestDataObject(foodName,"5",foodownername, id);    // get price and request id
                            results.add(index, obj);
                            index++;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    /*if(results.isEmpty())
                        emptylistTv.setVisibility(View.VISIBLE);
                    else
                        emptylistTv.setVisibility(View.GONE);*/

                    if(adapterState != results.size()) {
                        adapterState = results.size();
                        //Log.d(TAG, "" + results.size());
                        ArrayList<RequestDataObject> reversed = new ArrayList<>();
                        for (int i = results.size() - 1; i >= 0; i--)
                            reversed.add((RequestDataObject) results.get(i));
                        mAdapter = new ProfileRequestFeedAdapter(reversed, applicationContext);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                    recyclerprogresswheel.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    //responseTv.setText("Login Failed \n" + error.toString());
                    Toast.makeText(getApplicationContext(), "Couldn't get items(server)", Toast.LENGTH_SHORT).show();
                    recyclerprogresswheel.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            });

        Volley.newRequestQueue(ProfileActivity.this).add(jsonRequest);
        //return results;
    }

    private void getFollowCounts()
    {
        username  = LoginState.getUserInfo(applicationContext);
        //username = username.replace(' ', '+');
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

        Volley.newRequestQueue(ProfileActivity.this).add(jsonRequest);
    }

    public void followerListClick(View view)
    {
        username  = LoginState.getUserInfo(applicationContext);
        Intent toFollowList = new Intent(ProfileActivity.this, FollowListActivity.class);
        toFollowList.putExtra("followtype", "followers" );
        toFollowList.putExtra("username", username );


        startActivity(toFollowList);
    }

    public void followingListClick(View view)
    {
        username  = LoginState.getUserInfo(applicationContext);
        Intent toFollowList = new Intent(ProfileActivity.this, FollowListActivity.class);
        toFollowList.putExtra("followtype", "following" );
        toFollowList.putExtra("username", username );


        startActivity(toFollowList);
    }

    private void getUserTitle()
    {
        final Resources resources = getResources();
        String username = LoginState.getUserInfo(applicationContext);

        String url = "http://" + serverAddress + "/getbadges/?client_type=" + clientType
                +"&username=" + username;

        //http://46.101.180.126/getbadges/?client_type=1&username=Mustafa

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject responseObject) {
                        // the response is already constructed as a JSONObject!

                        try {

                            String userTitle = responseObject.getString("usertitle");
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

        Volley.newRequestQueue(ProfileActivity.this).add(jsonRequest);
    }

    public void changeProfilePicture(View v)
    {
        Crop.pickImage(ProfileActivity.this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case Crop.REQUEST_PICK: {
                if (resultCode == RESULT_OK) {
                    profileImage.setClickable(false);
                    profileImage.setVisibility(View.GONE);
                    profileImageProgress.setVisibility(View.VISIBLE);

                    Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
                    //new Crop(data.getData()).output(outputUri).asSquare().start(this);
                    Crop.of(data.getData(), outputUri).asSquare().start(this);
                    Log.d(TAG, "REQUEST_PICK returned");
                    profileImage.setImageURI(null);//necessary
                    profileImage.setImageURI(Crop.getOutput(data));

                    profileImage.setClickable(true);
                    profileImage.setVisibility(View.VISIBLE);
                    profileImageProgress.setVisibility(View.GONE);

                }
                break;
            }
            case Crop.REQUEST_CROP: {
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "REQUEST_CROP returned");
                    profileImage.setImageURI(null);//necessary
                    profileImage.setImageURI(Crop.getOutput(data));

                    Uri filePath = Crop.getOutput(data);

                    try {
                        //Getting the Bitmap from Gallery
                        profileBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                        //setProfilePhoto();
                        uploadProfilePhoto();
                        //Setting the Bitmap to ImageView
                        //imageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (resultCode == Crop.RESULT_ERROR) {
                    Toast.makeText(this, Crop.getError(data).getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void getProfilePhoto()
    {

        profileImage.setClickable(false);
        profileImage.setVisibility(View.INVISIBLE);
        profileImageProgress.setVisibility(View.VISIBLE);

        username  = LoginState.getUserInfo(applicationContext);
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

        Volley.newRequestQueue(ProfileActivity.this).add(jsonRequest);
    }



    private void uploadProfilePhoto()
    {
        profileImage.setClickable(false);
        profileImage.setVisibility(View.INVISIBLE);
        profileImageProgress.setVisibility(View.VISIBLE);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        profileBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        final byte[] bitmapData = byteArrayOutputStream.toByteArray();
        String url = "http://"+ serverAddress +"/uploadphotoand/?username="+username;

        mimeType = "multipart/form-data;boundary=" + boundary;

        BaseVolleyRequest baseVolleyRequest = new BaseVolleyRequest(1, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                Toast.makeText(ProfileActivity.this, "Profile photo changed", Toast.LENGTH_SHORT).show();
                profileImage.setClickable(true);
                profileImage.setVisibility(View.VISIBLE);
                profileImageProgress.setVisibility(View.GONE);
                Log.d(TAG, response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProfileActivity.this, "An error occured at  photo upload", Toast.LENGTH_SHORT).show();
                profileImage.setClickable(true);
                profileImage.setVisibility(View.VISIBLE);
                profileImageProgress.setVisibility(View.GONE);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return mimeType;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                dos = new DataOutputStream(bos);
                try {
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"photo\";filename=\""
                            + username +"pp.jpeg" + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(bitmapData);
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    return bos.toByteArray();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bitmapData;
            }
        };

        VolleySingleton.getInstance(applicationContext).addToRequestQueue(baseVolleyRequest);

    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.profileinboxmenuitem) {

            Intent newIntent = new Intent(ProfileActivity.this, MessageBoxActivity.class);
            startActivity(newIntent);

            return true;
        }
        else if(id == R.id.profilebadgesmenuitem)
        {
            Intent newIntent = new Intent(ProfileActivity.this, BadgeListActivity.class);
            startActivity(newIntent);

            return true;
        }


        return super.onOptionsItemSelected(item);
    }


}
