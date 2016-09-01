package com.pem.mustafa.servertest.Activities;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.pem.mustafa.servertest.Adapters.FriendRequestAdapter;
import com.pem.mustafa.servertest.Other.LoginState;
import com.pem.mustafa.servertest.R;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FriendRequestsActivity extends AppCompatActivity {

    private static final String TAG = FriendRequestsActivity.class.getName();
    private static final String serverAddress = LoginState.getServerAdress();
    private static final String clientType = "1";
    private static Context applicationContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList results = new ArrayList<String>();
    private int adapterState = 0;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public TextView emptyListAlert;
    private LinearLayout progressWheel;
    private CoordinatorLayout recyclerLayout;
    private static String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        Toolbar toolbar = (Toolbar) findViewById(R.id.friendrequesttoolbar);
        setSupportActionBar(toolbar);

        emptyListAlert = (TextView) findViewById(R.id.friendrequestemptytext);
        emptyListAlert.setVisibility(View.GONE);

        recyclerLayout = (CoordinatorLayout) findViewById(R.id.friendrequestrecyclerlayout);
        recyclerLayout.setVisibility(View.GONE);

        applicationContext = this.getApplicationContext();

        username = LoginState.getUserInfo(applicationContext);

        progressWheel = (LinearLayout) findViewById(R.id.friendrequestprogresswheel);
        progressWheel.setVisibility(View.VISIBLE);
        //wheel.setBarColor(Color.BLUE);

        mRecyclerView = (RecyclerView) findViewById(R.id.friendrequestRecyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.friendrequestswiperefresh);

        mSwipeRefreshLayout.setColorSchemeColors(R.color.swipecolor1, R.color.swipecolor2);

        mRecyclerView.setHasFixedSize(true);
        mAdapter = new FriendRequestAdapter(results, applicationContext);;
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        getDataSet();

        //runnable.run();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshRecyclerViewOnSwipe();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //runnable.run();
        emptyListAlert.setVisibility(View.GONE);
        progressWheel.setVisibility(View.VISIBLE);
        recyclerLayout.setVisibility(View.GONE);
        results.clear();
        adapterState = 0;
        mAdapter = new FriendRequestAdapter(results, applicationContext);;
        mRecyclerView.setAdapter(mAdapter);
        getDataSet();
        //Log.d(TAG, "onResume called");
    }



    @Override
    protected void onStop() {
        super.onStop();
        //handler.removeCallbacks(runnable);
    }

    private void getDataSet() {

        String url = "http://" + serverAddress + "/getfriendrequests/?username="+ username +"&client_type="+clientType;

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
                                JSONObject userObj = tempObj.getJSONObject("user");
                                username = userObj.getString("username");

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
                            mAdapter = new FriendRequestAdapter(reversed, applicationContext);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                        progressWheel.setVisibility(View.GONE);
                        recyclerLayout.setVisibility(View.VISIBLE);

                        if(results.size() == 0)
                        {
                            emptyListAlert.setVisibility(View.VISIBLE);
                            recyclerLayout.setVisibility(View.GONE);
                        }



                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        //responseTv.setText("Login Failed \n" + error.toString());
                        Toast.makeText(getApplicationContext(), "Couldn't get items(server)", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                        progressWheel.setVisibility(View.GONE);
                        recyclerLayout.setVisibility(View.VISIBLE);

                    }
                });

        Volley.newRequestQueue(FriendRequestsActivity.this).add(jsonRequest);
        //return results;




    }


    private void refreshRecyclerViewOnSwipe()
    {
        getDataSet();
        //mSwipeRefreshLayout.setRefreshing(false);
    }
}
