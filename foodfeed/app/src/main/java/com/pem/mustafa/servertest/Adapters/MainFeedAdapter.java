package com.pem.mustafa.servertest.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.pem.mustafa.servertest.CustomObjects.DataObject;
import com.pem.mustafa.servertest.Other.LoginState;
import com.pem.mustafa.servertest.Activities.PostDetailActivity;
import com.pem.mustafa.servertest.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainFeedAdapter extends RecyclerView.Adapter<MainFeedAdapter.DataObjectHolder> {

    private static String LOG_TAG = "MainFeedAdapter";
    private static ArrayList<DataObject> mDataset;
    private static MyClickListener myClickListener;
    private static final String serverAddress = LoginState.getServerAdress();
    private static Context appContext;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView foodname;
        TextView price;
        TextView location;
        TextView username;
        //TextView rating;
        ImageView foodimage;
        ImageView fooduserimage;

        public DataObjectHolder(View itemView) {
            super(itemView);
            foodname = (TextView) itemView.findViewById(R.id.foodnamev3);
            price = (TextView) itemView.findViewById(R.id.foodpricev3);
            location = (TextView) itemView.findViewById(R.id.foodplacev3);
            username = (TextView) itemView.findViewById(R.id.fooduserv3);
            //rating = (TextView) itemView.findViewById(R.id.ratingFeed);
            foodimage = (ImageView) itemView.findViewById(R.id.foodimagev3);
            fooduserimage = (ImageView) itemView.findViewById(R.id.fooduserimagev3);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //myClickListener.onItemClick(getPosition(), v);
            int pos = this.getAdapterPosition();
            //Toast.makeText(appContext, "Pressed : " + mDataset.get(pos).getFoodname(), Toast.LENGTH_SHORT).show();
            Intent toDetail = new Intent(appContext, PostDetailActivity.class);
            toDetail.putExtra("foodname", mDataset.get(pos).getFoodname() );
            toDetail.putExtra("price", mDataset.get(pos).getPrice() );
            toDetail.putExtra("username", mDataset.get(pos).getUsername() );
            toDetail.putExtra("foodid", mDataset.get(pos).getFoodId());
            toDetail.putExtra("foodimageurl", mDataset.get(pos).getImageUrl());
            toDetail.putExtra("hasphoto", mDataset.get(pos).isHasPhoto());

            v.getContext().startActivity(toDetail);


        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MainFeedAdapter(ArrayList<DataObject> myDataset, Context appContext) {
        this.mDataset = myDataset;
        this.appContext = appContext;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_recyclerv3, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }


    @Override
    public void onBindViewHolder(final DataObjectHolder holder, int position) {
        holder.foodname.setText(mDataset.get(position).getFoodname());
        holder.price.setText(mDataset.get(position).getPrice() + " TL");
        holder.location.setText(mDataset.get(position).getLocation());
        holder.username.setText("by "+mDataset.get(position).getUsername());
        //holder.rating.setText(mDataset.get(position).getRating());

        //Picasso.with(appContext).setIndicatorsEnabled(true);
        //Picasso.with(appContext).setLoggingEnabled(true);

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

        //Log.d(LOG_TAG,":::::: " + mDataset.get(position).getUsername() + " " + mDataset.get(position).getUserImageUrl());

        if(mDataset.get(position).isUserHasPhoto())
        {
            Picasso.with(appContext)
                    .load(mDataset.get(position).getUserImageUrl())
                    .placeholder(R.drawable.ic_person_black_48dp)
                    .error(R.drawable.ic_person_black_48dp)
                    .into(holder.fooduserimage);
        }
        else
        {
            holder.fooduserimage.setImageResource(R.drawable.ic_person_black_48dp);
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