package com.example.webviewdemo01_basic.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.webviewdemo01_basic.R;

/**
 * 创建日期：2017/9/6 on 下午5:28
 * 描述:自定义Dialog打开WiFi设置页
 * 作者:yangliang
 */
public class OpenWifiUtil {

    public static void showWifiDlg(final Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            Toast.makeText(context, "网络可用", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "网络不可用", Toast.LENGTH_SHORT).show();

            //弹出dialog，点击后跳转到设置页面
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("提醒");
            builder.setMessage("当前网络不可用，点击确定设置网络");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //跳转到系统网络设置的页面
//                    Intent intent = new Intent();
//                    intent.setClassName("com.android.settings", "com.android.settings.Settings");
//                    context.startActivity(intent);
                    //飞行模式，无线网和网络设置界面
                    Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                    context.startActivity(intent);
                }
            });
            builder.setNegativeButton("知道了", null);
            builder.create().show();
        }

    }

}


