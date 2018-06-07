package com.example.tcy.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NoSlideVpager extends ViewPager {
    public NoSlideVpager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public NoSlideVpager(Context context) {
        super(context);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return false;
    }
    public boolean onTouchEvent(MotionEvent arg0) {
        return false;
    }
}
