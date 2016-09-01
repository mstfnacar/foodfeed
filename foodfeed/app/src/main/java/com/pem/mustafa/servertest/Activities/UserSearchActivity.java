package com.pem.mustafa.servertest.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.pem.mustafa.servertest.Adapters.FollowAdapter;
import com.pem.mustafa.servertest.CustomObjects.FollowObj;
import com.pem.mustafa.servertest.Other.LoginState;
import com.pem.mustafa.servertest.R;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserSearchActivity extends AppCompatActivity {

    private static final String TAG = UserSearchActivity.class.getName();
    private static final String serverAddress = LoginState.getServerAdress();
    private static final String clientType = "1";
    private static Context applicationContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private int adapterState = 0;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList results = new ArrayList<FollowObj>();
    private ArrayList userList = new ArrayList<String>();
    private static String searchText;
    private ProgressWheel progressWheel;

    private AutoCompleteTextView searchEt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        getUserList();

        Toolbar toolbar = (Toolbar) findViewById(R.id.usersearchtoolbar);
        setSupportActionBar(toolbar);

        applicationContext = this.getApplicationContext();

        searchEt = (AutoCompleteTextView) findViewById(R.id.usersearchtext);
        mRecyclerView = (RecyclerView) findViewById(R.id.usersearchrecyclerview);
        mRecyclerView.setVisibility(View.GONE);

        progressWheel = (ProgressWheel) findViewById(R.id.usersearchprogresswheel);
        progressWheel.setVisibility(View.GONE);

        mRecyclerView.setHasFixedSize(true);
        mAdapter = new FollowAdapter(results, applicationContext);;
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }




    private void getDataSet()
    {
        String url = "http://" + serverAddress + "/usersearch/?inputtext=" + searchText;

        results.clear();
        mAdapter = new FollowAdapter(results, applicationContext);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.refreshDrawableState();

        JsonArrayRequest jsonRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray responseArray) {
                        // the response is already constructed as a JSONObject!
                        results.clear();
                        String username;// = LoginState.getUserInfo(applicationContext);
                        String photourl;
                        int id;

                        if(responseArray.length() == 0)
                        {
                            Toast.makeText(getApplicationContext(), "No related user found", Toast.LENGTH_SHORT).show();
                            mAdapter = new FollowAdapter(results, applicationContext);
                            //mAdapter.notifyDataSetChanged();
                            mRecyclerView.setAdapter(mAdapter);
                            mRecyclerView.refreshDrawableState();
                            progressWheel.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);

                            return;
                        }

                        int index = 0;
                        for(int i=0; i < responseArray.length() ; i++)
                        {
                            try {
                                JSONObject tempObj = responseArray.getJSONObject(i);
                                JSONObject userObj = tempObj.getJSONObject("user");
                                username = userObj.getString("username");
                                photourl = tempObj.getString("photo_url");
                                results.add(index, new FollowObj(username, "http://" + serverAddress + photourl));
                                index++;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        if(adapterState != results.size()) {
                            adapterState = results.size();
                            //Log.d(TAG, "" + results.size());
                            ArrayList<FollowObj> reversed = new ArrayList<>();
                            for (int i = results.size() - 1; i >= 0; i--)
                                reversed.add((FollowObj) results.get(i));
                            mAdapter = new FollowAdapter(reversed, applicationContext);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                        progressWheel.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        //responseTv.setText("Login Failed \n" + error.toString());
                        Toast.makeText(getApplicationContext(), "Couldn't get items(server)", Toast.LENGTH_SHORT).show();
                        results.clear();
                        mAdapter = new FollowAdapter(results, applicationContext);
                        //mAdapter.notifyDataSetChanged();
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.refreshDrawableState();
                        progressWheel.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                });

        Volley.newRequestQueue(UserSearchActivity.this).add(jsonRequest);
        //return results;
    }

    public void searchAction(View v)
    {
        progressWheel.setVisibility(View.VISIBLE);
        String tempStr = searchEt.getText().toString();
        searchText = tempStr.replace(' ', '+').trim();
        tempStr = tempStr.trim();

        if(tempStr.length() == 0)
        {
            Toast.makeText(getApplicationContext(), "Enter a keyword to search", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            getDataSet();
        }


        // hide keyboard after button press
        View view = UserSearchActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }


    }

    private void getUserList() {

        String url = "http://" + serverAddress + "/getuserlist/?client_type="+clientType;

        JsonArrayRequest jsonRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray responseArray) {
                        // the response is already constructed as a JSONObject!
                        userList.clear();
                        String foodName;

                        int index = 0;
                        for(int i=0; i < responseArray.length() ; i++)
                        {
                            try {
                                JSONObject tempObj = responseArray.getJSONObject(i);
                                foodName = tempObj.getString("username");
                                if(!userList.contains(foodName)) {
                                    userList.add(index, foodName);
                                    index++;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(applicationContext,R.layout.mytext,userList);
                        searchEt.setAdapter(adapter);




                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Couldn't get items(server)", Toast.LENGTH_SHORT).show();

                    }
                });

        Volley.newRequestQueue(UserSearchActivity.this).add(jsonRequest);
    }
}
