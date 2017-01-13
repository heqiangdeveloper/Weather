package com.heqiang.weather;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.text.TextUtils;

public class MarqueeTextView extends HorizontalScrollView implements Runnable {
    
	int currentScrollX = 0;// 当前滚动的位置
    TextView tv;

    public MarqueeTextView(Context context) {
        super(context);
        initView(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    void initView(Context context){
        View v = LayoutInflater.from(context).inflate(R.layout.scroll_layout, null);
        tv = (TextView)v.findViewById(R.id.foreweathertxt);
        this.addView(v);
    }

    public void setText(String text){
        tv.setText(text);
        startScroll();
    }
    
    public void setTextSize(Float size){
    	tv.setTextSize(size);
    	startScroll();
    }

    private void startScroll(){
        this.removeCallbacks(this);
        post(this);
    }

    public void run() {  Log.i("thread", "thread is" + Thread.currentThread());//result is : Main Thread
        // TODO Auto-generated method stub
        currentScrollX ++;// 滚动速度
        scrollTo(currentScrollX, 0);

        if (currentScrollX >= tv.getWidth()) {
                scrollTo(0, 0);
                currentScrollX = 0;
        }
        postDelayed(this, 50);
    }
}
