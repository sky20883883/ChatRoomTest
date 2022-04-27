package com.test.chatroomtest.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.test.chatroomtest.Module.InternetHelper;
import com.test.chatroomtest.R;
import com.test.chatroomtest.Module.Config;
import com.test.chatroomtest.Module.DialogHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Objects;

public class LogInActivity extends AppCompatActivity {
    EditText accountText,passwordText;
    Button logInBtn,registerBtn;
    String TAG="FireBase:";
    Boolean Internet=false;

    @Override
    protected void onResume() {
        super.onResume();
        InternetHelper.CheckInternet(LogInActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        accountText=findViewById(R.id.editTextAccount);
        passwordText=findViewById(R.id.editTextNumberPassword);
        logInBtn=findViewById(R.id.login_btn);
        registerBtn=findViewById(R.id.register_btn);
        registerBtn.setOnClickListener(v->{
            if (InternetHelper.CheckInternet(LogInActivity.this)){
                Intent intent=new Intent(LogInActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        logInBtn.setOnClickListener(view -> {
            if (InternetHelper.CheckInternet(LogInActivity.this)){
                if (accountText.getText().toString().equals("")||passwordText.getText().toString().equals("")){
                    Toast.makeText(LogInActivity.this,"帳號或密碼其中一個尚未輸入!",Toast.LENGTH_LONG).show();
                }
                else {
                    DialogHelper.WaitProgressDialog(LogInActivity.this,"登入中","登入中請稍後");
                    LogIn(accountText.getText().toString(),passwordText.getText().toString());
                }
            }
        });
        //showAccount();
    }
    public void showAccount(){
        FirebaseDatabase.getInstance().getReference("chat").addListenerForSingleValueEvent(new ValueEventListener() {
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
    }
    public void LogIn(String Account,String Password){
    FirebaseDatabase.getInstance().getReference("users").child(Account).child("password").addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.getValue()!=null){
                String pwd = Objects.requireNonNull(snapshot.getValue()).toString();
                Log.d(TAG, "onDataChange: "+pwd);
                if (pwd.equals(Password)){
                    Config.AccountName=Account;
                    DialogHelper.waitProgressDialog.dismiss();
  //                  Intent intent=new Intent(LogInActivity.this,MainActivity.class);
                    Intent intent=new Intent(LogInActivity.this,ChooseRoomActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    DialogHelper.waitProgressDialog.dismiss();
                    Toast.makeText(LogInActivity.this,"密碼錯誤!",Toast.LENGTH_LONG).show();
                    passwordText.setText("");
                }
            }
            else {
                DialogHelper.waitProgressDialog.dismiss();
                Toast.makeText(LogInActivity.this,"此帳號不存在!",Toast.LENGTH_LONG).show();
                accountText.setText("");
                passwordText.setText("");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            DialogHelper.waitProgressDialog.dismiss();
        }
    });

}
}