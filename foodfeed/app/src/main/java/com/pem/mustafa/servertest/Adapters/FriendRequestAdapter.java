package com.pem.mustafa.servertest.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.pem.mustafa.servertest.Other.LoginState;
import com.pem.mustafa.servertest.Activities.OtherProfile;
import com.pem.mustafa.servertest.R;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by mustafa on 22.03.2016.
 */
public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.DataObjectHolder> {

    private static String LOG_TAG = "FriendRequestAdapter";
    private static ArrayList<String> mDataset;
    private static MyClickListener myClickListener;
    private static Context appContext;
    private static final String serverAddress = LoginState.getServerAdress();

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView profileimage;
        TextView username;
        Button acceptButton;
        Button declineButton;


        public DataObjectHolder(View itemView) {
            super(itemView);

            username = (TextView) itemView.findViewById(R.id.friendrequestrowname);
            acceptButton = (Button) itemView.findViewById(R.id.friendrequestrowacceptbutton);
            declineButton = (Button) itemView.findViewById(R.id.friendrequestrowdeclinebutton);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = this.getAdapterPosition();
            Intent toDetail = new Intent(appContext, OtherProfile.class);
            toDetail.putExtra("foodownername", mDataset.get(pos) );

            v.getContext().startActivity(toDetail);


        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public FriendRequestAdapter(ArrayList<String> myDataset, Context appContext) {
        this.mDataset = myDataset;
        this.appContext = appContext;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_friendrequest, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);

        return dataObjectHolder;
    }


    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {

        holder.username.setText(mDataset.get(position));

        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(appContext, "Clicked accept", Toast.LENGTH_SHORT).show();
                requestAction(mDataset.get(position), 0, position);
            }
        });

        holder.declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(appContext, "Clicked decline", Toast.LENGTH_SHORT).show();
                requestAction(mDataset.get(position), 1, position);
            }
        });
    }

    public void addItem(String dataObj, int index) {
        mDataset.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    public void requestAction(String requestowner, final int state, final int position)
    {
        String url = "http://" + serverAddress + "/friendapproval/?client_type=" + 1
                +"&username=" + LoginState.getUserInfo(appContext) + "&requestowner=" + requestowner + "&state=" + state;

        JsonArrayRequest jsonRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray responseObject) {

                        if(state == 0)
                            Toast.makeText(appContext, "Request Accepted", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(appContext, "Request Declined", Toast.LENGTH_SHORT).show();
                        deleteItem(position);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();
                        Toast.makeText(appContext, "Process Failed (server)", Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(appContext).add(jsonRequest);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
