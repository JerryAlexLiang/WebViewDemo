package com.example.webviewdemo03_download;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * 创建日期：2017/9/12 on 上午10:02
 * 描述:调用其它浏览器下载文件：
 * 只需为WebView设置setDownloadListener，
 * 然后重写DownloadListener的 onDownloadStart，
 * 然后在里面写个Intent，然后startActivity对应的Activity即可！
 * 作者: liangyang
 */
public class FirstActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题栏,注意：这句代码要写在setContentView()前面
        getSupportActionBar().hide();
        setContentView(R.layout.activity_first);
        progressBar = (ProgressBar) findViewById(R.id.custom_progressBar);
        webView = (WebView) findViewById(R.id.web_view);

        //控制对新加载的Url的处理,返回true,说明主程序处理WebView不做处理,返回false意味着WebView会对其进行处理
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    //加载完网页后，进度条消失
                    progressBar.setVisibility(View.GONE);
                } else {
                    //开始加载网页时显示进度条
                    progressBar.setVisibility(View.VISIBLE);
                    //设置进度值
                    progressBar.setProgress(newProgress);
                }

            }
        });

        WebSettings settings = webView.getSettings();
        // 允许执行JS脚本
        settings.setJavaScriptEnabled(true);
        // 缩放相关属性设置
        //设定支持viewport
        settings.setUseWideViewPort(true);
        //自适应屏幕
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        //隐藏缩放控件
        settings.setDisplayZoomControls(false);
        //设定支持缩放
        settings.setSupportZoom(true);

        // 载入URL
        webView.loadUrl("http://www.oschina.net/app");

        //然后，找到下载的地方，这个时候点击下载，就可以调用手机内置的浏览器下下载了
        //WebView默认没有开启文件下载的功能，如果要实现文件下载的功能，需要设置WebView的DownloadListener
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Toast.makeText(FirstActivity.this, "开始下载~", Toast.LENGTH_SHORT).show();
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

    }

    /**
     * /**
     * 我们需要重写回退按钮的时间,当用户点击回退按钮：
     * 1.webView.canGoBack()判断网页是否能后退,可以则goback()
     * 2.如果不可以连续点击两次退出App,否则弹出提示Toast
     */
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            //当webView不是处于第一页面时，返回上一个页面
            webView.goBack();
        } else {
            //当webView处于第一页面时,直接退出程序
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序~", Toast.LENGTH_SHORT).show();
                //刷新退出时间为当前时间
                exitTime = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }
        }
    }
}
