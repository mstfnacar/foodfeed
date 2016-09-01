package com.pem.mustafa.servertest.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pem.mustafa.servertest.Other.LoginState;
import com.pem.mustafa.servertest.CustomObjects.MessageObject;
import com.pem.mustafa.servertest.R;

import java.util.ArrayList;

/**
 * Created by mustafa on 22.03.2016.
 */
public class MessageBodyAdapter extends RecyclerView.Adapter<MessageBodyAdapter.MessageObjectHolder> {

    private static String LOG_TAG = "MessageBodyAdapter";
    private static ArrayList<MessageObject> mDataset;
    private static MyClickListener myClickListener;
    private static Context appContext;
    private static String username;

    public static class MessageObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView messageBody;

        public MessageObjectHolder(View itemView) {
            super(itemView);

            messageBody = (TextView) itemView.findViewById(R.id.messagebodyrowtextview);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = this.getAdapterPosition();
            /*Intent toDetail = new Intent(appContext, OtherProfile.class);
            toDetail.putExtra("foodownername", mDataset.get(pos) );

            v.getContext().startActivity(toDetail);*/


        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MessageBodyAdapter(ArrayList<MessageObject> myDataset, Context appContext) {
        this.mDataset = myDataset;
        this.appContext = appContext;
        username = LoginState.getUserInfo(appContext);
    }

    @Override
    public MessageObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_messagebody, parent, false);

        MessageObjectHolder dataObjectHolder = new MessageObjectHolder(view);
        return dataObjectHolder;
    }


    @Override
    public void onBindViewHolder(MessageObjectHolder holder, int position) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1.0f;


        String senderUsername = mDataset.get(position).getSender();
        if(senderUsername.equals(username))
        {
            params.gravity = Gravity.RIGHT;
            //holder.messageBody.setGravity(Gravity.RIGHT);
            holder.messageBody.setLayoutParams(params);
        }
        else
        {
            params.gravity = Gravity.LEFT;
            //holder.messageBody.setGravity(Gravity.LEFT);
            holder.messageBody.setLayoutParams(params);
        }
        holder.messageBody.setText(mDataset.get(position).getBody());
    }

    public void addItem(MessageObject dataObj, int index) {
        mDataset.add(dataObj);
        notifyItemInserted(mDataset.size()-1);
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
