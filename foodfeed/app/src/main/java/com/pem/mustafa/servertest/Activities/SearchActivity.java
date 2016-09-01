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
import com.pem.mustafa.servertest.Adapters.MainFeedAdapter;
import com.pem.mustafa.servertest.CustomObjects.DataObject;
import com.pem.mustafa.servertest.Other.LoginState;
import com.pem.mustafa.servertest.R;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = SearchActivity.class.getName();
    private static final String serverAddress = LoginState.getServerAdress();
    private static final String clientType = "1";
    private static Context applicationContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private int adapterState = 0;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList results = new ArrayList<DataObject>();
    private ArrayList foodList = new ArrayList<String>();
    private static String searchText;
    private ProgressWheel progressWheel;

    private AutoCompleteTextView searchEt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getFoodList();

        Toolbar toolbar = (Toolbar) findViewById(R.id.searchtoolbar);
        setSupportActionBar(toolbar);

        applicationContext = this.getApplicationContext();

        searchEt = (AutoCompleteTextView) findViewById(R.id.searchtext);
        mRecyclerView = (RecyclerView) findViewById(R.id.searchrecyclerview);
        mRecyclerView.setVisibility(View.GONE);

        progressWheel = (ProgressWheel) findViewById(R.id.searchprogresswheel);
        progressWheel.setVisibility(View.GONE);

        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MainFeedAdapter(results, applicationContext);;
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }




    private void getDataSet()
    {
        String url = "http://" + serverAddress + "/foodsearch/?inputtext=" + searchText;
        results.clear();
        mAdapter = new MainFeedAdapter(results, applicationContext);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.refreshDrawableState();
        JsonArrayRequest jsonRequest = new JsonArrayRequest
            (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray responseArray) {
                    // the response is already constructed as a JSONObject!
                    results.clear();
                    String foodName;
                    String price;
                    String location;
                    String username;// = LoginState.getUserInfo(applicationContext);
                    int hasPhotoFlag;
                    String url;
                    int userHasPhotoFlag;
                    String userImageUrl;
                    boolean hasPhotoBool = false;
                    boolean userHasPhotoBool = false;
                    int id;

                    if(responseArray.length() == 0)
                    {
                        Toast.makeText(getApplicationContext(), "No related post found", Toast.LENGTH_SHORT).show();
                        mAdapter = new MainFeedAdapter(results, applicationContext);
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
                            foodName = tempObj.getString("name");
                            price = tempObj.getString("price");
                            location = tempObj.getString("meetingpoint");
                            id = tempObj.getInt("id");
                            hasPhotoFlag = tempObj.getInt("hasphoto");
                            url = tempObj.getString("photo_url");
                            JSONObject userObj = tempObj.getJSONObject("user");
                            userHasPhotoFlag = userObj.getInt("hasphoto");
                            userImageUrl = userObj.getString("photo_url");

                            if(hasPhotoFlag == 1)
                            {
                                hasPhotoBool = true;
                                url = "http://" + serverAddress + url;
                            }
                            else
                                url = "";

                            if(userHasPhotoFlag == 1)
                            {
                                userHasPhotoBool = true;
                                userImageUrl = "http://" + serverAddress + userImageUrl;
                            }
                            else
                            {
                                userHasPhotoBool = false;
                                userImageUrl = null;
                            }

                            JSONObject tempUser = tempObj.getJSONObject("user");
                            JSONObject tempUser2 = tempUser.getJSONObject("user");
                            username = tempUser2.getString("username");



                            DataObject obj = new DataObject(foodName,price,location,username,"Rating "+i, url, hasPhotoBool, id, userHasPhotoBool, userImageUrl );
                            results.add(index, obj);
                            index++;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    if(adapterState != results.size()) {
                        adapterState = results.size();
                        //Log.d(TAG, "" + results.size());
                        ArrayList<DataObject> reversed = new ArrayList<>();
                        for (int i = results.size() - 1; i >= 0; i--)
                            reversed.add((DataObject) results.get(i));
                        mAdapter = new MainFeedAdapter(reversed, applicationContext);
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
                    mAdapter = new MainFeedAdapter(results, applicationContext);
                    //mAdapter.notifyDataSetChanged();
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.refreshDrawableState();
                    progressWheel.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            });

        Volley.newRequestQueue(SearchActivity.this).add(jsonRequest);
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

        getDataSet();

        // hide keyboard after button press
        View view = SearchActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }


    }

    private void getFoodList() {

        String url = "http://" + serverAddress + "/userposts/?client_type="+clientType;

        JsonArrayRequest jsonRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray responseArray) {
                        // the response is already constructed as a JSONObject!
                        foodList.clear();
                        String foodName;

                        int index = 0;
                        for(int i=0; i < responseArray.length() ; i++)
                        {
                            try {
                                JSONObject tempObj = responseArray.getJSONObject(i);
                                foodName = tempObj.getString("name");
                                if(!foodList.contains(foodName)) {
                                    foodList.add(index, foodName);
                                    index++;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(applicationContext,R.layout.mytext,foodList);
                        searchEt.setAdapter(adapter);




                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Couldn't get items(server)", Toast.LENGTH_SHORT).show();

                    }
                });

        Volley.newRequestQueue(SearchActivity.this).add(jsonRequest);
    }
}
