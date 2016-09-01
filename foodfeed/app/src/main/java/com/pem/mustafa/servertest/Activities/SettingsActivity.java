package com.pem.mustafa.servertest.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pem.mustafa.servertest.Other.LoginState;
import com.pem.mustafa.servertest.R;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsActivity extends AppCompatActivity {

    private Context applicationContext;
    private static final String serverAddress = LoginState.getServerAdress();
    private static final String clientType = "1";
    private static final String TAG = SettingsActivity.class.getName();

    private CheckBox lockCheckBox;
    private ProgressWheel progressWheel;
    private static String username;
    private static int hideStatus = 0;

    private SeekBar distanceBar;
    private TextView distanceTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.settingstoolbar);
        setSupportActionBar(toolbar);

        progressWheel = (ProgressWheel) findViewById(R.id.settingslockprogresswheel);
        progressWheel.setVisibility(View.VISIBLE);

        applicationContext = this.getApplicationContext();

        lockCheckBox = (CheckBox) findViewById(R.id.settingslockradiobutton);
        lockCheckBox.setVisibility(View.GONE);

        distanceBar = (SeekBar) findViewById(R.id.settings_distancebar);
        distanceTv = (TextView) findViewById(R.id.settings_distancetv);

        final int currentDistanceValue = LoginState.getLocationInterval(applicationContext);
        Log.d(TAG, "" + currentDistanceValue);
        distanceTv.setText(String.valueOf(currentDistanceValue));
        distanceBar.setProgress(currentDistanceValue);

        distanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressVal = currentDistanceValue;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressVal = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                distanceTv.setText(String.valueOf(progressVal));
                LoginState.setLocationInterval(applicationContext, progressVal);
            }
        });


        username = LoginState.getUserInfo(applicationContext);

        getLockState();
    }

    private void getLockState()
    {
        String url = "http://" + serverAddress + "/islocked/?client_type=" + clientType
                +"&username=" + username + "&follower="+ username +
                "&comparetype=0" + "&actiontype=0";

        JsonObjectRequest jsonRequest = new JsonObjectRequest
            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject responseObject) {
                // the response is already constructed as a JSONObject!

                try {
                    hideStatus = responseObject.getInt("hideStatus");
                    if(hideStatus == 0)
                        lockCheckBox.setChecked(false);
                    else
                        lockCheckBox.setChecked(true);

                    progressWheel.setVisibility(View.GONE);
                    lockCheckBox.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Couldn't get lock info(json)", Toast.LENGTH_SHORT).show();
                    progressWheel.setVisibility(View.GONE);
                    lockCheckBox.setVisibility(View.VISIBLE);
                }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    //responseTv.setText("Login Failed \n" + error.toString());
                    Toast.makeText(getApplicationContext(), "Couldn't get lock info(server)", Toast.LENGTH_SHORT).show();

                }
            });

        Volley.newRequestQueue(SettingsActivity.this).add(jsonRequest);
    }

    public void changeLockState(View v)
    {
        progressWheel.setVisibility(View.VISIBLE);
        lockCheckBox.setVisibility(View.GONE);
        String url = "http://" + serverAddress + "/islocked/?client_type=" + clientType
                +"&username=" + username + "&follower="+ username +
                "&comparetype=0" + "&actiontype=1";

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject responseObject) {
                        // the response is already constructed as a JSONObject!

                        try {
                            hideStatus = responseObject.getInt("hideStatus");
                            if(hideStatus == 0)
                                lockCheckBox.setChecked(false);
                            else
                                lockCheckBox.setChecked(true);

                            progressWheel.setVisibility(View.GONE);
                            lockCheckBox.setVisibility(View.VISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Couldn't change lock info(json)", Toast.LENGTH_SHORT).show();
                            progressWheel.setVisibility(View.GONE);
                            lockCheckBox.setVisibility(View.VISIBLE);
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        //responseTv.setText("Login Failed \n" + error.toString());
                        Toast.makeText(getApplicationContext(), "Couldn't change lock info(server)", Toast.LENGTH_SHORT).show();

                    }
                });

        Volley.newRequestQueue(SettingsActivity.this).add(jsonRequest);
    }


}
