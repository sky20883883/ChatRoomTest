package com.test.chatroomtest.Module;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.test.chatroomtest.R;

import cn.bluemobi.dylan.photoview.library.PhotoView;

public class DialogHelper {
    public static ProgressDialog waitProgressDialog;
    public static void WaitProgressDialog(Context Context, String Title, String Msg)
    {
        int timer = 300*1000;


        waitProgressDialog = ProgressDialog.show(Context, Title, Msg, true);
        waitProgressDialog.setCancelable(false);
//		waitProgressDialog.setOnKeyListener(onKeyListener);
        waitProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode==KeyEvent.KEYCODE_SEARCH){
                    return true;
                }
                else {
                    return false;
                }
            }
        });

        Config.mRunnable = new Runnable()
        {
            public void run()
            {
                Config.isDamand = false;
                waitProgressDialog.dismiss();
            }
        };

        Config.mHandler.postDelayed(Config.mRunnable, timer);
    }
    public static void messageDeleteDialog(Context context,long time){
        AlertDialog.Builder aBuilder =new AlertDialog.Builder(context);
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialog=inflater.inflate(R.layout.delete_or_not_dialog,null);
        aBuilder.setView(dialog).setCancelable(false);
        Button okBtn,noBtn;
        TextView title,textView;
        okBtn=dialog.findViewById(R.id.dialog_ok_btn);
        noBtn=dialog.findViewById(R.id.dialog_cancel_btn);
        title=dialog.findViewById(R.id.dialog_text_title);
        textView=dialog.findViewById(R.id.dialog_text_in_side);
        AlertDialog showDialog=aBuilder.create();
        showDialog.setOnKeyListener((dialog1, keyCode, event) -> {
            if (keyCode==KeyEvent.KEYCODE_SEARCH){
                return true;
            }
            else {
                return false;
            }
        });
        showDialog.show();
        okBtn.setOnClickListener(v->{
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
            Query deleteQuery=reference.child("chatroom").child(Config.ChatRoomId).child("chat").orderByChild("time").equalTo(time);
            deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot delete:snapshot.getChildren()){
                        delete.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            showDialog.dismiss();
        });
        noBtn.setOnClickListener(v->{
            showDialog.dismiss();
        });
    }
    public static void checkInternetDialog(Context context,String titleString,String textString){
        AlertDialog.Builder aBuilder =new AlertDialog.Builder(context);
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialog=inflater.inflate(R.layout.delete_or_not_dialog,null);
        aBuilder.setView(dialog).setCancelable(false);
        Button okBtn,noBtn;
        TextView title,textView;
        okBtn=dialog.findViewById(R.id.dialog_ok_btn);
        noBtn=dialog.findViewById(R.id.dialog_cancel_btn);
        title=dialog.findViewById(R.id.dialog_text_title);
        textView=dialog.findViewById(R.id.dialog_text_in_side);
        title.setText(titleString);
        textView.setText(textString);
        AlertDialog showDialog=aBuilder.create();
        showDialog.setOnKeyListener((dialog1, keyCode, event) -> {
            if (keyCode==KeyEvent.KEYCODE_SEARCH){
                return true;
            }
            else {
                return false;
            }
        });
        showDialog.show();
        okBtn.setOnClickListener(v->{
            showDialog.dismiss();
        });
        noBtn.setOnClickListener(v->{
            showDialog.dismiss();
        });
    }
    public static void goBackDialog(Context context,String titleString,String textString){
        AlertDialog.Builder aBuilder =new AlertDialog.Builder(context);
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialog=inflater.inflate(R.layout.delete_or_not_dialog,null);
        aBuilder.setView(dialog).setCancelable(false);
        Button okBtn,noBtn;
        TextView title,textView;
        okBtn=dialog.findViewById(R.id.dialog_ok_btn);
        noBtn=dialog.findViewById(R.id.dialog_cancel_btn);
        title=dialog.findViewById(R.id.dialog_text_title);
        textView=dialog.findViewById(R.id.dialog_text_in_side);
        title.setText(titleString);
        textView.setText(textString);
        AlertDialog showDialog=aBuilder.create();
        showDialog.setOnKeyListener((dialog1, keyCode, event) -> {
            if (keyCode==KeyEvent.KEYCODE_SEARCH){
                return true;
            }
            else {
                return false;
            }
        });
        showDialog.show();
        okBtn.setOnClickListener(v->{
            ((Activity)context).finish();
            showDialog.dismiss();
        });
        noBtn.setOnClickListener(v->{
            showDialog.dismiss();
        });
    }
    public static void SetImageDialog(Context context, Bitmap bitmap,int height,int width) {
        PhotoView imageView;
        LinearLayout layout;
        Button back;
        Dialog dialog=new Dialog(context,R.style.edit_AlertDialog_style);
        dialog.setContentView(R.layout.dialog_image);
        imageView=dialog.findViewById(R.id.dialog_bitmap);
        back=dialog.findViewById(R.id.button2);
        Glide.with(context)
                .load(bitmap)
                .override(WindowManager.LayoutParams.MATCH_PARENT)
                .into(imageView);
        back.setOnClickListener(v->{
            dialog.dismiss();
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
//        AlertDialog.Builder builder=new AlertDialog.Builder(context);
//        LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View dialog=layoutInflater.inflate(R.layout.dialog_image,null);
//        imageView=dialog.findViewById(R.id.dialog_bitmap);
//        imageView.setImageBitmap(bitmap);
//        layout=dialog.findViewById(R.id.dialog_layout);
//        builder.setView(dialog);
//        AlertDialog showDialog=builder.show();
//        showDialog.show();
        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
//        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        Display display=windowManager.getDefaultDisplay();
//        Point size=new Point();
//        display.getSize(size);
//        int screenWidth=size.x;
//        float scale=screenWidth/width;
//        int screenHeight= (int) (height*scale);
//        WindowManager.LayoutParams layoutParams=showDialog.getWindow().getAttributes();
//        layoutParams.width=screenWidth;
//        layoutParams.height=screenHeight;
//        showDialog.getWindow().setAttributes(layoutParams);

    }
}
