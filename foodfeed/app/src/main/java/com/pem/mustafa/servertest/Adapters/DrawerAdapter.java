package com.pem.mustafa.servertest.Adapters;

/**
 * Created by mustafa on 09.05.2016.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pem.mustafa.servertest.CustomObjects.CusSeekBar;
import com.pem.mustafa.servertest.Other.LoginState;
import com.pem.mustafa.servertest.R;
import com.pem.mustafa.servertest.Activities.SettingsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import android.support.v7.widget.RecyclerView;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DataObjectHolder> {

    private static String TAG = "DrawerAdapter";
    private static String packageName = "com.pem.mustafa.servertest.Activities.";
    private static ArrayList<String> mDataset;
    private static MyClickListener myClickListener;
    private static final String serverAddress = LoginState.getServerAdress();
    private static Context appContext;
    private static LinkedHashMap<String, String> mMapDataset;
    private static HashMap<String, Integer> mIconSet;


    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView drawerItem;
        ImageView drawerItemIcon;

        ImageView profileImage;
        TextView usernameTv;
        SeekBar distanceBar;
        TextView seekBarProgress;


        public DataObjectHolder(View itemView) {
            super(itemView);
            drawerItem = (TextView) itemView.findViewById(R.id.drawerTv);
            drawerItemIcon = (ImageView) itemView.findViewById(R.id.drawer_icon);

            profileImage = (ImageView) itemView.findViewById(R.id.drawer_profileimage);
            usernameTv = (TextView) itemView.findViewById(R.id.drawer_username);



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
            toDetail.putExtra("username", mDataset.get(pos).getUsername() );
            toDetail.putExtra("foodid", mDataset.get(pos).getFoodId());
            toDetail.putExtra("foodimageurl", mDataset.get(pos).getImageUrl());
            toDetail.putExtra("hasphoto", mDataset.get(pos).isHasPhoto());

            v.getContext().startActivity(toDetail);*/

            if(pos > 0 && pos <= mDataset.size())
            {
                String activityKey = packageName + mMapDataset.get(mDataset.get(pos-1));
                Log.d("DRAWER ADAPTER : ", "Activity Name : " + activityKey);
                Class<?> activityClass = null;
                if(activityKey != null) {
                    try {
                        activityClass = Class.forName(activityKey );
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    if(activityClass != null)
                    {
                        Intent newIntent = new Intent(appContext, activityClass);
                        v.getContext().startActivity(newIntent);
                    }

                }
            }
            else if(pos == mDataset.size() + 1)
            {
                //Log.d(TAG, "GOT ME ::::::::::::::::::::::::::::");
            }






        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public DrawerAdapter(LinkedHashMap myDataset, HashMap icons, Context appContext) {
        //this.mDataset = myDataset;
        this.appContext = appContext;
        //this.packageName = appContext.getPackageName();
        mDataset = new ArrayList<>();
        this.mIconSet = new HashMap<>(icons);
        this.mMapDataset = new LinkedHashMap(myDataset);
        Iterator it = myDataset.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            //Log.d("DRAWER ADAPTER ::::",pair.getKey() + " = " + pair.getValue());
            mDataset.add((String)pair.getKey());
            //Log.d("DRAWER ADAPTER ::::"," Size : " + mDataset.size());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View v = null;
        boolean filterDrawn = false;

        if (viewType == 0)
        {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_drawer_profile, parent, false);

        }
        else
        {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_drawer, parent, false);
        }

        DataObjectHolder vh = new DataObjectHolder(v);
        if(filterDrawn)
            vh.distanceBar.setProgress(LoginState.getLocationInterval(appContext));


        return vh;
    }


    @Override
    public void onBindViewHolder(final DataObjectHolder holder, int position) {

        if(position == 0)
        {
            // get profile picture here
            if(LoginState.getHasPhoto(appContext) == 1)
            {
                String photoUrl = LoginState.getUserPhoto(appContext);
                Picasso.with(appContext)
                        .load(photoUrl)
                        .placeholder(R.drawable.ic_person_white_48dp)
                        .error(R.drawable.ic_person_white_48dp)
                        .into(holder.profileImage);
            }
            else
            {
                holder.profileImage.setImageResource(R.drawable.ic_person_white_48dp);
            }
            holder.usernameTv.setText(LoginState.getUserInfo(appContext));
        }
        else
        {
            holder.drawerItem.setText(mDataset.get(position-1));
            holder.drawerItemIcon.setImageResource(mIconSet.get(mDataset.get(position-1)));

        }

    }

    public void addItem(String dataObj, int index) {
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
    public int getItemViewType(int position) {

        return position;
        //return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size()+1;
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}