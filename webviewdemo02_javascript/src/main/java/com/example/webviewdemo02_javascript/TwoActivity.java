package com.example.webviewdemo02_javascript;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TwoActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);
        webView = (WebView) findViewById(R.id.web_view02);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        //获得WebSetting对象,支持js脚本,可访问文件,支持缩放,以及编码方式
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setBuiltInZoomControls(true);
        settings.setDefaultTextEncodingName("UTF-8");

        //设置WebChromeClient,处理网页中的各种js事件
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.loadUrl("file:///android_asset/demo02.html");

    }

    /**
     * 这里需要自定义一个类实现WebChromeClient类,并重写三种不同对话框的处理方法
     * 分别重写onJsAlert,onJsConfirm,onJsPrompt方法
     */
    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            //创建一个Builder来显示网页中的对话框
            new AlertDialog.Builder(TwoActivity.this)
                    .setTitle("Alert对话框")
                    .setMessage(message)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    })
                    .setCancelable(false)
                    .create().show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {

            new AlertDialog.Builder(TwoActivity.this)
                    .setTitle("Confirm对话框")
                    .setMessage(message)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();

                        }
                    })
                    .setCancelable(false)
                    .create().show();
            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
            //获得一个LayoutInflater对象factory,加载指定布局成相应对象
            LayoutInflater inflater = LayoutInflater.from(TwoActivity.this);
            View promptView = inflater.inflate(R.layout.prompt_view, null);
            //设置TextView对应网页中的提示信息,edit设置来自于网页的默认文字
            TextView text = (TextView) promptView.findViewById(R.id.text);
            text.setText(message);
            final EditText edText = (EditText) promptView.findViewById(R.id.edit);
            edText.setText(defaultValue);
            //定义对话框上的确定按钮
            new AlertDialog.Builder(TwoActivity.this)
                    .setTitle("Prompt对话框")
                    .setView(promptView)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //单击确定后取得输入的值,传给网页处理
                            String value = edText.getText().toString();
                            result.confirm(value);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    })
                    .create().show();
            return true;
        }
    }
}
