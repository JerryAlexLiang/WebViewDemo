package com.example.webviewdemo02_javascript;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class OneActivity extends AppCompatActivity {

    private WebView webView;

    @SuppressLint({"AddJavascriptInterface", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        webView = (WebView) findViewById(R.id.web_view);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl("file:///android_asset/demo01.html");
                return true;
            }
        });

        webView.loadUrl("file:///android_asset/demo01.html");

        WebSettings settings = webView.getSettings();
        //1、设置WebView允许调用js
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("UTF-8");
        //2、将object对象暴露给Js,调用addjavascriptInterface
        webView.addJavascriptInterface(new MyObject(OneActivity.this), "myObj");

    }

    /**
     * 自定义一个Object对象，js通过该类暴露的方法来调用Android
     */
    private class MyObject {

        private Context context;

        private MyObject(Context context) {
            this.context = context;
        }

        //将显示Toast和对话框的方法暴露给JS脚本调用
        @JavascriptInterface
        public void showToast(String text) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void showDialog() {
            new AlertDialog.Builder(context)
                    .setTitle("联系人列表")
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setItems(new String[]{"Android", "ios", "html5", "c++"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0:
                                    Toast.makeText(context, "Android", Toast.LENGTH_SHORT).show();
                                    break;
                                case 1:
                                    Toast.makeText(context, "ios", Toast.LENGTH_SHORT).show();
                                    break;
                                case 2:
                                    Toast.makeText(context, "html5", Toast.LENGTH_SHORT).show();
                                    break;
                                case 3:
                                    Toast.makeText(context, "c++", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    })
                    .setPositiveButton("确定", null)
                    .create().show();
        }

    }
}
