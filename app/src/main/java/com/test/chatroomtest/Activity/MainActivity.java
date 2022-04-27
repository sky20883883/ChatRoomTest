package com.test.chatroomtest.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.paging.PagedListKt;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.ExifInterfaceImageHeaderParser;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions;
//import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter;
//import com.shreyaspatil.firebase.recyclerpagination.LoadingState;
import com.test.chatroomtest.Adapter.MessageAdapter;
import com.test.chatroomtest.Data.ChatMessage;
import com.test.chatroomtest.Module.BitMapHelper;
import com.test.chatroomtest.Module.DialogHelper;
import com.test.chatroomtest.Module.InternetHelper;
import com.test.chatroomtest.R;
import com.test.chatroomtest.Data.MessageData;
import com.test.chatroomtest.Module.Config;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import cn.bluemobi.dylan.photoview.library.PhotoView;

public class MainActivity extends AppCompatActivity {
    String TAG="Firebasae return";
    EditText mesEdit;
    Button sendBtn,photoBtn;
    Button goBackBtn;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    RecyclerView recyclerView;
    //MessageAdapter adapter;
    ArrayList<MessageData> listData=new ArrayList<>();
    DatabaseReference reference;
    LinearLayoutManager linearLayoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    FirebaseRecyclerAdapter<ChatMessage, ChatMessageHolder> firebaseRecyclerAdapter;
    //FirebaseRecyclerPagingAdapter<ChatMessage, ChatMessageHolder> firebaseRecyclerAdapter;
    InputMethodManager inputMethodManager;//鍵盤
    Context context;
    Boolean userScrolled=false;
    //save bitmap
    byte[] saveBitMap;

    public static final int CAMERA_PERMISSION = 100;//檢測相機權限用
    public static final int READ_WRITE_PERMISSION=99;//讀寫權限使用

