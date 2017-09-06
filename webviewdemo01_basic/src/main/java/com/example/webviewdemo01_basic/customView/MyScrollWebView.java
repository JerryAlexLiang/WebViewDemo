package com.example.webviewdemo01_basic.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * 创建日期：2017/9/6 on 上午11:24
 * 描述: WebView滚动事件的监听
 * 监听滚动事件一般都是设置setOnScrollChangedListener，可惜的是WebView并没有给我们提供这样的方法，
 * 但是我们可以重写WebView，覆盖里面的一个方法,然后再对外提供一个接口
 * protected void onScrollChanged(final int l, final int t, final int oldl,final int oldt){}
 * 作者: liangyang
 */
public class MyScrollWebView extends WebView {

    /**
     * 接口对象
     */
    private OnScrollChangedCallback mOnScrollChangedCallback;

    /**
     * 构造函数
     *
     * @param context
     */
    public MyScrollWebView(Context context) {
        super(context);
    }

    /**
     * 构造函数
     *
     * @param context
     */
    public MyScrollWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 构造函数
     *
     * @param context
     */
    public MyScrollWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 重写onScrollChanged方法
     *
     * @param l
     * @param t
     * @param oldl
     * @param oldt
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangedCallback != null) {
            mOnScrollChangedCallback.onScroll(l - oldl, t - oldt);
        }

    }

    /**
     * get方法
     *
     * @return
     */
    public OnScrollChangedCallback getOnScrollChangedCallback() {
        return mOnScrollChangedCallback;
    }

    /**
     * set方法
     *
     * @param mOnScrollChangedCallback
     */
    public void setOnScrollChangedCallback(OnScrollChangedCallback mOnScrollChangedCallback) {
        this.mOnScrollChangedCallback = mOnScrollChangedCallback;
    }

    /**
     * 对外接口
     */
    public static interface OnScrollChangedCallback {
        //这里的dx和dy代表的是x轴和y轴上的偏移量，你也可以自己把l, t, oldl, oldt四个参数暴露出来
        void onScroll(int dx, int dy);
    }


}
