package com.test.chatroomtest.Module;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetHelper {
    public static boolean CheckInternet(Context context){
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if (networkInfo==null||!networkInfo.isAvailable()){
            DialogHelper.checkInternetDialog(context,"網路未連接","您可能尚未開啟網路\n或者網路訊號過於微弱\n部分功能將無法使用");
            return false;
        }else {
            return true;
        }
    }
}
