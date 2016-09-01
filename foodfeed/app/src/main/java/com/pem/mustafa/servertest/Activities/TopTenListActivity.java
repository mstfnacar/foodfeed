package com.pem.mustafa.servertest.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pem.mustafa.servertest.Adapters.FollowAdapter;
import com.pem.mustafa.servertest.Adapters.MainFeedAdapter;
import com.pem.mustafa.servertest.Adapters.TopTenAdapter;
import com.pem.mustafa.servertest.CustomObjects.DataObject;
import com.pem.mustafa.servertest.CustomObjects.TopTenDataObj;
import com.pem.mustafa.servertest.Other.LoginState;
import com.pem.mustafa.servertest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TopTenListActivity extends AppCompatActivity {

    private static final String serverAddress = LoginState.getServerAdress();
    private static final String clientType = "1";
    private RecyclerView chefRecyclerView;
    private RecyclerView gourmetRecyclerView;
    private RecyclerView.Adapter mChefAdapter;
    private RecyclerView.LayoutManager mChefLayoutManager;
    private RecyclerView.Adapter mGourmetAdapter;
    private RecyclerView.LayoutManager mGourmetLayoutManager;
    private static Context applicationContext;
    private ArrayList<TopTenDataObj> chefList = new ArrayList<>();
    private ArrayList<TopTenDataObj> gourmetList = new ArrayList<>();
    private static int chefAdapterState = 0;
    private static int gourmetAdapterState = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_ten_list);

        applicationContext = this.getApplicationContext();

        chefRecyclerView = (RecyclerView) findViewById(R.id.toptencheflist);
        gourmetRecyclerView = (RecyclerView) findViewById(R.id.toptengourmetlist);



        chefRecyclerView.setHasFixedSize(true);
        mChefAdapter = new TopTenAdapter(chefList, gourmetList, 0, applicationContext);;
        chefRecyclerView.setAdapter(mChefAdapter);
        mChefLayoutManager = new LinearLayoutManager(this);
        chefRecyclerView.setLayoutManager(mChefLayoutManager);

        gourmetRecyclerView.setHasFixedSize(true);
        mGourmetAdapter = new TopTenAdapter(chefList, gourmetList, 1, applicationContext);;;
        gourmetRecyclerView.setAdapter(mGourmetAdapter);
        mGourmetLayoutManager = new LinearLayoutManager(this);
        gourmetRecyclerView.setLayoutManager(mGourmetLayoutManager);




    }

    @Override
    protected void onResume() {
        super.onResume();

        chefList.clear();
        gourmetList.clear();



        chefAdapterState = 0;
        gourmetAdapterState = 0;

        mChefAdapter = new TopTenAdapter(chefList, gourmetList, 0, applicationContext);
        chefRecyclerView.setAdapter(mChefAdapter);

        mGourmetAdapter = new TopTenAdapter(chefList, gourmetList, 1, applicationContext);
        gourmetRecyclerView.setAdapter(mGourmetAdapter);

        getDataSet();
    }

    private void getDataSet()
    {

        String url = "http://" + serverAddress + "/gettopten/?client_type="+clientType;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject responseObject) {
                        // the response is already constructed as a JSONObject!

                        try
                        {
                            JSONArray eaters = responseObject.getJSONArray("eaters");
                            for(int i=0 ; i < eaters.length() ; i++)
                            {
                                JSONObject upiObj = eaters.getJSONObject(i);
                                JSONObject tempObj = upiObj.getJSONObject("user");
                                JSONObject userObj = tempObj.getJSONObject("user");

                                String username = userObj.getString("username");
                                String rank = eaters.getJSONObject(i).getString("eatcoins");
                                String title = eaters.getJSONObject(i).getString("usertitle");
                                int hasPhotoFlag = tempObj.getInt("hasphoto");
                                //Log.d("DEBUG MODE : ", username + " :: " + hasPhotoFlag);
                                if(hasPhotoFlag == 1){
                                    String url = "http://" + serverAddress + tempObj.getString("photo_url");
                                    gourmetList.add(new TopTenDataObj(username, rank, title, true, url));
                                }
                                else
                                {
                                    gourmetList.add(new TopTenDataObj(username, rank, title, false, ""));
                                }
                            }

                            JSONArray sellers = responseObject.getJSONArray("sellers");
                            for(int i=0 ; i < sellers.length() ; i++)
                            {
                                JSONObject upiObj = sellers.getJSONObject(i);
                                JSONObject tempObj = upiObj.getJSONObject("user");
                                JSONObject userObj = tempObj.getJSONObject("user");

                                String username = userObj.getString("username");
                                String rank = sellers.getJSONObject(i).getString("sellcoins");
                                String title = sellers.getJSONObject(i).getString("usertitle");
                                int hasPhotoFlag = tempObj.getInt("hasphoto");
                                //Log.d("DEBUG MODE : ", username + " :: " + hasPhotoFlag);
                                if(hasPhotoFlag == 1){
                                    String url = "http://" + serverAddress + tempObj.getString("photo_url");
                                    //Log.d("DEBUG URL MODE : ", username + " :: " + hasPhotoFlag + " :: " + url);

                                    chefList.add(new TopTenDataObj(username, rank, title, true, url));
                                }
                                else
                                {
                                    chefList.add(new TopTenDataObj(username, rank, title, false, ""));
                                }

                            }




                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Couldn't get follow counts(json)", Toast.LENGTH_SHORT).show();
                        }

                        if(chefAdapterState != chefList.size()) {
                            chefAdapterState = chefList.size();
                            /*for (int i = chefList.size() - 1; i >= 0; i--)
                                chefsReversed.add((TopTenDataObj) chefList.get(i));*/
                            mChefAdapter = new TopTenAdapter(chefList, gourmetList, 0, applicationContext);
                            chefRecyclerView.setAdapter(mChefAdapter);
                        }

                        if(gourmetAdapterState != gourmetList.size()) {
                            gourmetAdapterState = gourmetList.size();
                            /*for (int i = gourmetList.size() - 1; i >= 0; i--)
                                gourmetsReversed.add((TopTenDataObj) gourmetList.get(i));*/
                            mGourmetAdapter = new TopTenAdapter(chefList, gourmetList, 1, applicationContext);
                            gourmetRecyclerView.setAdapter(mGourmetAdapter);
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

        Volley.newRequestQueue(TopTenListActivity.this).add(jsonRequest);

    }
}
