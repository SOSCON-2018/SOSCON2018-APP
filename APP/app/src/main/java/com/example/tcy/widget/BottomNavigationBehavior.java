package com.example.tcy.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.example.tcy.app.*;

public class BottomNavigationBehavior extends CoordinatorLayout.Behavior<View>{
    private ObjectAnimator showAnimation, fadeAnimation;
    MainActivity mActivity=MainActivity.mainActivity;
    public BottomNavigationBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes== ViewCompat.SCROLL_AXIS_VERTICAL;
    }
    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
            if (dy > 3) {// 上滑隐藏
                if (fadeAnimation == null) {
                    fadeAnimation = ObjectAnimator.ofFloat(child, "translationY", 0, child.getHeight());
                    fadeAnimation.setDuration(350);
                }
                if (!fadeAnimation.isRunning() && child.getTranslationY() <= 0) {
                    fadeAnimation.start();
                }
            } else if (dy < -3) {// 下滑显示
                if (showAnimation == null) {
                    showAnimation = ObjectAnimator.ofFloat(child, "translationY", child.getHeight(), 0);
                    showAnimation.setDuration(350);
                }
                if (!showAnimation.isRunning() && child.getTranslationY() >= child.getHeight()) {
                    showAnimation.start();
                }
            }
    }
}
