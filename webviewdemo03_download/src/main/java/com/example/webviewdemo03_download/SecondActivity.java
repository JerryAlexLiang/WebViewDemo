package com.example.webviewdemo03_download;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;

/**
 * 创建日期：2017/9/12 on 上午10:23
 * 描述:如果不想把下载文件放到默认路径下，或者想自己定义文件名等等，都可以自己来写一个线程来下载文件
 * 作者: liangyang
 */
public class SecondActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private long exitTime = 0;
    private DownloadManager downloadManager;
    private long enqueue;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题栏,注意：这句代码要写在setContentView()前面
        getSupportActionBar().hide();
        setContentView(R.layout.activity_second);
        //Android6.0动态申请SD卡读写的权限
        verifyStoragePermissions(this);
        progressBar = (ProgressBar) findViewById(R.id.custom_progressBar);
        webView = (WebView) findViewById(R.id.web_view);
//        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

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

        // 然后，找到下载的地方，这个时候点击下载，调用自己写的下载程序
        //WebView默认没有开启文件下载的功能，如果要实现文件下载的功能，需要设置WebView的DownloadListener
        webView.setDownloadListener(new DownloadListener() {


            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                // 实现自己封装的下载逻辑线程
                Log.e("tag", "onDownloadStart被调用：下载链接：" + url);
                startDownload(url);
            }
        });
    }

    /**
     * 下载文件
     *
     * @param url
     */
    private void startDownload(String url) {
        downloadManager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        //设置请求的Mime
        //这是安卓.apk文件的类型。有些机型必须设置此方法，才能在下载完成后，点击通知栏的Notification时，
        //才能正确的打开安装界面。不然会弹出一个Toast（can not open file）
        request.setMimeType("application/vnd.android.package-archive");
        request.setTitle("fileName.apk");
        request.setDescription("文件下载");
        //如果我们希望下载的文件可以被系统的Downloads应用扫描到并管理，
        //我们需要调用Request对象的setVisibleInDownloadsUi方法，传递参数true。
        request.setVisibleInDownloadsUi(true);

        //存放路径-方法1:目录: Android -> data -> com.app -> files -> Download -> 微信.apk
        //这个文件是你的应用所专用的,软件卸载后，下载的文件将随着卸载全部被删除
//        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS,"fileName.apk");

        //存放路径-方法2:下载的文件存放地址 SD卡 download文件夹，pp.jpg,软件卸载后，下载的文件会保留
        //在SD卡上创建一个文件夹
//        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//            //创建一个文件夹对象，赋值为外部存储器的目录
//            File sdcardDir = Environment.getExternalStorageDirectory();
//            //得到一个路径，内容是sdcard的文件夹路径和名字
//            String path = sdcardDir.getPath() + "/mydownfile";
//            File path1 = new File(path);
//            if (!path1.exists()) {
//                //若不存在，创建目录，可以在应用启动的时候创建
//                path1.mkdirs();
//                setTitle("path ok,path:" + path);
//            }else {
//                setTitle("false");
//                return;
//            }
//            request.setDestinationInExternalPublicDir(path, "fileName.apk");
//        }


        //存放路径-方法3:文件将存放在外部存储的download文件内，如果无此文件夹，创建之，如果有，下面将返回false。
        //系统有个下载文件夹，比如小米手机系统下载文件夹  SD卡--> Download文件夹
        //创建目录
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir();
        //设置文件存放路径
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "fileName.apk");

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        enqueue = downloadManager.enqueue(request);
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

    /**
     * Android6.0动态申请SD卡读写的权限
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
