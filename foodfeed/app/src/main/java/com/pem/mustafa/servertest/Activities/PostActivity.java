package com.pem.mustafa.servertest.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.pem.mustafa.servertest.Other.BaseVolleyRequest;
import com.pem.mustafa.servertest.Other.FetchAddressIntentService;
import com.pem.mustafa.servertest.Other.LocationConstants;
import com.pem.mustafa.servertest.Other.LoginState;
import com.pem.mustafa.servertest.Other.MyFileContentProvider;
import com.pem.mustafa.servertest.Other.VolleySingleton;
import com.pem.mustafa.servertest.R;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class PostActivity extends AppCompatActivity {

    private static final int TAKE_PICTURE = 2;
    private static final String serverAddress = LoginState.getServerAdress();
    private static final String clientType = "1";
    private static final String TAG = PostActivity.class.getName();
    public static String username;
    public static String foodname;
    public static String categoryStr = "Other";

    private EditText nameEt;
    //private EditText categoryEt;
    private EditText meetPointEt;
    private EditText priceEt;
    private EditText amountEt;
    private Button postSubmitButton;
    private LinearLayout submitProgress;
    private Spinner categorySpinner;

    private static Context appContext;

    private Bitmap postBitmap;
    private ImageView postPhoto;

    //// LOCATION VARS ////
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private AddressResultReceiver mResultReceiver;
    String mAddressOutput = "";
    ////


    ////

    String mimeType;
    DataOutputStream dos = null;
    String lineEnd = "\r\n";
    String boundary = "apiclient-" + System.currentTimeMillis();
    String twoHyphens = "--";
    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 1024 * 1024;

    ////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postv2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.posttoolbarv2);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_navigate_before_white_24dp, null));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //set a default value
        postBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.defaultfood);

        appContext = this.getApplicationContext();
        mResultReceiver = new AddressResultReceiver(new Handler());

        username = LoginState.getUserInfo(appContext);
        nameEt = (EditText) findViewById(R.id.postnamev2);
        //categoryEt = (EditText) findViewById(R.id.postCategory);
        meetPointEt = (EditText) findViewById(R.id.postmeetpointv2);
        priceEt = (EditText) findViewById(R.id.postpricev2);
        amountEt = (EditText) findViewById(R.id.postamountv2);
        postSubmitButton = (Button) findViewById(R.id.postsubmitbuttonv2);
        submitProgress = (LinearLayout) findViewById(R.id.postprogressv2);
        postPhoto = (ImageView) findViewById(R.id.postfoodphotov2);
        categorySpinner = (Spinner) findViewById(R.id.postcategoryspinnerv2);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, R.layout.item_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, (String) parent.getItemAtPosition(position));
                if( position > 0)
                    categoryStr = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                categoryStr = "Other";
            }
        });

        submitProgress.setVisibility(View.GONE);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {


                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                        if (ContextCompat.checkSelfPermission(appContext,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ContextCompat.checkSelfPermission(appContext,
                                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.


                            Log.d("Location : ", "PERMISSION DENIED");


                        }
                        else
                        {
                            Log.d("Location : ", "PERMISSION GRANTED");
                            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                    mGoogleApiClient);
                            if (mLastLocation != null) {
                                Log.d("Location : " , String.valueOf(mLastLocation.getLatitude()));
                                Log.d("Location : ", String.valueOf(mLastLocation.getLongitude()));

                                startIntentService();
                            }
                        }




                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d("Location : ", "CONNECTION SUSPENDED");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d("Location : ", "CONNECTION FAILED");
                    }
                })
                .addApi(LocationServices.API)
                .build();
        }

    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void getAdressButtonClick(View view)
    {
        if(mAddressOutput != null)
        {
            meetPointEt.setText(mAddressOutput);
        }
        else
        {
            Toast.makeText(appContext, "No address found" , Toast.LENGTH_SHORT).show();
        }
    }


    public void submitPostWithPhoto(View view)
    {
        postPhoto.setClickable(false);
        //postPhoto.setVisibility(View.GONE);
        //profileImageProgress.setVisibility(View.VISIBLE);
        postSubmitButton.setClickable(false);
        postSubmitButton.setVisibility(View.INVISIBLE);
        submitProgress.setVisibility(View.VISIBLE);


        foodname = nameEt.getText().toString();
        //String category = categoryEt.getText().toString();
        String meetPoint = meetPointEt.getText().toString();
        String price = priceEt.getText().toString();
        String amount = amountEt.getText().toString();

        //HashMap<String, String> params = new HashMap<String, String>();

        if(foodname.trim().length() == 0 || meetPoint.trim().length()==0 || price.trim().length()==0 || amount.trim().length()==0)
        {
            Toast.makeText(getApplicationContext(), "Lütfen tüm kutuları doldurun", Toast.LENGTH_SHORT).show();
            postPhoto.setClickable(true);
            postSubmitButton.setClickable(true);
            postSubmitButton.setVisibility(View.VISIBLE);
            submitProgress.setVisibility(View.INVISIBLE);
            return;
        }

        foodname = foodname.replaceAll("\\s","+");
        //category = category.replaceAll("\\s","+");
        meetPoint = meetPoint.replaceAll("\\s","+");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        postBitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
        final byte[] bitmapData = byteArrayOutputStream.toByteArray();

        String locationLat = "-1";
        String locationLong = "-1";
        int hasLocation = 0;

        if(mLastLocation != null)
        {
            locationLat = String.valueOf(mLastLocation.getLatitude());
            locationLong = String.valueOf(mLastLocation.getLongitude());
            hasLocation = 1;
        }


        String url = "http://" + serverAddress + "/posttofeed/?username="+ username +
                "&client_type="+clientType+
                "&name="+foodname+
                "&category="+categoryStr+
                "&meetingpoint="+meetPoint+
                "&price="+price+
                "&amount="+amount+
                "&isactive=1"+
                "&latitude="+locationLat+
                "&longitude="+locationLong+
                "&haslocation="+hasLocation;

        mimeType = "multipart/form-data;boundary=" + boundary;

        BaseVolleyRequest baseVolleyRequest = new BaseVolleyRequest(1, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                Toast.makeText(PostActivity.this, "Food photo added", Toast.LENGTH_SHORT).show();
                postPhoto.setClickable(true);
                //postPhoto.setVisibility(View.VISIBLE);
                //profileImageProgress.setVisibility(View.GONE);
                finish();
                Log.d(TAG, response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PostActivity.this, "An error occured at photo upload", Toast.LENGTH_SHORT).show();
                postPhoto.setClickable(true);
                postSubmitButton.setClickable(true);
                postSubmitButton.setVisibility(View.VISIBLE);
                submitProgress.setVisibility(View.GONE);
                //postPhoto.setVisibility(View.VISIBLE);
                //profileImageProgress.setVisibility(View.GONE);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return mimeType;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                dos = new DataOutputStream(bos);
                try {
                    long millis = System.currentTimeMillis() ;
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"FoodPhoto\";filename=\""
                            + username + "_"+ foodname + millis +".jpeg" + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(bitmapData);
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    return bos.toByteArray();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bitmapData;
            }
        };

        VolleySingleton.getInstance(appContext).addToRequestQueue(baseVolleyRequest);

    }

    public void addFoodPhoto(View v)
    {
        //Crop.pickImage(PostActivity.this);

        String actions[] = new String[2];
        actions[0] = "From camera";
        actions[1] = "From gallery";

        AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
        builder.setTitle("Pick a method to add photo")
                .setItems(actions, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        //Toast.makeText(appContext, "Picked " + which, Toast.LENGTH_SHORT).show();
                        // from camera
                        if(which == 0)
                        {
                            PackageManager pm = getPackageManager();

                            if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                                Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                i.putExtra(MediaStore.EXTRA_OUTPUT, MyFileContentProvider.CONTENT_URI);
                                startActivityForResult(i, TAKE_PICTURE);
                            } else {
                                Toast.makeText(getBaseContext(), "Camera is not available", Toast.LENGTH_LONG).show();
                            }
                        }
                        // from gallery
                        else if(which == 1)
                        {
                            Crop.pickImage(PostActivity.this);
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

        /*PackageManager pm = getPackageManager();

        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            i.putExtra(MediaStore.EXTRA_OUTPUT, MyFileContentProvider.CONTENT_URI);
            startActivityForResult(i, TAKE_PICTURE);
        } else {
            Toast.makeText(getBaseContext(), "Camera is not available", Toast.LENGTH_LONG).show();
        }*/

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case Crop.REQUEST_PICK: {
                if (resultCode == RESULT_OK) {
                    postPhoto.setClickable(false);
                    //postPhoto.setVisibility(View.GONE);
                    //profileImageProgress.setVisibility(View.VISIBLE);

                    Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
                    //new Crop(data.getData()).output(outputUri).asSquare().start(this);
                    Crop.of(data.getData(), outputUri).asSquare().start(this);
                    Log.d(TAG, "REQUEST_PICK returned");
                    postPhoto.setImageURI(null);//necessary
                    postPhoto.setImageURI(Crop.getOutput(data));

                    postPhoto.setClickable(true);
                    //profileImage.setVisibility(View.VISIBLE);
                    //profileImageProgress.setVisibility(View.GONE);

                }
                break;
            }
            case Crop.REQUEST_CROP: {
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "REQUEST_CROP returned");
                    postPhoto.setImageURI(null);//necessary
                    postPhoto.setImageURI(Crop.getOutput(data));

                    Uri filePath = Crop.getOutput(data);

                    try {
                        //Getting the Bitmap from Gallery
                        postBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                        //setProfilePhoto();
                        //uploadProfilePhoto();
                        //Setting the Bitmap to ImageView
                        //imageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (resultCode == Crop.RESULT_ERROR) {
                    Toast.makeText(this, Crop.getError(data).getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case TAKE_PICTURE: {

                if (resultCode == RESULT_OK) {

                    File out = new File(getFilesDir(), "newImage.jpg");
                    if (!out.exists()) {
                        Toast.makeText(getBaseContext(), "Error while capturing image", Toast.LENGTH_SHORT).show();
                    }

                    Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
                    Uri outputFileUri = Uri.fromFile(out);
                    Crop.of(outputFileUri, outputUri).asSquare().start(this);
                    break;

                }
            }
        }
    }

    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(LocationConstants.RECEIVER, mResultReceiver);
        intent.putExtra(LocationConstants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }


    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            mAddressOutput = resultData.getString(LocationConstants.RESULT_DATA_KEY);

            if(mAddressOutput != null)
            {
                Log.d(TAG, "FINAL LOCATION IS : " + mAddressOutput);
            }

            else
            {
                Log.d(TAG, "FINAL LOCATION IS : No address found" );
            }


            if(mLastLocation != null)
            {
                //latitudeTv.setText("Latitude is : " + String.valueOf(mLastLocation.getLatitude()));
                //longitudeTv.setText("Longitude is : " + String.valueOf(mLastLocation.getLongitude()));
            }

            // Show a toast message if an address was found.
            if (resultCode == LocationConstants.SUCCESS_RESULT) {
                Log.d("Location : " , "Adress found at onReceiveResult");
            }

        }
    }



}


