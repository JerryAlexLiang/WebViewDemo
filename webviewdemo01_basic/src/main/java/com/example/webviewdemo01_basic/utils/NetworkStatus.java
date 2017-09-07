package com.example.webviewdemo01_basic.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * 创建日期：2017/9/6 on 下午5:02
 * 描述:检测网络状态
 * 作者:yangliang
 */
public class NetworkStatus {

    private static String status;

    public static String getNetworkStatus(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo == null) {
            Toast.makeText(context, "亲，网络连了么？", Toast.LENGTH_SHORT).show();
            status = "false";
        }else {
            status = "true";
        }

        /*
        boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
      if(internet){
        //执行相关操作
        netStatus=true;
        Toast.makeText(context,
                "当前移动网络已连接！", Toast.LENGTH_LONG)
                .show();
    }else if(wifi){
        netStatus=true;
        Toast.makeText(context,
                "当前WIFI已连接", Toast.LENGTH_LONG)
                .show();
    } else
    {
        Toast.makeText(context,
                 "亲，网络连了么？", Toast.LENGTH_LONG)
                .show();
    }
        */
        return status;
    }

}
