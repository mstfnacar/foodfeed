package com.pem.mustafa.servertest.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pem.mustafa.servertest.Activities.OtherProfile;
import com.pem.mustafa.servertest.CustomObjects.FollowObj;
import com.pem.mustafa.servertest.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by mustafa on 22.03.2016.
 */
public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.DataObjectHolder> {

    private static String LOG_TAG = "FollowAdapter";
    private static ArrayList<FollowObj> mDataset;
    private static MyClickListener myClickListener;
    private static Context appContext;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView profileimage;
        TextView username;


        public DataObjectHolder(View itemView) {
            super(itemView);

            username = (TextView) itemView.findViewById(R.id.followrowname);
            profileimage = (ImageView) itemView.findViewById(R.id.followrowimage);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = this.getAdapterPosition();
            Intent toDetail = new Intent(appContext, OtherProfile.class);
            toDetail.putExtra("foodownername", mDataset.get(pos).getUsername() );

            v.getContext().startActivity(toDetail);


        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public FollowAdapter(ArrayList<FollowObj> myDataset, Context appContext) {
        this.mDataset = myDataset;
        this.appContext = appContext;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_follow, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }


    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        holder.username.setText(mDataset.get(position).getUsername());
        Log.d(LOG_TAG, ":::::::::: " + mDataset.get(position).getUsername() + " " + mDataset.get(position).getPhotourl());
        if(mDataset.get(position).getPhotourl() == null || mDataset.get(position).getPhotourl().equals(""))
        {
            holder.profileimage.setImageResource(R.drawable.ic_person_black_48dp);
        }
        else
        {
            Picasso.with(appContext)
                    .load(mDataset.get(position).getPhotourl())
                    .placeholder(R.drawable.ic_person_black_48dp)
                    .error(R.drawable.ic_person_black_48dp)
                    .into(holder.profileimage);
        }
    }

    public void addItem(FollowObj dataObj, int index) {
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
