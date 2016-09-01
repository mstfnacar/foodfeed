package com.pem.mustafa.servertest.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.os.Handler;
import android.widget.ViewAnimator;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pem.mustafa.servertest.Other.LoginState;
import com.pem.mustafa.servertest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private static final String serverAddress = LoginState.getServerAdress();
    private static final String clientType = "1";

    public Button loginButton;
    public Button registerButton;
    public EditText usernameEt;
    public EditText passwordEt;
    public EditText passwordCheckEt;
    public static EditText emailEt;
    public EditText regusernameEt;
    public EditText regpasswordEt;

    public LinearLayout loginProgressWheel;
    public LinearLayout registerProgressWheel;


    public LinearLayout tologinnavigator;
    public LinearLayout toregisternavigator;
    private ViewAnimator viewAnimator;
    private ViewAnimator credentialsanimator;



    Animation fade_in, fade_out, slide_left, slide_right;

    private static Context appContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appContext = this.getApplicationContext();

        loginButton = (Button) findViewById(R.id.loginButton);
        registerButton = (Button) findViewById(R.id.registerButton);
        usernameEt = (EditText) findViewById(R.id.usernameEt);
        passwordEt = (EditText) findViewById(R.id.passwordEt);
        regusernameEt = (EditText) findViewById(R.id.regusernameEt);
        regpasswordEt = (EditText) findViewById(R.id.regpasswordEt);
        passwordCheckEt = (EditText) findViewById(R.id.passwordCheckEt);
        emailEt = (EditText) findViewById(R.id.emailEt);

        loginProgressWheel = (LinearLayout) findViewById(R.id.mainloginprogresswheel);
        registerProgressWheel = (LinearLayout) findViewById(R.id.mainregisterprogresswheel);

        loginProgressWheel.setVisibility(View.GONE);
        registerProgressWheel.setVisibility(View.GONE);


        tologinnavigator = (LinearLayout) findViewById(R.id.tologintext);
        toregisternavigator = (LinearLayout) findViewById(R.id.toregistertext);

        viewAnimator = (ViewAnimator) findViewById(R.id.loginviewanimator);
        credentialsanimator = (ViewAnimator) findViewById(R.id.credentialsanimator);

        fade_in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

        slide_left = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        slide_right = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        viewAnimator.setInAnimation(fade_in);
        viewAnimator.setOutAnimation(fade_out);

        credentialsanimator.setInAnimation(fade_in);
        credentialsanimator.setOutAnimation(fade_out);



    }

    public void loginRadioPressed(View view)
    {
        //TransitionManager.beginDelayedTransition(tologinnavigator, new Fade());
        viewAnimator.showPrevious();
        //tologinnavigator.setVisibility(View.GONE);
        //TransitionManager.beginDelayedTransition(toregisternavigator, new Fade());
        //toregisternavigator.setVisibility(View.VISIBLE);
        credentialsanimator.showPrevious();


    }

    public void registerRadioPressed(View view)
    {
        //TransitionManager.beginDelayedTransition(toregisternavigator, new Fade());
        //toregisternavigator.setVisibility(View.GONE);
        //TransitionManager.beginDelayedTransition(tologinnavigator, new Fade());
        //tologinnavigator.setVisibility(View.VISIBLE);
        viewAnimator.showNext();

        credentialsanimator.showNext();

    }

    public void onLoginButtonPressed(View view)
    {
        loginButton.setClickable(false);
        loginButton.setVisibility(View.INVISIBLE);
        loginProgressWheel.setVisibility(View.VISIBLE);

        String usernameStr = usernameEt.getText().toString();
        String passwordStr = passwordEt.getText().toString();

        if (usernameStr.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Enter a username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (passwordStr.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Enter a password", Toast.LENGTH_SHORT).show();
            return;
        }

        usernameStr = usernameStr.replace(' ', '+').trim();
        passwordStr = passwordStr.trim();

        String url = "http://" + serverAddress + "/userlogin/?username="+ usernameStr+"&password=" + passwordStr + "&client_type=" + clientType;



        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            String nameOfUser = response.getString("username");
                            nameOfUser = nameOfUser.replace(' ', '+');
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                            LoginState.userLogin(appContext);
                            LoginState.setUserInfo(appContext, nameOfUser);
                            setProfilePhoto(nameOfUser);




                        } catch (JSONException e) {
                            e.printStackTrace();
                            //responseTv.setText("Login Failed \n" + e.toString());
                            Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                            loginButton.setClickable(true);
                            loginButton.setVisibility(View.VISIBLE);
                            loginProgressWheel.setVisibility(View.INVISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        //responseTv.setText("Login Failed \n" + error.toString());
                        Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                        loginButton.setClickable(true);
                        loginButton.setVisibility(View.VISIBLE);
                        loginProgressWheel.setVisibility(View.INVISIBLE);
                    }
                });

        Volley.newRequestQueue(MainActivity.this).add(jsonRequest);
    }

    public void onRegisterButtonPressed(View view)
    {
        registerButton.setClickable(false);
        registerButton.setVisibility(View.INVISIBLE);
        registerProgressWheel.setVisibility(View.VISIBLE);

        String usernameStr = regusernameEt.getText().toString();
        String passwordStr = regpasswordEt.getText().toString();
        String passwordCheckStr = passwordCheckEt.getText().toString();
        String emailStr = emailEt.getText().toString();

        if (usernameStr.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Enter a username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (passwordStr.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Enter a password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (emailStr.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Enter an email adress", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!checkEmail(emailStr))
        {
            Toast.makeText(getApplicationContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!passwordStr.equals(passwordCheckStr))
        {
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        usernameStr = usernameStr.replace(' ', '+').trim();
        emailStr = emailStr.trim();
        passwordStr = passwordStr.trim();

        String url = "http://" + serverAddress + "/userregister/?username="+ usernameStr+"&password=" + passwordStr+"&email="+ emailStr + "&client_type=" + clientType;



        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            String nameOfUser = response.getString("username");
                            nameOfUser = nameOfUser.replace(' ', '+');
                            Toast.makeText(getApplicationContext(), "Register Successful", Toast.LENGTH_SHORT).show();
                            LoginState.userLogin(appContext);
                            LoginState.setUserInfo(appContext, nameOfUser);
                            setProfilePhoto(nameOfUser);




                        } catch (JSONException e) {
                            e.printStackTrace();
                            //responseTv.setText("Register Failed, try again \n" + e.toString());
                            Toast.makeText(getApplicationContext(), "Register Failed", Toast.LENGTH_SHORT).show();
                            registerButton.setClickable(true);
                            registerButton.setVisibility(View.VISIBLE);
                            registerProgressWheel.setVisibility(View.INVISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        //responseTv.setText("Register Failed, try again \n" + error.toString());
                        Toast.makeText(getApplicationContext(), "Register Failed", Toast.LENGTH_SHORT).show();
                        registerButton.setClickable(true);
                        registerButton.setVisibility(View.VISIBLE);
                        registerProgressWheel.setVisibility(View.INVISIBLE);
                    }
                });

        Volley.newRequestQueue(MainActivity.this).add(jsonRequest);
    }

    private boolean checkEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void setProfilePhoto(String username)
    {
        //username = username.replace(' ', '+');
        String url = "http://" + serverAddress + "/showphoto/?client_type=" + clientType
                + "&username=" + username;

        JsonArrayRequest jsonRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray responseArray) {
                        // the response is already constructed as a JSONObject!

                        try {

                            if (responseArray.length() > 0) {
                                JSONObject responseObj = responseArray.getJSONObject(0);
                                String photoUrl = responseObj.getString("photo");

                                LoginState.setHasPhoto(appContext, 1, photoUrl);

                            } else {
                                LoginState.setHasPhoto(appContext, 0, "");
                            }


                        } catch (JSONException e) {
                            //e.printStackTrace();
                        }finally {

                            loginButton.setClickable(true);
                            loginButton.setVisibility(View.VISIBLE);
                            loginProgressWheel.setVisibility(View.INVISIBLE);

                            registerButton.setClickable(true);
                            registerButton.setVisibility(View.VISIBLE);
                            registerProgressWheel.setVisibility(View.INVISIBLE);

                            Intent newIntent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(newIntent);
                            finish();
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                    }
                });

        Volley.newRequestQueue(MainActivity.this).add(jsonRequest);

    }






}
