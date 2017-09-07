package com.example.yangliang.webviewdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

/**
 * 创建日期：2017/9/7 on 上午10:16
 * 描述://检测用户的网络情况，若网络不可用，则弹出对话框，点击“确定”后跳转到系统网络设置的界面
 * --logCat里查看cmp=com.android.settings/.Settings
 * 作者: liangyang
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            Toast.makeText(this, "网络可用", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();

            //弹出dialog，点击后跳转到设置页面
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提醒");
            builder.setMessage("当前网络不可用，点击确定设置网络");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //跳转到系统网络设置的页面
//                    Intent intent = new Intent();
//                    intent.setClassName("com.android.settings", "com.android.settings.Settings");
//                    startActivity(intent);
                    //飞行模式，无线网和网络设置界面
                    Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("知道了", null);
            builder.create().show();
        }

    }
}
