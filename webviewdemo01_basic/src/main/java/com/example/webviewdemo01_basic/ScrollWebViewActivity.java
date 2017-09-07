package com.example.webviewdemo01_basic;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.webviewdemo01_basic.customView.MyScrollWebView;
import com.example.webviewdemo01_basic.utils.NetworkStatus;
import com.example.webviewdemo01_basic.utils.OpenWifiUtil;

/**
 * 创建日期：2017/9/6 on 上午11:24
 * 描述: WebView滚动事件的监听，并添加WebView加载网页设置进度条
 * 监听滚动事件一般都是设置setOnScrollChangedListener，可惜的是WebView并没有给我们提供这样的方法，
 * 但是我们可以重写WebView，覆盖里面的一个方法,然后再对外提供一个接口
 * protected void onScrollChanged(final int l, final int t, final int oldl,final int oldt){}
 * 作者: liangyang
 */
public class ScrollWebViewActivity extends AppCompatActivity implements View.OnClickListener {

    private HorizontalScrollView mNoticeScrollView;
    private TranslateAnimation mRigthToLeftAnim;
    private TextView txt_title;
    private FloatingActionButton btn_top;
    private MyScrollWebView webView;
    private long exitTime = 0;
    private final static float SCOLL_V = 0.5f;
    private ProgressBar progressBar;
    private LinearLayout errrorWebPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题栏,注意：这句代码要写在setContentView()前面
        getSupportActionBar().hide();
        setContentView(R.layout.activity_scroll_web_view);
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
            //覆写shouldOverrideUrlLoading实现内部显示网页
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

//            @Override
//            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                super.onReceivedError(view, errorCode, description, failingUrl);
//            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                //没有网络连接
                webView.setVisibility(View.GONE);
                errrorWebPage.setVisibility(View.VISIBLE);
            }
        });

        //调用loadView方法为WebView加入链接
        webView.loadUrl("http://www.baidu.com/");

        //设置WebView属性,允许执行JS脚本,不然加载出来的网页很难看
        webView.getSettings().setJavaScriptEnabled(true);

        //这里设置获取到的页面title
        this.webView.setWebChromeClient(new WebChromeClient() {

            //webView加载进度
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

            //获取网页标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                txt_title.setText(title);
            }

        });

        //自定义webView的滑动监听事件,这里做一个简单的判断，当页面发生滚动，显示那个FloatingActionButton
        //官方API的需要版本控制
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            webView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//                @Override
//                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//
//                }
//            });
//        }

        this.webView.setOnScrollChangedCallback(new MyScrollWebView.OnScrollChangedCallback() {
            @Override
            public void onScroll(int dx, int dy) {
                //这里做一个简单的判断，当页面发生滚动，显示那个FloatingActionButton
                if (dy > 0) {
                    btn_top.setVisibility(View.VISIBLE);
                } else {
                    btn_top.setVisibility(View.GONE);
                }
            }
        });

        //设置滚动条
        //setHorizontalScrollBarEnabled(false);//水平不显示
        this.webView.setHorizontalScrollBarEnabled(false);
        //setVerticalScrollBarEnabled(false); //垂直不显示
//        webView.setVerticalScrollBarEnabled(true);
        //setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);//滚动条在WebView内侧显示
        //setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY)//滚动条在WebView外侧显示
//        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        this.webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        //设置缩放及屏幕自适应
        webView.getSettings().setUseWideViewPort(true);//设定支持viewport
        webView.getSettings().setLoadWithOverviewMode(true);//自适应屏幕
        webView.getSettings().setBuiltInZoomControls(true);//设置可缩放
        webView.getSettings().setDisplayZoomControls(false);//隐藏缩放控件
        webView.getSettings().setSupportZoom(true);
//        webView.setInitialScale(25);//为25%，最小缩放等级,整个网页缩放
        //settings.setTextZoom(int);  或者 settings.setTextSize(TextSize.LARGER)
        //Android自带五个可选字体大小的值：SMALLEST(50%),SMALLER(75%),NORMAL(100%),LARGER(150%), LARGEST(200%)。
//        webView.getSettings().setTextZoom(25);

    }

    private void initView() {
        errrorWebPage = (LinearLayout) findViewById(R.id.web_view_error_page);
        RelativeLayout errrorWebPageBtn = (RelativeLayout) errrorWebPage.findViewById(R.id.online_error_rl);
        progressBar = (ProgressBar) findViewById(R.id.custom_progressBar);
        mNoticeScrollView = (HorizontalScrollView) findViewById(R.id.horiSv);
        Button btn_back = (Button) findViewById(R.id.btn_back);
        txt_title = (TextView) findViewById(R.id.txt_title);
        btn_top = (FloatingActionButton) findViewById(R.id.btn_top);
        Button btn_refresh = (Button) findViewById(R.id.btn_refresh);
        Button btn_clear = (Button) findViewById(R.id.btn_clear);
        webView = (MyScrollWebView) findViewById(R.id.web_view);

        btn_back.setOnClickListener(this);
        btn_refresh.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        btn_top.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                //关闭当前Activity
                finish();
                break;

            case R.id.btn_refresh:
                //刷新当前页面
                checkNet();
                break;

            case R.id.btn_top:
                //滚动到顶部
                webView.setScrollY(0);
                btn_top.setVisibility(View.GONE);
                break;

            case R.id.btn_clear:
                // webView清除缓存
                webView.clearCache(true);
                //刷新当前页面
                checkNet();
                break;

            case R.id.online_error_rl:
                //刷新当前页面
                checkNet();
                break;

            default:
                break;

        }

    }

    /**
     * 刷新当前网络状态
     */
    private void checkNet() {
        String networkStatus = NetworkStatus.getNetworkStatus(getApplicationContext());
        if (networkStatus.equals("false")) {
            //检测网络状态
            OpenWifiUtil.showWifiDlg(this);
            webView.setVisibility(View.GONE);
            errrorWebPage.setVisibility(View.VISIBLE);
        } else {
            webView.setVisibility(View.VISIBLE);
            errrorWebPage.setVisibility(View.GONE);
            webView.reload();
        }
    }
}
