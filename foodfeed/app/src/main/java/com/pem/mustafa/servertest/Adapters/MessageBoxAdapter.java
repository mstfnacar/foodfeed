package com.pem.mustafa.servertest.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pem.mustafa.servertest.Activities.MessageBodyActivity;
import com.pem.mustafa.servertest.CustomObjects.MessageBoxObject;
import com.pem.mustafa.servertest.R;

import java.util.ArrayList;

/**
 * Created by mustafa on 22.03.2016.
 */
public class MessageBoxAdapter extends RecyclerView.Adapter<MessageBoxAdapter.MessageBoxObjectHolder> {

    private static String LOG_TAG = "MessageBoxAdapter";
    private static ArrayList<MessageBoxObject> mDataset;
    private static MyClickListener myClickListener;
    private static Context appContext;

    public static class MessageBoxObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView profileimage;
        TextView username;
        TextView body;


        public MessageBoxObjectHolder(View itemView) {
            super(itemView);

            username = (TextView) itemView.findViewById(R.id.messageboxrowusername);
            body = (TextView) itemView.findViewById(R.id.messageboxbodypreview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = this.getAdapterPosition();
            Intent toDetail = new Intent(appContext, MessageBodyActivity.class);
            toDetail.putExtra("username", mDataset.get(pos).getSender() );

            v.getContext().startActivity(toDetail);


        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MessageBoxAdapter(ArrayList<MessageBoxObject> myDataset, Context appContext) {
        this.mDataset = myDataset;
        this.appContext = appContext;
    }

    @Override
    public MessageBoxObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_messagebox, parent, false);

        MessageBoxObjectHolder dataObjectHolder = new MessageBoxObjectHolder(view);
        return dataObjectHolder;
    }


    @Override
    public void onBindViewHolder(MessageBoxObjectHolder holder, int position) {

        holder.username.setText(mDataset.get(position).getSender());
        holder.body.setText(mDataset.get(position).getBody());
    }

    public void addItem(MessageBoxObject dataObj, int index) {
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
