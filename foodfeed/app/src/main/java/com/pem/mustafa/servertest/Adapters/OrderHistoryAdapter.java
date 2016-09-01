package com.pem.mustafa.servertest.Adapters;

/**
 * Created by mustafa on 10.05.2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pem.mustafa.servertest.CustomObjects.DataObject;
import com.pem.mustafa.servertest.Other.LoginState;
import com.pem.mustafa.servertest.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.DataObjectHolder> {

    private static String LOG_TAG = "OrderHistoryAdapter";
    private static ArrayList<DataObject> mDataset;
    private static MyClickListener myClickListener;
    private static final String serverAddress = LoginState.getServerAdress();
    private static Context appContext;
    private static final String clientType = "1";

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener,RatingBar.OnRatingBarChangeListener {
        TextView foodname;
        TextView price;
        TextView location;
        TextView username;
        //TextView rating;
        ImageView foodimage;
        RatingBar rateBar;

        public DataObjectHolder(View itemView) {
            super(itemView);
            foodname = (TextView) itemView.findViewById(R.id.orderhistoryname);
            price = (TextView) itemView.findViewById(R.id.orderhistoryprice);
            location = (TextView) itemView.findViewById(R.id.orderhistoryplace);
            username = (TextView) itemView.findViewById(R.id.orderhistoryuser);
            //rating = (TextView) itemView.findViewById(R.id.ratingFeed);
            foodimage = (ImageView) itemView.findViewById(R.id.orderhistoryimage);
            rateBar = (RatingBar) itemView.findViewById(R.id.orderhistoryratingbar);

            rateBar.setOnRatingBarChangeListener(this);
            //itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //myClickListener.onItemClick(getPosition(), v);
            int pos = this.getAdapterPosition();
            //Toast.makeText(appContext, "Pressed : " + mDataset.get(pos).getFoodname(), Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            int pos = this.getAdapterPosition();

            //Log.d("ORDERHISTORYADAPTER : ", "Changed item: " + pos + "||| New Rating:  " + rating);
            ratingBar.setIsIndicator(true);
            String url = "http://" + serverAddress + "/ratemeal/?client_type=" + clientType
                    +"&username=" + LoginState.getUserInfo(appContext) + "&foodid="+ mDataset.get(pos).getFoodId() +
                    "&rate=" + (int)rating;



            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject responseObject) {
                            // the response is already constructed as a JSONObject!

                            try {
                                int id = responseObject.getInt("id");
                                Toast.makeText(appContext, "Your rating is saved", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(appContext, "Rating couldn't saved", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            //responseTv.setText("Login Failed \n" + error.toString());
                            Toast.makeText(appContext, "Rating couldn't saved(server)", Toast.LENGTH_SHORT).show();

                        }
                    });

            Volley.newRequestQueue(appContext).add(jsonRequest);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public OrderHistoryAdapter(ArrayList<DataObject> myDataset, Context appContext) {
        this.mDataset = myDataset;
        this.appContext = appContext;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_orderhistory, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }


    @Override
    public void onBindViewHolder(final DataObjectHolder holder, int position) {
        holder.foodname.setText(mDataset.get(position).getFoodname());
        holder.price.setText(mDataset.get(position).getPrice() + " TL");
        holder.location.setText("at "+mDataset.get(position).getLocation());
        holder.username.setText("by "+mDataset.get(position).getUsername());
        //holder.rating.setText(mDataset.get(position).getRating());
        if(mDataset.get(position).isHasPhoto())
        {
            Picasso.with(appContext)
                    .load(mDataset.get(position).getImageUrl())
                    .placeholder(R.drawable.defaultfood)
                    .error(R.drawable.defaultfood)
                    .into(holder.foodimage);
        }
        else
        {
            holder.foodimage.setImageResource(R.drawable.defaultfood);
        }


    }

    public void addItem(DataObject dataObj, int index) {
        mDataset.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    public void clearData() {
        int size = this.mDataset.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.mDataset.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
