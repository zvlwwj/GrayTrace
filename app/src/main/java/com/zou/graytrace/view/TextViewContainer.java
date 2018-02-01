package com.zou.graytrace.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zou.graytrace.R;
import com.zou.graytrace.Utils.Tools;

import java.util.List;

/**
 * 自定义控件，TextView的容器
 * Created by zou on 2018/2/1.
 */

public class TextViewContainer extends LinearLayout {
    private int maxWidth;
    private TextView moreTextView;
    private static final int TextViewWidth = 50;//dp
    private boolean hasMoreTextView;
    public TextViewContainer(Context context) {
        super(context);
        init();
    }

    public TextViewContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        moreTextView = new TextView(getContext());
        moreTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        moreTextView.setText(R.string.more);
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public void addTextView(TextView child){
        child.setLayoutParams(new LayoutParams(Tools.dip2px(getContext(),TextViewWidth),LayoutParams.WRAP_CONTENT));
        child.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        child.setSingleLine();
        if(hasMoreTextView){
            super.addView(child,0);
            removeViewAt(getChildCount()-3);
            return;
        }
        int childWidthSum = 0;
        for(int i=0;i<getChildCount();i++){
            View childView = getChildAt(i);
            childWidthSum += childView.getMeasuredWidth();
        }
        int cWidth = Tools.dip2px(getContext(),TextViewWidth);
        if(childWidthSum+cWidth>maxWidth){
            if(childWidthSum + cWidth > maxWidth){
                removeViewAt(getChildCount()-2);
            }
            super.addView(moreTextView,getChildCount()-1);
            super.addView(child,0);
            hasMoreTextView = true;
        }else {
            super.addView(child,0);
        }
    }

}
