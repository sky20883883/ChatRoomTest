package com.test.chatroomtest.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.test.chatroomtest.Data.MessageData;
import com.test.chatroomtest.Module.Config;
import com.test.chatroomtest.R;
import com.test.chatroomtest.ViewHolder.MessageViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder2> {
    ArrayList<MessageData> listData;
    Context context;
    public MessageAdapter(Context context, ArrayList<MessageData> listData){
        this.context=context;
        this.listData=listData;
    }
    @NonNull
    @Override
    public MessageViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.message_view,parent,false);
        return new MessageViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder2 holder, int position) {
        MessageData data=listData.get(position);
        String Time=new SimpleDateFormat("hh:mm a", Locale.TRADITIONAL_CHINESE).format(data.getTime());
        String Message=data.getMessage();
        String User=data.getUser();
        if (Message==null){
            holder.userLayout.setVisibility(View.GONE);
            holder.otherLayout.setVisibility(View.GONE);
        }else {
            if (!User.equals(Config.AccountName)){
                holder.userLayout.setVisibility(View.GONE);
                holder.otherLayout.setVisibility(View.VISIBLE);
                holder.otherTime.setText(Time);
                holder.otherMessage.setText(Message);
                holder.otherName.setText(User);
            }
            else {
                holder.otherLayout.setVisibility(View.GONE);
                holder.userLayout.setVisibility(View.VISIBLE);
                holder.myMessage.setText(Message);
                holder.myTime.setText(Time);
            }
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
    public class MessageViewHolder2 extends RecyclerView.ViewHolder {
        TextView otherName,otherMessage,otherTime,myTime,myMessage;
        RelativeLayout userLayout,otherLayout;
        public MessageViewHolder2(@NonNull View itemView) {
            super(itemView);
            otherName=itemView.findViewById(R.id.txv_user_other);
            otherMessage=itemView.findViewById(R.id.txv_msg_other);
            otherTime=itemView.findViewById(R.id.txv_time_other);
            myMessage=itemView.findViewById(R.id.txv_msg_user);
            myTime=itemView.findViewById(R.id.txv_time_user);
            userLayout=itemView.findViewById(R.id.userLayout);
            otherLayout=itemView.findViewById(R.id.otherUserLayout);
        }
    }
}
