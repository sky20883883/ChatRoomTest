package com.test.chatroomtest.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.test.chatroomtest.Activity.MainActivity;
import com.test.chatroomtest.Data.ChatRoomIdData;
import com.test.chatroomtest.Module.Config;
import com.test.chatroomtest.R;

public class ChatRoomIdViewHolder extends RecyclerView.ViewHolder {
    TextView chatName,chatUser;
    Button button,deleteBtn;
    public ChatRoomIdViewHolder(@NonNull View itemView) {
        super(itemView);
        chatName=itemView.findViewById(R.id.recycler_chat_name_text);
        chatUser=itemView.findViewById(R.id.recycler_chat_user_name_text);
        button=itemView.findViewById(R.id.recycler_go_to_chat_room);
        deleteBtn=itemView.findViewById(R.id.recycler_dlete_chat_room);
    }
    public void setView(ChatRoomIdData model, Context context){
        if (model!=null){
            chatName.setText(model.getChatRoomId());
            button.setOnClickListener(v -> {
                Config.ChatRoomId=model.getChatRoomId();
                Intent intent=new Intent(context, MainActivity.class);
                context.startActivity(intent);
            });
            deleteBtn.setOnClickListener(v -> {
                FirebaseDatabase.getInstance().getReference().child("chatroom").child(model.getChatRoomId()).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            String name=dataSnapshot.child("name").getValue().toString();
                            DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
                            Query deleteQuery=reference.child("users").child(name).child("chatroom").orderByChild("chatRoomId").equalTo(model.getChatRoomId());
                            deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                    for (DataSnapshot delete:snapshot1.getChildren()){
                                        delete.getRef().removeValue();
                                    }
                                    FirebaseDatabase.getInstance().getReference().child("chatroom").child(model.getChatRoomId()).removeValue();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            });
        }
    }
    public void setUserName(String name){
        chatUser.setText(name);
    }
}