    //主鑑
    ArrayList<String> keyList=new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        InternetHelper.CheckInternet(MainActivity.this);
        //fireStoreTest();
    }

    //找相片
    ActivityResultLauncher<Intent> findPhoto=
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()== Activity.RESULT_OK){
                        DialogHelper.WaitProgressDialog(MainActivity.this,"上傳照片中","上傳中，請稍後...");
                        Intent data=result.getData();
                        //取得圖檔的路徑位置
                        Uri uri = data.getData();
                        //抽象資料的接口
                        ContentResolver cr = MainActivity.this.getContentResolver();
                        try {
                            //由抽象資料接口轉換圖檔路徑為Bitmap
                            Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                            //處理圖片大小
                            int getHeight=bitmap.getHeight();
                            int getWidth=bitmap.getWidth();
                            if (getWidth>=600){
                                DecimalFormat decimalFormat=new DecimalFormat("0");
                                String height=decimalFormat.format((float)getHeight/getWidth*600);
                                getHeight=Integer.parseInt(height);
                                getWidth=600;
                            }
                            Bitmap photoBitmap=Bitmap.createScaledBitmap(bitmap,getWidth,getHeight,true);
                            String bitmapString=null;
                            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                            //photoBitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                            photoBitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                            byte[] bytes=byteArrayOutputStream.toByteArray();
                            bitmapString= Base64.encodeToString(bytes,Base64.DEFAULT);
                            sendPhotoToFirestore(bitmapString);



//                            ExifInterface exifInterface=null;
//                            String pathUri= BitMapHelper.getPathUri(uri,MainActivity.this);
//                            exifInterface=new ExifInterface(pathUri);



                        } catch (IOException e){
                            e.printStackTrace();
                        }

                    }
                    else {
                        DialogHelper.waitProgressDialog.dismiss();
                        Toast.makeText(MainActivity.this,"未選取任何相片",Toast.LENGTH_LONG).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=MainActivity.this;
        setview();
        permission();
        sendBtn.setOnClickListener(view -> {
            if (InternetHelper.CheckInternet(MainActivity.this)){
                if(!mesEdit.getText().toString().trim().equals("")){
                    sendMessage();
//                recyclerView.scrollToPosition(firebaseRecyclerAdapter.getItemCount()-1);
                    Config.ListCount+=1;
                    recyclerView.scrollToPosition(Config.ListCount);
                    recyclerView.smoothScrollToPosition(Config.ListCount);
                    //收起鍵盤
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
                    Log.d(TAG, "getItemCount: "+Config.ListCount);
                }else {
                    Toast.makeText(MainActivity.this,"訊息框中不可沒有輸入任何訊息",Toast.LENGTH_LONG).show();
                }
            }
        });
        photoBtn.setOnClickListener(view->{
            if (InternetHelper.CheckInternet(MainActivity.this)){
                sendPhoto();
//            recyclerView.scrollToPosition(firebaseRecyclerAdapter.getItemCount()-1);
                Config.ListCount+=1;
                recyclerView.scrollToPosition(Config.ListCount);
                recyclerView.smoothScrollToPosition(Config.ListCount);
            }
        });
        goBackBtn.setOnClickListener(view->{
            DialogHelper.goBackDialog(context,"返回首頁","請問是否確定返回至首頁");
//            finish();
        });
        //監聽更新
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                if (adapter!=null){
//                    recyclerView.scrollToPosition(adapter.getItemCount()-1);
//                }
                if (firebaseRecyclerAdapter!=null){
//                    recyclerView.scrollToPosition(firebaseRecyclerAdapter.getItemCount()-1);
                    recyclerView.scrollToPosition(Config.ListCount);
                    recyclerView.smoothScrollToPosition(Config.ListCount);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Config.ListCount=1;
                mSwipeRefreshLayout.setRefreshing(false);
                Config.recyclerviewReference+=5;
                firebaseRecyclerAdapter.notifyDataSetChanged();
                recyclerView.swapAdapter(setNewAdapter(),true);
            }
        });
        //displayMessage();
        fireBaseDisPlayMessage();
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (recyclerView.computeVerticalScrollOffset()>0){
//                    if (!recyclerView.canScrollVertically(1)){
//                        if (newState==AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
//                            Log.d(TAG, "畫面滑動: "+"到達頂部");
////                            Config.recyclerviewReference+=10;
////                            firebaseRecyclerAdapter.notifyDataSetChanged();
//                        }
//                        //Toast.makeText(MainActivity.this,"到達頂部!",Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });
    }
    private RecyclerView.Adapter setNewAdapter() {
        FirebaseRecyclerAdapter<ChatMessage,ChatMessageHolder> newAdapter= new FirebaseRecyclerAdapter<ChatMessage, ChatMessageHolder>
                (ChatMessage.class, R.layout.message_view, ChatMessageHolder.class,reference.limitToLast(Config.recyclerviewReference)
                ) {

            public ChatMessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(context).inflate(R.layout.message_view, parent, false);
                ChatMessageHolder holder = new ChatMessageHolder(view);

                return holder;
            }
            @Override
            protected void populateViewHolder(ChatMessageHolder viewHolder, ChatMessage model, final int position) {
                viewHolder.setValues(model);
                Config.ListCount+=1;
            }
        };
        return newAdapter;
    }
    public void setview(){
//        reference=FirebaseDatabase.getInstance().getReference().child("chat");
        //reference=FirebaseDatabase.getInstance().getReference().child("chatroom").child(Config.ChatRoomId);
        reference=FirebaseDatabase.getInstance().getReference().child("chatroom").child(Config.ChatRoomId).child("chat");
        recyclerView=findViewById(R.id.message_recycler);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mesEdit=findViewById(R.id.message_edittext);
        sendBtn=findViewById(R.id.send_message_btn);
        inputMethodManager=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        photoBtn=findViewById(R.id.photo_btn);
        mSwipeRefreshLayout=findViewById(R.id.recycler_refresh);
        goBackBtn=findViewById(R.id.go_back_btn);


    }
    public void fireBaseDisPlayMessage(){
        try {
            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ChatMessage, ChatMessageHolder>
                    (ChatMessage.class, R.layout.message_view, ChatMessageHolder.class,reference.limitToLast(Config.recyclerviewReference)
                    ) {

                public ChatMessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(context).inflate(R.layout.message_view, parent, false);
                    ChatMessageHolder holder = new ChatMessageHolder(view);

                    return holder;
                }
                @Override
                protected void populateViewHolder(ChatMessageHolder viewHolder, ChatMessage model, final int position) {
                    viewHolder.setValues(model);
                    Config.ListCount+=1;
                }
            };
            //paging
//            firebaseRecyclerAdapter = new FirebaseRecyclerPagingAdapter<ChatMessage, ChatMessageHolder>(options) {
//
//                @Override
//                protected void onBindViewHolder(@NonNull ChatMessageHolder chatMessageHolder, int i, @NonNull ChatMessage chatMessage) {
//                    chatMessageHolder.setValues(chatMessage);
//                }
//
//                @Override
//                protected void onLoadingStateChanged(@NonNull LoadingState state) {
//                    switch (state) {
//                        case LOADING_INITIAL:
//                        case LOADING_MORE:
//                            // Do your loading animation
//                            mSwipeRefreshLayout.setRefreshing(true);
//                            break;
//
//                        case LOADED:
//                            // Stop Animation
//                            mSwipeRefreshLayout.setRefreshing(false);
//                            break;
//
//                        case FINISHED:
//                            //Reached end of Data set
//                            mSwipeRefreshLayout.setRefreshing(false);
//                            break;
//
//                        case ERROR:
//                            retry();
//                            break;
//                    }
//                }
//
//                public ChatMessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                    View view = LayoutInflater.from(context).inflate(R.layout.message_view, parent, false);
//                    ChatMessageHolder holder = new ChatMessageHolder(view);
//
//                    return holder;
//                }
//            };

            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(firebaseRecyclerAdapter);
            recyclerView.scrollToPosition(firebaseRecyclerAdapter.getItemCount() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendPhoto(){
        permission();
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        findPhoto.launch(intent);
    }
    public void sendMessage(){
        String mes=mesEdit.getText().toString();
        long time=new Date().getTime();
        String photo="";

//        FirebaseDatabase.getInstance().getReference().child("chat").push().setValue(new ChatMessage(mes, Config.AccountName,time,"1",photo));
        FirebaseDatabase.getInstance().getReference().child("chatroom").child(Config.ChatRoomId).child("chat").push().setValue(new ChatMessage(mes, Config.AccountName,time,"1",photo));
        mesEdit.setText("");
    }
    public void pushPhoto(String id) {
        String mes="";
        long time=new Date().getTime();
//        FirebaseDatabase.getInstance().getReference().child("chat").push().setValue(new ChatMessage(mes,Config.AccountName,time,"2",id));
        FirebaseDatabase.getInstance().getReference().child("chatroom").child(Config.ChatRoomId).child("chat").push().setValue(new ChatMessage(mes,Config.AccountName,time,"2",id));
        DialogHelper.waitProgressDialog.dismiss();
    }
    //送出相片
    private void sendPhotoToFirestore(String photoStr) {
        Map<String,String> photoMap=new HashMap<>();
        photoMap.put("bitmap",photoStr);
        db.collection(Config.AccountName)
                .add(photoMap)
                .addOnSuccessListener(documentReference -> {
                    pushPhoto(documentReference.getId());
                }).addOnFailureListener(e -> {
                    DialogHelper.waitProgressDialog.dismiss();
                    Toast.makeText(MainActivity.this,"圖片上傳失敗",Toast.LENGTH_LONG).show();
                });
    }
    //權限
    private void permission(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                final AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("需要 讀寫 權限");
                builder.setMessage("請同意應用程式使用讀寫功能 。");
                builder.setPositiveButton(android.R.string.ok,null);
                builder.setOnDismissListener(dialog -> {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_WRITE_PERMISSION);
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},READ_WRITE_PERMISSION);
                });
                builder.show();
            }
            if (checkSelfPermission(Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
                final AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("需要 相機 權限");
                builder.setMessage("請同意應用程式使用相機功能 。");
                builder.setPositiveButton(android.R.string.ok,null);
                builder.setOnDismissListener(dialog -> {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION);
                });
                builder.show();
            }
        }
    }
    public class ChatMessageHolder extends RecyclerView.ViewHolder {
        TextView otherName,otherTime,myTime,otherMessage,myMessage;
        RelativeLayout userLayout,otherLayout;
        ImageView myImage,otherImage;
        TextView myImageTime,otherImageTime;
        RelativeLayout relativeLayout;
        LinearLayout layout;
        ImageView img_avatar_other,img_avatar_user;
        public ChatMessageHolder(@NonNull View itemView) {
            super(itemView);
            otherName=itemView.findViewById(R.id.txv_user_other);
            otherMessage=itemView.findViewById(R.id.txv_msg_other);
            otherTime=itemView.findViewById(R.id.txv_time_other);
            myMessage=itemView.findViewById(R.id.txv_msg_user);
            myTime=itemView.findViewById(R.id.txv_time_user);
            userLayout=itemView.findViewById(R.id.userLayout);
            otherLayout=itemView.findViewById(R.id.otherUserLayout);
            myImage=itemView.findViewById(R.id.imgmsg_user);
            otherImage=itemView.findViewById(R.id.imgmsg_otheruser);
            myImageTime=itemView.findViewById(R.id.txv_time_imgUSer);
            otherImageTime=itemView.findViewById(R.id.txv_time_imgOther);
            relativeLayout=itemView.findViewById(R.id.message_layout);
            layout=itemView.findViewById(R.id.message_liner);
            img_avatar_other=itemView.findViewById(R.id.img_avatar_other);
            img_avatar_user=itemView.findViewById(R.id.img_avatar_user);
        }
        public void setValues(ChatMessage model) {
            if (model!=null){
                img_avatar_user.setEnabled(false);
                img_avatar_other.setEnabled(false);
                long now=new Date().getTime();
                String today=new SimpleDateFormat("MM/dd", Locale.TRADITIONAL_CHINESE).format(now);
                String todayTwo=new SimpleDateFormat("MM/dd", Locale.TRADITIONAL_CHINESE).format(model.getTime());
                String Time;
                if (today.equals(todayTwo)){
                    Time=new SimpleDateFormat("a hh:mm", Locale.TRADITIONAL_CHINESE).format(model.getTime());
                    Time="今日 "+Time;
                }
                else {
                    Time=new SimpleDateFormat("MM/dd a hh:mm", Locale.TRADITIONAL_CHINESE).format(model.getTime());
                }
                String PhotoString=model.getPhoto();
                String Message=model.getMessage();
                String User=model.getUser();
                String Type=model.getType();
                if (Message==null){
                    userLayout.setVisibility(View.GONE);
                    otherLayout.setVisibility(View.GONE);
                }else {
                    if (Type.equals("1")){
                        relativeLayout.setVisibility(View.VISIBLE);
                        layout.setVisibility(View.VISIBLE);
                        if (!User.equals(Config.AccountName)){
                            userLayout.setVisibility(View.GONE);
                            otherLayout.setVisibility(View.VISIBLE);
                            otherMessage.setVisibility(View.VISIBLE);
                            otherImage.setVisibility(View.GONE);
                            otherTime.setVisibility(View.VISIBLE);
                            otherImageTime.setVisibility(View.GONE);
                            otherTime.setText(Time);
                            otherMessage.setText(Message);
                            otherName.setText(User);
//                            otherMessage.setOnClickListener(v->{
//                                DialogHelper.messageDeleteDialog(context,String.valueOf(model.getTime()));
//                            });
                        }
                        else {
                            otherLayout.setVisibility(View.GONE);
                            userLayout.setVisibility(View.VISIBLE);
                            myMessage.setVisibility(View.VISIBLE);
                            myImage.setVisibility(View.GONE);
                            myTime.setVisibility(View.VISIBLE);
                            myImageTime.setVisibility(View.GONE);
                            myMessage.setText(Message);
                            myTime.setText(Time);
                            myMessage.setOnClickListener(v->{
                                DialogHelper.messageDeleteDialog(context,model.getTime());
                            });
                        }
                    }
                    else {
                        relativeLayout.setVisibility(View.VISIBLE);
                        layout.setVisibility(View.VISIBLE);
                        if (!User.equals(Config.AccountName)){
                            userLayout.setVisibility(View.GONE);
                            otherLayout.setVisibility(View.VISIBLE);
                            otherMessage.setVisibility(View.GONE);
                            otherImage.setVisibility(View.VISIBLE);
                            otherTime.setVisibility(View.GONE);
                            otherImageTime.setVisibility(View.VISIBLE);
                            otherImageTime.setText(Time);
                            otherName.setText(User);
                            db.collection(User).document(PhotoString)
                                     .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.getData()!=null){
                                                JSONObject jsonObject=new JSONObject(documentSnapshot.getData());
                                                try {
                                                    byte[] bytes;
                                                    bytes= Base64.decode(jsonObject.getString("bitmap"),Base64.DEFAULT);
                                                    //otherImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
                                                    Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                                    Glide.with(context)
                                                            .load(bitmap)
                                                            .into(otherImage);
                                                    int height=bitmap.getHeight();
                                                    int width=bitmap.getWidth();
                                                    otherImage.setOnClickListener(v->{
                                                        DialogHelper.SetImageDialog(MainActivity.this,bitmap,height,width);
                                                    });
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            else {
                                                RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
                                                layoutParams.setMargins(0,0,0,0);
                                                relativeLayout.setVisibility(View.GONE);
                                                layout.setVisibility(View.GONE);
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
                                    layoutParams.setMargins(0,0,0,0);
                                    relativeLayout.setVisibility(View.GONE);
                                    layout.setVisibility(View.GONE);
                                }
                            });
                        }
                        else {
                            otherLayout.setVisibility(View.GONE);
                            userLayout.setVisibility(View.VISIBLE);
                            myMessage.setVisibility(View.GONE);
                            myMessage.setText(Message);
                            myImage.setVisibility(View.VISIBLE);
                            myTime.setVisibility(View.GONE);
                            myImageTime.setVisibility(View.VISIBLE);
                            myImageTime.setText(Time);
                            db.collection(Config.AccountName).document(PhotoString)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.getData()!=null){
                                                JSONObject jsonObject=new JSONObject(documentSnapshot.getData());
                                                try {
                                                    byte[] bytes;
                                                    bytes= Base64.decode(jsonObject.getString("bitmap"),Base64.DEFAULT);
                                                    //myImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
                                                    Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                                    Glide.with(context)
                                                            .load(bitmap)
                                                            .into(myImage);
                                                    int height=bitmap.getHeight();
                                                    int width=bitmap.getWidth();
                                                    myImage.setOnClickListener(v->{
                                                        DialogHelper.SetImageDialog(MainActivity.this,bitmap,height,width);
                                                    });
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            else {
                                                relativeLayout.setVisibility(View.GONE);
                                                layout.setVisibility(View.GONE);
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    relativeLayout.setVisibility(View.GONE);
                                    layout.setVisibility(View.GONE);
                                }
                            });
                        }

                    }
                }
            }
        }
    }
    public void displayMessage() {
//        FirebaseDatabase.getInstance().getReference("chat").addListenerForSingleValueEvent(new ValueEventListener()
        FirebaseDatabase.getInstance().getReference("chatroom").child(Config.ChatRoomId).child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(snapshot.getValue().toString());
                    Log.d(TAG, jsonObject.toString());
                    Iterator keys=jsonObject.keys();
                    while (keys.hasNext()){
                        String key=String.valueOf(keys.next());
                        JSONObject jsonObject1=new JSONObject(jsonObject.getString(key));
                        listData.add(new MessageData(jsonObject1.getString("message"),jsonObject1.getString("user"),jsonObject1.getLong("time")));
                        Log.d(TAG, "onDataChange: "+jsonObject1.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setHasFixedSize(true);
        //recyclerView.setAdapter(adapter);
        //recyclerView.scrollToPosition(adapter.getItemCount()-1);
    }
    private void fireStoreTest(){
        //抓單一資料表資料
        db.collection("message").document("m2Yj94epqqVCEZGO5tz5")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    JSONObject jsonObject=new JSONObject(documentSnapshot.getData());
                    try {
                        Toast.makeText(MainActivity.this,jsonObject.getString("bitmap"),Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
        //抓單全部資料表全部資料
//        db.collection("message")
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()){
//                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
//                            JSONObject jsonObject=new JSONObject(documentSnapshot.getData());
//                            try {
//                                Toast.makeText(MainActivity.this,jsonObject.getString("bitmap"),Toast.LENGTH_LONG).show();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                    else {
//                        Toast.makeText(MainActivity.this,"不存在!",Toast.LENGTH_LONG).show();
//                    }
//                });

        //存入資料
        Map<String, Object> user=new HashMap<>();
        user.put("bitmap","UserTest");
        user.put("bitmap2","UserTest2");
        db.collection("message")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MainActivity.this,documentReference.getId(),Toast.LENGTH_LONG).show();
                        Log.d("FireStore", "onSuccess: "+documentReference.getId());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_LONG).show();
                Log.d(TAG, "onFailure: "+e.toString());

            }
        });
    }
}