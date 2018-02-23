package com.zou.graytrace.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zou.graytrace.R;
import com.zou.graytrace.Utils.Tools;

/**
 * 自定义控件，TextView的容器,用于显示人物中的事件
 * Created by zou on 2018/2/1.
 */

public class TextViewPeopleContainer extends LinearLayout {
    public TextViewPeopleContainer(Context context) {
        super(context);
        init();
    }

    public TextViewPeopleContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewPeopleContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void addEvent(String event_title,String event_text){
        TextView tv_title = new TextView(getContext());
        tv_title.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
        tv_title.setText(event_title);
        tv_title.setTextSize(22);
        addView(tv_title);
        //TODO 显示富文本(图片和影像)
        TextView tv_text = new TextView(getContext());
        tv_text.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
        tv_text.setText(event_text);
        addView(tv_text);
    }
}
