package com.test.chatroomtest.ViewHolder;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.test.chatroomtest.Module.Config;
import com.test.chatroomtest.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MessageViewHolder extends RecyclerView.ViewHolder{
    TextView otherName,otherMessage,otherTime,myTime,myMessage;
    RelativeLayout userLayout,otherLayout;
    public MessageViewHolder(@NonNull View itemView) {
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
