package com.zou.graytrace.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zou.graytrace.R;

/**
 * Created by zou on 2018/1/17.
 */

public class MineFragment extends BaseFragment {
    private static MineFragment instance;

    public static synchronized MineFragment getInstance() {
        if (instance == null) {
            instance = new MineFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent(R.layout.fragment_mine);
    }

    @Override
    protected void onBaseCreateView() {
        super.onBaseCreateView();
        initView();
    }

    private void initView() {

    }
}
