package com.test.chatroomtest.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.test.chatroomtest.Data.ChatMessage;
import com.test.chatroomtest.Data.UserData;
import com.test.chatroomtest.Module.Config;
import com.test.chatroomtest.Module.InternetHelper;
import com.test.chatroomtest.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    EditText nameEditText,passwordEditText;
    Button createBtn;
    DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
    ArrayList<UserData> list=new ArrayList<>();
    Boolean checkIsNumber=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        nameEditText=findViewById(R.id.register_account_editText);
        passwordEditText=findViewById(R.id.register_password_editText);
        createBtn=findViewById(R.id.register_create_btn);
        createBtn.setOnClickListener(v -> {
            if (!nameEditText.getText().toString().trim().equals("")){
                if (!passwordEditText.getText().toString().trim().equals("")){
                    checkIsNumber=CheckNumber(passwordEditText.getText().toString());
                    if (checkIsNumber){
                        if (InternetHelper.CheckInternet(RegisterActivity.this)){
                            String name=nameEditText.getText().toString();
                            String password=passwordEditText.getText().toString();
                            FirebaseDatabase.getInstance().getReference().child("user").push().setValue(new UserData(name));
                            FirebaseDatabase.getInstance().getReference().child("users").child(name).child("password").setValue(password);
                            Toast.makeText(this, "創建帳號成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }else {
                        Toast.makeText(this, "密碼不可以包含數字以外的字", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, "尚未輸入密碼", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "尚未輸入帳號", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean CheckNumber(String password){
        Pattern pattern=Pattern.compile("[0-9]*");
        Matcher matcher=pattern.matcher(password);
        return matcher.matches();
    }
}