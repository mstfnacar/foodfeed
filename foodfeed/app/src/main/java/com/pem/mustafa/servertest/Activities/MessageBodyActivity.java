package com.pem.mustafa.servertest.Activities;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pem.mustafa.servertest.Adapters.MessageBodyAdapter;
import com.pem.mustafa.servertest.CustomObjects.MessageObject;
import com.pem.mustafa.servertest.Other.LoginState;
import com.pem.mustafa.servertest.R;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessageBodyActivity extends AppCompatActivity {

    private static final String TAG = MessageBodyActivity.class.getName();
    private static final String serverAddress = LoginState.getServerAdress();
    private static final String clientType = "1";
    private static final int MESSAGE_NOTIFICATION_ID = 0;
    private static Context applicationContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ArrayList results = new ArrayList<MessageObject>();
    private ArrayList finalReversed = new ArrayList<MessageObject>();
    private int adapterState = 0;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EditText messageInputEt;
    private Button sendButton;
    private LinearLayout progressWheel;
    private CoordinatorLayout recyclerLayout;
    //private static String followType = "following";
    private static String username;
    private static String recipient;
    private static int messageListPart = 0;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("newmessage");
            String sender = intent.getStringExtra("sender");
            Log.d("receiver", "Got message: " + message);

            if(recipient.equals(sender))
            {
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(MESSAGE_NOTIFICATION_ID);
                //notificationManager.cancelAll();

                finalReversed.add(new MessageObject(sender, username, message, ""));
                mAdapter.notifyItemInserted(finalReversed.size()-1);
                mRecyclerView.scrollToPosition(finalReversed.size() - 1);
            }


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_body);

        Toolbar toolbar = (Toolbar) findViewById(R.id.messagebodytoolbar);
        setSupportActionBar(toolbar);

        applicationContext = this.getApplicationContext();
        /*emptyListAlert = (TextView) findViewById(R.id.messageboxemptytext);
        emptyListAlert.setVisibility(View.GONE);*/
        username = LoginState.getUserInfo(applicationContext);

        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            recipient = extras.getString("username");
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("broadcast_intent"));

        messageInputEt = (EditText) findViewById(R.id.messagebodyinput);
        sendButton = (Button) findViewById(R.id.messagebodysendbutton);

        recyclerLayout = (CoordinatorLayout) findViewById(R.id.messagebodyrecyclerlayout);
        recyclerLayout.setVisibility(View.GONE);

        progressWheel = (LinearLayout) findViewById(R.id.messagebodyprogresswheel);
        progressWheel.setVisibility(View.VISIBLE);
        //wheel.setBarColor(Color.BLUE);

        mRecyclerView = (RecyclerView) findViewById(R.id.messagebodyrecyclerview);
        //mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.messageboxswiperefresh);

        //mSwipeRefreshLayout.setColorSchemeColors(R.color.swipecolor1, R.color.swipecolor2);

        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MessageBodyAdapter(finalReversed, applicationContext);;
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        //mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        getMessages();

        //runnable.run();

        /*mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshRecyclerViewOnSwipe();
            }
        });*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        //runnable.run();
        //emptyListAlert.setVisibility(View.GONE);
        progressWheel.setVisibility(View.VISIBLE);
        recyclerLayout.setVisibility(View.GONE);
        results.clear();
        finalReversed.clear();
        adapterState = 0;
        mAdapter = new MessageBodyAdapter(finalReversed, applicationContext);;
        mRecyclerView.setAdapter(mAdapter);
        getMessages();
        //Log.d(TAG, "onResume called");
    }



    @Override
    protected void onStop() {
        super.onStop();
        //handler.removeCallbacks(runnable);
    }

    private void getMessages() {

        String url = "http://" + serverAddress + "/getmessages/?current_user="+ username +"&client_type="+clientType+"&other_user=" + recipient + "&part=0";

        //http://46.101.180.126/getmessages/?client_type=0&current_user=mustafa&part=0&other_user=zeynep

        JsonArrayRequest jsonRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray responseArray) {
                        // the response is already constructed as a JSONObject!
                        results.clear();
                        String sender;
                        String reciever;
                        String body;
                        String subject;
                        int id;
                        //Log.d(TAG, "REFRESHING");
                        int index = 0;
                        for(int i=0; i < responseArray.length() ; i++)
                        {
                            try {
                                JSONObject tempObj = responseArray.getJSONObject(i);
                                sender = tempObj.getString("sender");
                                reciever = tempObj.getString("recipient");
                                body = tempObj.getString("body");
                                subject = tempObj.getString("subject");


                                results.add(index, new MessageObject(sender, reciever, body, subject));
                                index++;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        if(adapterState != results.size()) {
                            adapterState = results.size();
                            Log.d(TAG, "" + results.size());

                            for (int i = results.size() - 1; i >= 0; i--)
                                finalReversed.add( (MessageObject) results.get(i));
                            mAdapter = new MessageBodyAdapter(finalReversed, applicationContext);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                        //mSwipeRefreshLayout.setRefreshing(false);
                        progressWheel.setVisibility(View.GONE);
                        recyclerLayout.setVisibility(View.VISIBLE);

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
                        progressWheel.setVisibility(View.GONE);
                        recyclerLayout.setVisibility(View.VISIBLE);

                    }
                });

        Volley.newRequestQueue(MessageBodyActivity.this).add(jsonRequest);
        //return results;
        //progressWheel.setVisibility(View.GONE);
        //recyclerLayout.setVisibility(View.VISIBLE);



    }



    /*private void refreshRecyclerViewOnSwipe()
    {
        getMessages();
        //mSwipeRefreshLayout.setRefreshing(false);
    }*/

    public void sendMessage(View view)
    {
        String message = messageInputEt.getText().toString();
        messageInputEt.setText("");
        if(message.trim().length() == 0)
        {
            //Toast.makeText(getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
        }
        else
        {

            message = message.replace(' ', '+').trim();
            String url = "http://" + serverAddress + "/sendmessage/?client_type=" + clientType
                    +"&current_user=" + username + "&other_user=" + recipient + "&subject="+ "" + "&body=" + message;
            //http://46.101.180.126/sendmessage/?client_type=0&current_user=zeynep&other_user=mustafa&subject=yemek+istegi&body=merhabalar%2C+yemek+alicam

            JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject responseObject) {

                    try {
                        String sender;
                        String reciever;
                        String body;
                        String subject;

                        sender = responseObject.getString("sender");
                        reciever = responseObject.getString("recipient");
                        body = responseObject.getString("body");
                        subject = responseObject.getString("subject");

                        //Log.d(TAG, body + " from " + sender);

                        finalReversed.add(new MessageObject(sender, reciever, body, subject));
                        mAdapter.notifyItemInserted(finalReversed.size()-1);
                        mRecyclerView.scrollToPosition(finalReversed.size()-1);
                        recyclerLayout.setVisibility(View.VISIBLE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Couldn't send message(json)", Toast.LENGTH_SHORT).show();
                    }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    error.printStackTrace();
                    //responseTv.setText("Login Failed \n" + error.toString());
                    Toast.makeText(getApplicationContext(), "Couldn't send message(server)", Toast.LENGTH_SHORT).show();
                    }
                });

            Volley.newRequestQueue(MessageBodyActivity.this).add(jsonRequest);
        }

    }
}
