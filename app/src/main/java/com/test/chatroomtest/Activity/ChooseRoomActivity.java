package com.test.chatroomtest.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.test.chatroomtest.Data.ChatMessage;
import com.test.chatroomtest.Data.ChatRoomIdData;
import com.test.chatroomtest.Data.UserNameData;
import com.test.chatroomtest.Module.Config;
import com.test.chatroomtest.Module.InternetHelper;
import com.test.chatroomtest.R;
import com.test.chatroomtest.ViewHolder.ChatRoomIdViewHolder;
import com.test.chatroomtest.ViewHolder.UserNameViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class ChooseRoomActivity extends AppCompatActivity {
    Context context;
    DatabaseReference NameReference;
    DatabaseReference ChatRoomReference;
    RecyclerView NameRecycler;
    RecyclerView ChatRoomRecycler;
    LinearLayoutManager layoutManager;
    public static Button createNewChatRoomBtn;
    String TAG="ChatRoom:";
    FirebaseRecyclerAdapter<UserNameData, UserNameViewHolder> UserNameFirebaseRecyclerAdapter;
    FirebaseRecyclerAdapter<ChatRoomIdData, ChatRoomIdViewHolder>ChatRoomIdFirebaseRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_room);
        context=ChooseRoomActivity.this;
        setSomethingView();
        firebaseAdapterSetting();
    }

    private void firebaseAdapterSetting() {
        UserNameFirebaseRecyclerAdapter=new FirebaseRecyclerAdapter<UserNameData, UserNameViewHolder>(
                UserNameData.class,R.layout.recycler_user_friend,UserNameViewHolder.class,NameReference) {
            public UserNameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(context).inflate(R.layout.recycler_user_friend, parent, false);
                UserNameViewHolder holder=new UserNameViewHolder(view);
                return holder;
            }
            @Override
            protected void populateViewHolder(UserNameViewHolder userNameViewHolder, UserNameData userNameData, int i) {
                userNameViewHolder.setView(userNameData);
            }
        };
        NameRecycler.setLayoutManager(layoutManager);
        NameRecycler.setHasFixedSize(true);
        NameRecycler.setAdapter(UserNameFirebaseRecyclerAdapter);
        NameRecycler.scrollToPosition(UserNameFirebaseRecyclerAdapter.getItemCount()-1);
        ChatRoomIdFirebaseRecyclerAdapter=new FirebaseRecyclerAdapter<ChatRoomIdData, ChatRoomIdViewHolder>(
                ChatRoomIdData.class,R.layout.recycler_chat_room,ChatRoomIdViewHolder.class,ChatRoomReference) {
            public ChatRoomIdViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(context).inflate(R.layout.recycler_chat_room, parent, false);
                ChatRoomIdViewHolder holder=new ChatRoomIdViewHolder(view);
                return holder;
            }
            @Override
            protected void populateViewHolder(ChatRoomIdViewHolder chatRoomIdViewHolder, ChatRoomIdData chatRoomIdData, int i) {
                chatRoomIdViewHolder.setView(chatRoomIdData,context);
            }
        };
        ChatRoomRecycler.setLayoutManager(new LinearLayoutManager(context));
        ChatRoomRecycler.setHasFixedSize(true);
        ChatRoomRecycler.setAdapter(ChatRoomIdFirebaseRecyclerAdapter);
        ChatRoomRecycler.scrollToPosition(ChatRoomIdFirebaseRecyclerAdapter.getItemCount()-1);
    }
    private void getName(String chatid){
        FirebaseDatabase.getInstance().getReference().child("chatroom").child(chatid).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String name=dataSnapshot.child("name").getValue().toString();
                    Log.d(TAG, "onDataChange: "+name);
                    Config.chatUser=Config.chatUser+name+" ";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setSomethingView() {
        NameRecycler=findViewById(R.id.user_friend_recycler);
        ChatRoomRecycler=findViewById(R.id.chatroom_id_recycler);
        NameReference= FirebaseDatabase.getInstance().getReference().child("user");
        ChatRoomReference=FirebaseDatabase.getInstance().getReference().child("users").child(Config.AccountName).child("chatroom");
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        createNewChatRoomBtn=findViewById(R.id.create_new_chatroom);
        createNewChatRoomBtn.setOnClickListener(v -> {
            if (InternetHelper.CheckInternet(ChooseRoomActivity.this)){
                createNewChatRoom(Config.friendName);
            }
        });
    }
    private void createNewChatRoom(ArrayList<String> newUser) {
        long time=new Date().getTime();
        String allUser="";
        for (int i=0;i<newUser.size();i++){
            if (i==(newUser.size()-1)){
                allUser=allUser+newUser.get(i);
            }else {
                allUser=allUser+newUser.get(i)+"&";
            }
        }
        String chatRoomId=Config.AccountName+"&"+allUser+time;
        FirebaseDatabase.getInstance().getReference().child("users").child(Config.AccountName).child("chatroom").push().setValue(new ChatRoomIdData(chatRoomId));
        FirebaseDatabase.getInstance().getReference().child("chatroom").child(chatRoomId).child("users").push().setValue(new UserNameData(Config.AccountName));
        for (int i=0;i<newUser.size();i++){
            FirebaseDatabase.getInstance().getReference().child("users").child(newUser.get(i)).child("chatroom").push().setValue(new ChatRoomIdData(chatRoomId));
            FirebaseDatabase.getInstance().getReference().child("chatroom").child(chatRoomId).child("users").push().setValue(new UserNameData(newUser.get(i)));
        }
    }
}