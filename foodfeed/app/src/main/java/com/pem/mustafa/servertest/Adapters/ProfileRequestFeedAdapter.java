package com.pem.mustafa.servertest.Adapters;

import android.content.Context;
import android.view.View;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pem.mustafa.servertest.Other.LoginState;
import com.pem.mustafa.servertest.R;
import com.pem.mustafa.servertest.CustomObjects.RequestDataObject;

import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileRequestFeedAdapter extends RecyclerView.Adapter<ProfileRequestFeedAdapter.DataObjectHolder> {

    private static String LOG_TAG = "ProfileRequestFeedAdapter";
    private static final String serverAddress = LoginState.getServerAdress();
    private static final String clientType = "1";
    private static ArrayList<RequestDataObject> mDataset;
    private static MyClickListener myClickListener;
    private static Context appContext;


    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView foodname;
        TextView price;
        TextView username;
        Button acceptBttn;
        Button declineBttn;

        public DataObjectHolder(View itemView) {
            super(itemView);
            foodname = (TextView) itemView.findViewById(R.id.profilerequestv2foodName);
            price = (TextView) itemView.findViewById(R.id.profilerequestv2foodprice);
            username = (TextView) itemView.findViewById(R.id.profilerequestv2username);
            acceptBttn = (Button) itemView.findViewById(R.id.profilerequestv2acceptbutton);
            declineBttn = (Button) itemView.findViewById(R.id.profilerequestv2declinebutton);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //myClickListener.onItemClick(getPosition(), v);
            int pos = this.getAdapterPosition();
            //Toast.makeText(appContext, "Pressed : " + mDataset.get(pos).getFoodname(), Toast.LENGTH_SHORT).show();
            /*Intent toDetail = new Intent(appContext, PostDetailActivity.class);
            toDetail.putExtra("foodname", mDataset.get(pos).getFoodname() );
            toDetail.putExtra("price", mDataset.get(pos).getPrice() );
            toDetail.putExtra("location", mDataset.get(pos).getLocation());
            toDetail.putExtra("username", mDataset.get(pos).getUsername() );
            toDetail.putExtra("rating", mDataset.get(pos).getRating());
            toDetail.putExtra("foodid", mDataset.get(pos).getFoodId());

            v.getContext().startActivity(toDetail);*/


        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public ProfileRequestFeedAdapter(ArrayList<RequestDataObject> myDataset, Context appContext) {
        this.mDataset = myDataset;
        this.appContext = appContext;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_profilerequestfeedv2, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }


    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {
        holder.foodname.setText("for " + mDataset.get(holder.getAdapterPosition()).getFoodname());
        //holder.price.setText(mDataset.get(position).getPrice() + " TL");
        holder.username.setText(mDataset.get(holder.getAdapterPosition()).getUsername());

        holder.acceptBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.acceptBttn.setClickable(false);
                holder.declineBttn.setClickable(false);
                //Toast.makeText(appContext, "Accepted", Toast.LENGTH_SHORT).show();

                //holder.acceptBttn.setText("Approved");
                //holder.acceptBttn.setBackgroundColor(Color.GREEN);

                String url = "http://" + serverAddress + "/approval/?state=0" +
                        "&client_type=" + clientType +
                        "&request_key=" + mDataset.get(holder.getAdapterPosition()).getRequestId();

                JsonObjectRequest jsonRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // the response is already constructed as a JSONObject!
                                Toast.makeText(appContext, "Request Accepted", Toast.LENGTH_SHORT).show();
                                //holder.acceptBttn.setText("Approved");
                                //holder.acceptBttn.setBackgroundColor(Color.GREEN);
                                //Log.d(LOG_TAG, "Adapter POSITION IS " + holder.getAdapterPosition());
                                //Log.d(LOG_TAG, "Layout POSITION IS " + holder.getLayoutPosition());
                                ProfileRequestFeedAdapter.this.deleteItem(holder.getAdapterPosition());


                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                holder.acceptBttn.setClickable(true);
                                holder.declineBttn.setClickable(true);
                                //responseTv.setText("Login Failed \n" + error.toString());
                                Toast.makeText(appContext, "Approval Failed(server)", Toast.LENGTH_SHORT).show();
                            }
                        });

                Volley.newRequestQueue(appContext).add(jsonRequest);

            }
        });

        holder.declineBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.declineBttn.setClickable(false);
                holder.acceptBttn.setClickable(false);
                //Toast.makeText(appContext, "Accepted", Toast.LENGTH_SHORT).show();

                //holder.acceptBttn.setText("Approved");
                //holder.acceptBttn.setBackgroundColor(Color.GREEN);

                String url = "http://" + serverAddress + "/approval/?state=1" +
                        "&client_type=" + clientType +
                        "&request_key=" + mDataset.get(holder.getAdapterPosition()).getRequestId();

                JsonObjectRequest jsonRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // the response is already constructed as a JSONObject!
                                Toast.makeText(appContext, "Request Declined", Toast.LENGTH_SHORT).show();
                                //holder.declineBttn.setText("Declined");
                                //holder.declineBttn.setBackgroundColor(Color.RED);
                                ProfileRequestFeedAdapter.this.deleteItem(holder.getAdapterPosition());


                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                holder.declineBttn.setClickable(true);
                                holder.acceptBttn.setClickable(true);
                                Toast.makeText(appContext, "Decline Failed(server)", Toast.LENGTH_SHORT).show();
                            }
                        });

                Volley.newRequestQueue(appContext).add(jsonRequest);
            }
        });

    }

    public void addItem(RequestDataObject dataObj, int index) {
        mDataset.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}