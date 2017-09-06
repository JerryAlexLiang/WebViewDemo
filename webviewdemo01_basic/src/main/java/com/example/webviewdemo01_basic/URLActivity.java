package com.example.webviewdemo01_basic;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;


public class URLActivity extends AppCompatActivity implements View.OnClickListener {

    private HorizontalScrollView mNoticeScrollView;
    private TranslateAnimation mRigthToLeftAnim;
    private Button btn_back;
    private TextView txt_title;
    private FloatingActionButton btn_top;
    private Button btn_refresh;
    private WebView webView;
    private long exitTime = 0;
    private final static float SCOLL_V = 0.5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题栏,注意：这句代码要写在setContentView()前面
        getSupportActionBar().hide();
        setContentView(R.layout.activity_url);
        //初始化视图
        initView();
        //设置webView
        setWebView();

    }

    /**
     * 设置webView
     */
    private void setWebView() {
        //控制对新加载的Url的处理,返回true,说明主程序处理WebView不做处理,返回false意味着WebView会对其进行处理
        webView.setWebViewClient(new WebViewClient() {
            //设置在webView点击打开的新网页在当前界面显示，而不跳转到已有的浏览器中
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        //设置WebView属性，运行执行js脚本
        webView.getSettings().setJavaScriptEnabled(true);
        //调用loadView方法为WebView加入链接
        webView.loadUrl("http://www.baidu.com/");

        //这里设置获取到的页面title
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                txt_title.setText(title);
            }
        });

    }

    private void initView() {
        mNoticeScrollView = (HorizontalScrollView) findViewById(R.id.horiSv);
        btn_back = (Button) findViewById(R.id.btn_back);
        txt_title = (TextView) findViewById(R.id.txt_title);
        btn_top = (FloatingActionButton) findViewById(R.id.btn_top);
        btn_refresh = (Button) findViewById(R.id.btn_refresh);
        webView = (WebView) findViewById(R.id.web_view);

        btn_back.setOnClickListener(this);
        btn_refresh.setOnClickListener(this);
        btn_top.setOnClickListener(this);

        //使用动画实现跑马灯效果
        txt_title.post(new Runnable() {
            @Override
            public void run() {
                mRigthToLeftAnim = new TranslateAnimation(mNoticeScrollView.getWidth(), -txt_title.getWidth(), 0, 0);
                mRigthToLeftAnim.setRepeatCount(Animation.INFINITE);//无限的
                mRigthToLeftAnim.setInterpolator(new LinearInterpolator());
                mRigthToLeftAnim.setDuration((long) ((mNoticeScrollView.getWidth() + txt_title.getWidth()) / SCOLL_V));
                txt_title.startAnimation(mRigthToLeftAnim);
            }
        });
    }

    /**
     * 我们需要重写回退按钮的时间,当用户点击回退按钮：
     * 1.webView.canGoBack()判断网页是否能后退,可以则goback()
     * 2.如果不可以连续点击两次退出App,否则弹出提示Toast
     */
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序~", Toast.LENGTH_SHORT).show();
                //刷新退出时间为当前时间
                exitTime = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                //关闭当前Activity
                finish();
                break;

            case R.id.btn_refresh:
                //刷新当前页面
                webView.reload();
                break;

            case R.id.btn_top:
                //滚动到顶部
                webView.setScrollY(0);
                break;
        }
    }

}
