package com.pem.mustafa.servertest.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.pem.mustafa.servertest.Adapters.DrawerAdapter;
import com.pem.mustafa.servertest.Adapters.MainFeedAdapter;
import com.pem.mustafa.servertest.CustomObjects.DataObject;
import com.pem.mustafa.servertest.CustomObjects.TopTenDataObj;
import com.pem.mustafa.servertest.GcmFiles.QuickstartPreferences;
import com.pem.mustafa.servertest.GcmFiles.RegistrationIntentService;
import com.pem.mustafa.servertest.Other.CustomDividerItemDecoration;
import com.pem.mustafa.servertest.Other.DividerItemDecoration;
import com.pem.mustafa.servertest.Other.LoginState;
import com.pem.mustafa.servertest.R;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class LoginActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = LoginActivity.class.getName();
    private static final String serverAddress = LoginState.getServerAdress();
    private static final String clientType = "1";
    private static Context applicationContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView toolbarTitleTv;
    private ArrayList results = new ArrayList<DataObject>();
    private int adapterState = 0;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressWheel progressWheel;
    private CoordinatorLayout recyclerLayout;
    private LinearLayout mainv2progresswheel;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;

    private DrawerAdapter mDrawerAdapter;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private LinearLayoutManager mDrawerLayoutManager;
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerAdapter mRightDrawerAdapter;
    private DrawerLayout mRightDrawerLayout;
    private LinearLayoutManager mRightDrawerLayoutManager;
    private ActionBarDrawerToggle mRightDrawerToggle;
    private RelativeLayout mRightDrawerList;
    private ImageView rightDrawerTuneButton;
    private boolean isRightDrawerOpen = false;

    private SeekBar distanceFilterBar;
    private TextView distanceBarProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.maintoolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        toolbar.setNavigationIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_white_24dp, null));


        toolbarTitleTv = (TextView) findViewById(R.id.main_toolbar_title);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/cookies&milk-regular.ttf");

        toolbarTitleTv.setTypeface(custom_font);

        recyclerLayout = (CoordinatorLayout) findViewById(R.id.mainrecyclerlayout);
        recyclerLayout.setVisibility(View.GONE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(LoginActivity.this, PostActivity.class);
                startActivity(newIntent);
            }
        });

        applicationContext = this.getApplicationContext();

        progressWheel = (ProgressWheel) findViewById(R.id.mainprogresswheel);
        progressWheel.setVisibility(View.VISIBLE);

        mainv2progresswheel = (LinearLayout) findViewById(R.id.mainv2logoutprogresswheel);
        mainv2progresswheel.setVisibility(View.GONE);
        //wheel.setBarColor(Color.BLUE);

        mRecyclerView = (RecyclerView) findViewById(R.id.feedRecyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mainswiperefresh);

        mSwipeRefreshLayout.setColorSchemeColors(R.color.swipecolor1, R.color.swipecolor2);

        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MainFeedAdapter(results, applicationContext);
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //mRecyclerView.addItemDecoration(new CustomDividerItemDecoration(this));

        //// drawer settings

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_mainactivity);
        mDrawerList = (RecyclerView) findViewById(R.id.left_drawer_mainactivity);

        mRightDrawerLayout = (DrawerLayout) findViewById(R.id.rightdrawer_layout_mainactivity);
        mRightDrawerList = (RelativeLayout) findViewById(R.id.rightdrawer_item_rl);
        rightDrawerTuneButton = (ImageView) findViewById(R.id.main_toolbar_tunebutton);
        isRightDrawerOpen = false;

        LinkedHashMap adapterMapData = new LinkedHashMap();
        adapterMapData.put("My Profile", "ProfileActivity");
        adapterMapData.put("Recommend me", "RecommendationActivity");
        adapterMapData.put("Food search", "SearchActivity");
        adapterMapData.put("User search", "UserSearchActivity");
        adapterMapData.put("Favorites", "FavoritesActivity");
        adapterMapData.put("Messages", "MessageBoxActivity");
        adapterMapData.put("Order history", "OrderHistoryActivity");
        adapterMapData.put("Friend requests", "FriendRequestsActivity");
        adapterMapData.put("Top 10", "TopTenListActivity");

        HashMap<String, Integer > iconMap = new HashMap<>();
        iconMap.put("My Profile", R.drawable.ic_person_white_48dp);
        iconMap.put("Recommend me", R.drawable.ic_check_circle_white_36dp);
        iconMap.put("Food search", R.drawable.ic_search_white_24dp);
        iconMap.put("User search", R.drawable.ic_search_white_24dp);
        iconMap.put("Favorites", R.drawable.ic_favorite_border_white_24dp);
        iconMap.put("Messages", R.drawable.ic_question_answer_white_24dp);
        iconMap.put("Order history", R.drawable.ic_history_white_24dp);
        iconMap.put("Friend requests", R.drawable.ic_add_white_24dp);
        iconMap.put("Top 10", R.drawable.ic_crown_white_24dp);

        mDrawerAdapter = new DrawerAdapter(adapterMapData, iconMap, applicationContext);
        mDrawerList.setHasFixedSize(true);
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerLayoutManager = new LinearLayoutManager(this);
        mDrawerList.setLayoutManager(mDrawerLayoutManager);

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mRightDrawerLayout.closeDrawer(mRightDrawerList);
                isRightDrawerOpen = false;
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ){

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };


        mRightDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

                if(LoginState.isIntervalChanged(applicationContext) == 1)
                {
                    LoginState.setLocationIntervalChange(applicationContext, 0);
                    progressWheel.setVisibility(View.VISIBLE);
                    recyclerLayout.setVisibility(View.GONE);
                    getDataSet();
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        ///

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    //Toast.makeText(LoginActivity.this, "Token retrieved and sent to server!", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(LoginActivity.this, "An error occurred while either fetching the InstanceID token,\n" +
                            //"sending the fetched token to the server or subscribing to the PubSub topic.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        registerReceiver();

        if (checkPlayServices()) {
            Log.d(TAG, "GETTING TOKEN...");
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshRecyclerViewOnSwipe();
            }
        });


        // distance filter seekbar settings start here

        distanceFilterBar = (SeekBar) findViewById(R.id.drawer_seekbar);
        distanceBarProgress = (TextView) findViewById(R.id.drawer_seekbar_progress);

        distanceFilterBar.setProgress(LoginState.getLocationInterval(applicationContext));

        distanceFilterBar.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int action = event.getAction();
                switch (action)
                {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow Drawer to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow Drawer to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle seekbar touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        distanceFilterBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressVal = LoginState.getLocationInterval(applicationContext);
            int changeCheck = progressVal;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressVal = progress;
                distanceBarProgress.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                p.addRule(RelativeLayout.ABOVE, seekBar.getId());
                Rect thumbRect = seekBar.getThumb().getBounds();
                p.setMargins(
                        thumbRect.centerX(),0, 0, 0);
                distanceBarProgress.setLayoutParams(p);
                distanceBarProgress.setText(String.valueOf(progress) + " km.");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Log.d(TAG, "Started tracking");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //distanceTv.setText(String.valueOf(progressVal));
                if(progressVal != changeCheck)
                {
                    LoginState.setLocationInterval(applicationContext, progressVal);
                    LoginState.setLocationIntervalChange(applicationContext, 1);

                }
                else
                {
                    LoginState.setLocationIntervalChange(applicationContext, 0);
                }
                distanceBarProgress.setVisibility(View.INVISIBLE);
                //Log.d(TAG, "Distance is set to : " + progressVal);
            }
        });



    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //runnable.run();
        mDrawerLayout.closeDrawer(mDrawerList);
        mRightDrawerLayout.closeDrawer(mRightDrawerList);
        registerReceiver();
        progressWheel.setVisibility(View.VISIBLE);
        recyclerLayout.setVisibility(View.GONE);
        results.clear();
        adapterState = 0;
        mAdapter = new MainFeedAdapter(results, applicationContext);;
        mRecyclerView.setAdapter(mAdapter);
        getDataSet();

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();


    }

    @Override
    protected void onStop() {
        super.onStop();
        //handler.removeCallbacks(runnable);
    }

    private void getDataSet() {

        String url;
        if(LoginState.getHasLocation(applicationContext) == 1)
        {
            Log.d(TAG, "Have VALID LOCATION");
            url = "http://" + serverAddress + "/locationsearch/?client_type=" + clientType +
                            "&latitude=" + LoginState.getLocationLatitude(applicationContext) +
                            "&longitude=" + LoginState.getLocationLongitude(applicationContext) +
                            "&interval=" + LoginState.getLocationInterval(applicationContext);

            //http://46.101.180.126/locationsearch/?client_type=1&latitude=39.911701&longitude=32.840302&interval=10
        }
        else
        {
            Log.d(TAG, "NO VALID LOCATION");
            url = "http://" + serverAddress + "/userposts/?client_type="+clientType;
        }


        JsonArrayRequest jsonRequest = new JsonArrayRequest
            (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray responseArray) {
                    // the response is already constructed as a JSONObject!
                    results.clear();
                    String foodName;
                    String price;
                    String location;
                    String username;
                    int hasPhotoFlag;
                    String url;
                    int userHasPhotoFlag;
                    String userImageUrl;
                    boolean hasPhotoBool = false;
                    boolean userHasPhotoBool = false;
                    int id;
                    //Log.d(TAG, "REFRESHING");
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
                            {
                                hasPhotoBool = false;
                                url = null;
                            }

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

                            DataObject obj = new DataObject(foodName,price,location,username,"Rating "+i, url, hasPhotoBool , id, userHasPhotoBool, userImageUrl );
                            results.add(index, obj);
                            index++;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    if(adapterState != results.size()) {
                        adapterState = results.size();
                        Log.d(TAG, "" + results.size());
                        ArrayList<DataObject> reversed = new ArrayList<>();
                        for (int i = results.size() - 1; i >= 0; i--)
                            reversed.add((DataObject) results.get(i));
                        mAdapter = new MainFeedAdapter(reversed, applicationContext);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                    progressWheel.setVisibility(View.GONE);
                    recyclerLayout.setVisibility(View.VISIBLE);


                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Couldn't get items(server)", Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                    progressWheel.setVisibility(View.GONE);
                    recyclerLayout.setVisibility(View.VISIBLE);
                }
            });

        Volley.newRequestQueue(LoginActivity.this).add(jsonRequest);
        //return results;
    }

    private void refreshRecyclerViewOnSwipe() {
        getDataSet();
        //mSwipeRefreshLayout.setRefreshing(false);
    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public void goToSettings(View v) {
        Intent newIntent = new Intent(LoginActivity.this, SettingsActivity.class);
        startActivity(newIntent);
    }

    public void rightDrawerControl(View v) {

        if(!isRightDrawerOpen)
        {
            mRightDrawerLayout.openDrawer(mRightDrawerList);
            isRightDrawerOpen = true;
        }
        else
        {
            mRightDrawerLayout.closeDrawer(mRightDrawerList);
            isRightDrawerOpen = false;
        }
    }

    public void logOutAction(View v){

        mRightDrawerLayout.closeDrawer(mRightDrawerList);
        isRightDrawerOpen = false;
        mainv2progresswheel.setVisibility(View.VISIBLE);

        String url = "http://" + serverAddress + "/registerdevice/?username="+ LoginState.getUserInfo(applicationContext) + "&client_type=" + clientType;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        Log.d(TAG, "Delete succesful");
                        LoginState.userLogout(applicationContext);
                        mainv2progresswheel.setVisibility(View.GONE);
                        Intent newIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(newIntent);
                        finish();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        //responseTv.setText("Login Failed \n" + error.toString());
                        mainv2progresswheel.setVisibility(View.GONE);
                        Log.d(TAG, "Delete failed");
                    }
                });

        Volley.newRequestQueue(LoginActivity.this).add(jsonRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            mainv2progresswheel.setVisibility(View.VISIBLE);
            String url = "http://" + serverAddress + "/registerdevice/?username="+ LoginState.getUserInfo(applicationContext) + "&client_type=" + clientType;

            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // the response is already constructed as a JSONObject!
                            Log.d(TAG, "Delete succesful");
                            LoginState.userLogout(applicationContext);
                            mainv2progresswheel.setVisibility(View.GONE);
                            Intent newIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(newIntent);
                            finish();
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            //responseTv.setText("Login Failed \n" + error.toString());
                            mainv2progresswheel.setVisibility(View.GONE);
                            Log.d(TAG, "Delete failed");
                        }
                    });

            Volley.newRequestQueue(LoginActivity.this).add(jsonRequest);

            /*LoginState.userLogout(applicationContext);
            Intent newIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(newIntent);
            finish();*/

            return true;
        }

        if(id == R.id.action_topten)
        {
            Intent newIntent = new Intent(LoginActivity.this, TopTenListActivity.class);
            startActivity(newIntent);
        }



        return super.onOptionsItemSelected(item);
    }

}
