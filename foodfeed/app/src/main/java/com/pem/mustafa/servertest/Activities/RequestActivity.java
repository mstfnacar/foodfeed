package com.pem.mustafa.servertest.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.pem.mustafa.servertest.Adapters.ProfileRequestFeedAdapter;
import com.pem.mustafa.servertest.CustomObjects.RequestDataObject;
import com.pem.mustafa.servertest.Other.LoginState;
import com.pem.mustafa.servertest.R;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RequestActivity extends AppCompatActivity {

    private static final String serverAddress = LoginState.getServerAdress();
    private static final String clientType = "1";
    private static Context applicationContext;
    private static final String TAG = ProfileActivity.class.getName();

    private static String username;
    private static String title;
    private static String description;

    private ProgressWheel progressWheel;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList results = new ArrayList<RequestDataObject>();
    private int adapterState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        applicationContext = this.getApplicationContext();

        progressWheel = (ProgressWheel) findViewById(R.id.requestprogresswheel);
        mRecyclerView = (RecyclerView) findViewById(R.id.requestrecyclerview);

    }







    private void getDataSet()
    {
        // getrequests/?client_type=1&username=mustafa&request_type=0
        username  = LoginState.getUserInfo(applicationContext);
        String url = "http://" + serverAddress + "/approved/?username=" + username;

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
                        if(adapterState != results.size()) {
                            adapterState = results.size();
                            //Log.d(TAG, "" + results.size());
                            ArrayList<RequestDataObject> reversed = new ArrayList<>();
                            for (int i = results.size() - 1; i >= 0; i--)
                                reversed.add((RequestDataObject) results.get(i));
                            mAdapter = new ProfileRequestFeedAdapter(reversed, applicationContext);
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
                        progressWheel.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                });

        Volley.newRequestQueue(RequestActivity.this).add(jsonRequest);
        //return results;
    }


}
