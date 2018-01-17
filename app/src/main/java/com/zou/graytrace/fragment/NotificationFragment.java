package com.zou.graytrace.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zou.graytrace.R;

/**
 * Created by zou on 2018/1/17.
 */

public class NotificationFragment extends BaseFragment {
    private static NotificationFragment instance;

    public static synchronized NotificationFragment getInstance() {
        if (instance == null) {
            instance = new NotificationFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent(R.layout.fragment_notification);
    }

    @Override
    protected void onBaseCreateView() {
        super.onBaseCreateView();
        initView();
    }

    private void initView() {

    }
}
