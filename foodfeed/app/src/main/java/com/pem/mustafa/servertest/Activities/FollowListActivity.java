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
import android.widget.TextView;
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

public class FollowListActivity extends AppCompatActivity {

    private static final String TAG = FollowListActivity.class.getName();
    private static final String serverAddress = LoginState.getServerAdress();
    private static final String clientType = "1";
    private static Context applicationContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList results = new ArrayList<FollowObj>();
    private int adapterState = 0;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView emptyListAlert;
    private ProgressWheel progressWheel;
    private CoordinatorLayout recyclerLayout;
    private static String followType = "following";
    private static String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.followlisttoolbar);
        setSupportActionBar(toolbar);

        emptyListAlert = (TextView) findViewById(R.id.followlistemptytext);
        emptyListAlert.setVisibility(View.GONE);

        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            followType = extras.getString("followtype");
            username = extras.getString("username");
            if(followType.equals("following"))
                toolbar.setSubtitle("Following");
            else
                toolbar.setSubtitle("Followers");
        }


        recyclerLayout = (CoordinatorLayout) findViewById(R.id.followlistrecyclerlayout);
        recyclerLayout.setVisibility(View.GONE);

        applicationContext = this.getApplicationContext();

        progressWheel = (ProgressWheel) findViewById(R.id.followlistprogresswheel);
        progressWheel.setVisibility(View.VISIBLE);
        //wheel.setBarColor(Color.BLUE);

        mRecyclerView = (RecyclerView) findViewById(R.id.followlistRecyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.followlistswiperefresh);

        mSwipeRefreshLayout.setColorSchemeColors(R.color.swipecolor1, R.color.swipecolor2);

        mRecyclerView.setHasFixedSize(true);
        mAdapter = new FollowAdapter(results, applicationContext);;
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
        mAdapter = new FollowAdapter(results, applicationContext);;
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

        String url = "http://" + serverAddress + "/followlist/?username="+ username +"&client_type="+clientType+"&list_type="+followType;

        JsonArrayRequest jsonRequest = new JsonArrayRequest
            (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray responseArray) {
                    // the response is already constructed as a JSONObject!
                    results.clear();
                    String username;
                    String photourl;
                    int id;
                    //Log.d(TAG, "REFRESHING");
                    int index = 0;
                    for(int i=0; i < responseArray.length() ; i++)
                    {
                        try {
                            JSONObject tempObj = responseArray.getJSONObject(i);
                            username = tempObj.getString("username");
                            photourl = tempObj.getString("photo_url");

                            results.add(index, new FollowObj(username, "http://" + serverAddress + photourl));
                            index++;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }



                    if(adapterState != results.size()) {
                        adapterState = results.size();
                        Log.d(TAG, "" + results.size());
                        ArrayList<FollowObj> reversed = new ArrayList<>();
                        for (int i = results.size() - 1; i >= 0; i--)
                            reversed.add((FollowObj) results.get(i));
                        mAdapter = new FollowAdapter(reversed, applicationContext);
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

        Volley.newRequestQueue(FollowListActivity.this).add(jsonRequest);
        //return results;




    }


    private void refreshRecyclerViewOnSwipe()
    {
        getDataSet();
        //mSwipeRefreshLayout.setRefreshing(false);
    }
}
