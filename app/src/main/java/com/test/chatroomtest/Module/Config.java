package com.test.chatroomtest.Module;

import android.os.Handler;

import java.util.ArrayList;

public class Config {
    public static Runnable mRunnable;
    public static Handler mHandler = new Handler();
    public static boolean isDamand = false;
    public static String AccountName;
    public static int recyclerviewReference=10;
    public static int ListCount=1;
    public static String ChatRoomId="chat";
    public static int countChoose=0;
    public static ArrayList<String> friendName=new ArrayList<>();
    public static String chatUser="";
}
