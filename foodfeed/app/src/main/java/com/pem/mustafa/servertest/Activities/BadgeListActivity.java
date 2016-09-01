package com.pem.mustafa.servertest.Activities;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pem.mustafa.servertest.Adapters.BadgeAdapter;
import com.pem.mustafa.servertest.Adapters.DrawerAdapter;
import com.pem.mustafa.servertest.CustomObjects.BadgeItemObject;
import com.pem.mustafa.servertest.Other.LoginState;
import com.pem.mustafa.servertest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BadgeListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String serverAddress = LoginState.getServerAdress();
    private static final String clientType = "1";
    private static Context applicationContext;
    private static final String TAG = BadgeListActivity.class.getName();

    private List<BadgeItemObject> mItems;    // GridView items list
    private BadgeAdapter mAdapter;
    //private DrawerAdapter mDrawerAdapter;
    private GridView gridView;

    ////
    /*private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private LinearLayoutManager mLayoutManager;*/
    /////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.badgetoolbar);
        setSupportActionBar(toolbar);

        applicationContext = this.getApplicationContext();

        gridView = (GridView) findViewById(R.id.badgegridview);

        mItems = new ArrayList<BadgeItemObject>();

        getBadgeList();

    }


    private void getBadgeList()
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

                            JSONArray badgeList = responseObject.getJSONArray("badgelist");

                            // there exists at least one badge
                            if(badgeList.length() > 0)
                            {
                                for(int i=0 ; i < badgeList.length() ; i++)
                                {
                                    String title = badgeList.getString(i);
                                    Log.d(TAG, "Title of this badges is " + title);
                                    mItems.add(new BadgeItemObject(title, resources.getDrawable(R.drawable.ic_default_badge)));
                                }
                                mAdapter = new BadgeAdapter(getApplicationContext(), mItems);
                                gridView.setAdapter(mAdapter);
                                gridView.setOnItemClickListener(BadgeListActivity.this);

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Couldn't get badges(json)", Toast.LENGTH_SHORT).show();
                        }



                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        //responseTv.setText("Login Failed \n" + error.toString());
                        Toast.makeText(getApplicationContext(), "Couldn't get badges(server)", Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(BadgeListActivity.this).add(jsonRequest);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // retrieve the GridView item
        BadgeItemObject item = mItems.get(position);

        // do something
        Toast.makeText(getApplicationContext(), "You have earned " + item.title + " Badge", Toast.LENGTH_SHORT).show();
    }
}
