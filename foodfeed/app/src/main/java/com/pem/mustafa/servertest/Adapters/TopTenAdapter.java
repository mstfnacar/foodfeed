package com.pem.mustafa.servertest.Adapters;

/**
 * Created by mustafa on 13.05.2016.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.pem.mustafa.servertest.Activities.OtherProfile;
import com.pem.mustafa.servertest.CustomObjects.TopTenDataObj;
import com.pem.mustafa.servertest.Other.LoginState;
import com.pem.mustafa.servertest.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TopTenAdapter extends RecyclerView.Adapter<TopTenAdapter.DataObjectHolder> {

    private static String LOG_TAG = "TopTenAdapter";
    private static ArrayList<TopTenDataObj> mChefDataset;
    private static ArrayList<TopTenDataObj> mGourmetDataset;
    private static MyClickListener myClickListener;
    private static Context appContext;
    private static int listType = 0;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView chefUsername;
        TextView chefRank;
        TextView chefTitle;
        CircularImageView chefProfileImage;
        TextView gourmetUsername;
        TextView gourmetRank;
        TextView gourmetTitle;
        CircularImageView gourmetProfileImage;




        public DataObjectHolder(View itemView) {
            super(itemView);

            chefUsername = (TextView) itemView.findViewById(R.id.toptenchefusername);
            chefRank = (TextView) itemView.findViewById(R.id.toptenchefsrank);
            chefTitle = (TextView) itemView.findViewById(R.id.toptencheftitle);
            chefProfileImage = (CircularImageView) itemView.findViewById(R.id.toptenchefprofileimage);

            gourmetUsername = (TextView) itemView.findViewById(R.id.toptengourmetusername);
            gourmetTitle = (TextView) itemView.findViewById(R.id.toptengourmettitle);
            gourmetRank = (TextView) itemView.findViewById(R.id.toptengourmetrank);
            gourmetProfileImage = (CircularImageView) itemView.findViewById(R.id.toptengourmetprofileimage);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = this.getAdapterPosition();

            String foodownername = "";
            if(listType == 0)
                foodownername = mChefDataset.get(pos).getName();
            else if(listType == 1)
                foodownername = mGourmetDataset.get(pos).getName();
            else
                return;

            Intent toOtherProfile = new Intent(appContext, OtherProfile.class);
            toOtherProfile.putExtra("foodownername", foodownername);
            v.getContext().startActivity(toOtherProfile);


        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public TopTenAdapter(ArrayList<TopTenDataObj> myChefDataset, ArrayList<TopTenDataObj> myGourmetDataset, int listType, Context appContext) {
        this.mChefDataset = myChefDataset;
        this.mGourmetDataset = myGourmetDataset;
        this.appContext = appContext;
        this.listType = listType;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType ) {
        View v = null;

        if (listType == 0)
        {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_toptenchefs, parent, false);

        }
        else
        {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_toptengourmets, parent, false);
        }

        DataObjectHolder vh = new DataObjectHolder(v);



        return vh;
    }


    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        if(listType == 0)
        {
            holder.chefUsername.setText(mChefDataset.get(position).getName());
            holder.chefRank.setText(mChefDataset.get(position).getRank());
            holder.chefTitle.setText(mChefDataset.get(position).getTitle());
            if(mChefDataset.get(position).isHasPhoto())
            {
                //Log.d("DEBUG MODE : " , mChefDataset.get(position).getName() + " :: " + mChefDataset.get(position).isHasPhoto() + " :: " + mChefDataset.get(position).getPhotoUrl());
                Picasso.with(appContext)
                        .load(mChefDataset.get(position).getPhotoUrl())
                        .placeholder(R.drawable.ic_person_black_48dp)
                        .error(R.drawable.ic_person_black_48dp)
                        .into(holder.chefProfileImage);
            }
            else
            {
                holder.chefProfileImage.setImageResource(R.drawable.ic_person_black_48dp);
            }

        }
        else
        {
            holder.gourmetUsername.setText(mGourmetDataset.get(position).getName());
            holder.gourmetRank.setText(mGourmetDataset.get(position).getRank());
            holder.gourmetTitle.setText(mGourmetDataset.get(position).getTitle());
            if(mGourmetDataset.get(position).isHasPhoto())
            {
                //Log.d("DEBUG MODE : " , mChefDataset.get(position).getName() + " :: " + mChefDataset.get(position).isHasPhoto() + " :: " + mChefDataset.get(position).getPhotoUrl());
                Picasso.with(appContext)
                        .load(mGourmetDataset.get(position).getPhotoUrl())
                        .placeholder(R.drawable.ic_person_black_48dp)
                        .error(R.drawable.ic_person_black_48dp)
                        .into(holder.gourmetProfileImage);
            }
            else
            {
                holder.gourmetProfileImage.setImageResource(R.drawable.ic_person_black_48dp);
            }

        }


    }

    public void addItem(String dataObj, int index) {
        //mDataset.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        //mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {

        if(listType == 0)
            return mChefDataset.size();
        else
            return mGourmetDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
