package com.example.webviewdemo01_basic;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.webviewdemo01_basic.utils.NetworkStatus;
import com.example.webviewdemo01_basic.utils.OpenWifiUtil;


public class URLActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String APP_CACHE_DIRNAME = "/webcache"; // web缓存目录
    private LinearLayout errrorWebPage;
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

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                //没有网络连接
//                webView.setVisibility(View.GONE);
//                errrorWebPage.setVisibility(View.VISIBLE);
//                btn_top.setVisibility(View.GONE);
            }
        });

        //设置WebView属性，运行执行js脚本
        webView.getSettings().setJavaScriptEnabled(true);

        //开启DOM storage API 功能
        webView.getSettings().setDomStorageEnabled(true);
        //开启database storage API功能
        webView.getSettings().setDatabaseEnabled(true);
        // web缓存目录
        String cacheDirPath = getFilesDir().getAbsolutePath() + APP_CACHE_DIRNAME;
        Log.d("tag", "cacheDirPath: " + cacheDirPath);
        // 设置数据库缓存路径
        webView.getSettings().setAppCachePath(cacheDirPath);
        webView.getSettings().setAppCacheEnabled(true);

        //调用loadView方法为WebView加入链接
        webView.loadUrl("http://www.baidu.com/");

        //检测网络状态
        String networkStatus = NetworkStatus.getNetworkStatus(this);
        //缓存策略：判断是否有网络，有的话，使用LOAD_DEFAULT， 无网络时，使用LOAD_CACHE_ELSE_NETWORK。
        if (networkStatus.equals("true")) {
            //设置缓存模式
            this.webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            this.webView.setVisibility(View.VISIBLE);
            errrorWebPage.setVisibility(View.GONE);
        } else if (networkStatus.equals("false")) {
            this.webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            //检测网络状态
            OpenWifiUtil.showWifiDlg(this);
        }

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
        errrorWebPage = (LinearLayout) findViewById(R.id.web_view_error_page);
        RelativeLayout errrorWebPageBtn = (RelativeLayout) errrorWebPage.findViewById(R.id.online_error_rl);
        mNoticeScrollView = (HorizontalScrollView) findViewById(R.id.horiSv);
        btn_back = (Button) findViewById(R.id.btn_back);
        txt_title = (TextView) findViewById(R.id.txt_title);
        btn_top = (FloatingActionButton) findViewById(R.id.btn_top);
        btn_refresh = (Button) findViewById(R.id.btn_refresh);
        webView = (WebView) findViewById(R.id.web_view);
        Button btn_clear = (Button) findViewById(R.id.btn_clear);

        btn_back.setOnClickListener(this);
        btn_refresh.setOnClickListener(this);
        btn_top.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        errrorWebPageBtn.setOnClickListener(this);

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
                checkNetState();
                break;

            case R.id.btn_top:
                //滚动到顶部
                webView.setScrollY(0);
                break;

            case R.id.btn_clear:
                // webView清除缓存
                webView.clearCache(true);
                checkNetState();
                break;

            case R.id.online_error_rl:
                //刷新当前页面
                checkNetState();
                break;

            default:
                break;
        }
    }

    /**
     * 判断网络状态
     */
    private void checkNetState() {
        String networkStatus = NetworkStatus.getNetworkStatus(getApplicationContext());
        if (networkStatus.equals("false")) {
            //检测网络状态
            webView.setVisibility(View.GONE);
            errrorWebPage.setVisibility(View.VISIBLE);
            OpenWifiUtil.showWifiDlg(this);
        } else {
            webView.setVisibility(View.VISIBLE);
            errrorWebPage.setVisibility(View.GONE);
            webView.reload();
        }
    }

}
