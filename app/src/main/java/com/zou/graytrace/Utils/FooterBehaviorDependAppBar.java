package com.zou.graytrace.Utils;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;

/**
 * Created by zou on 2018/3/16.
 */

public class FooterBehaviorDependAppBar extends CoordinatorLayout.Behavior<View> {
    private static final String TAG = "Behavior";
    private int sinceDirectionChange;

    public FooterBehaviorDependAppBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    //当 dependency instanceof AppBarLayout 返回TRUE，将会调用onDependentViewChanged（）方法
//    @Override
//    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
//        return   dependency instanceof AppBarLayout;
//    }
//
//    @Override
//    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
//        //根据dependency top值的变化改变 child 的 translationY
//        float translationY = Math.abs(dependency.getTop());
//        child.setTranslationY(translationY);
//        return true;
//
//    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
//        if (dyConsumed > 0 && sinceDirectionChange < 0 || dyConsumed < 0 && sinceDirectionChange > 0) {
//            child.animate().cancel();
//            sinceDirectionChange = 0;
//        }
//        sinceDirectionChange += dyConsumed;
//        int visibility = child.getVisibility();
//            if (sinceDirectionChange > child.getHeight() && visibility == View.VISIBLE) {
//            hide(child);
//        } else {
//            if (sinceDirectionChange < 0 && (visibility == View.GONE || visibility == View
//                    .INVISIBLE)) {
//                show(child);
//            }
//        }
        if(dyUnconsumed>0){
            child.setTranslationY(0);
            return;
        }
        if(child.getTranslationY()+dyConsumed>child.getHeight()){
            child.setTranslationY(child.getHeight());
        }else if(child.getTranslationY()+dyConsumed<0){
            child.setTranslationY(0);
        }else{
            child.setTranslationY(child.getTranslationY() + dyConsumed);
        }


    }
}
