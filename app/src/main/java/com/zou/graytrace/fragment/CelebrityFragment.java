package com.zou.graytrace.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zou.graytrace.R;

/**
 * Created by zou on 2018/1/17.
 */

public class CelebrityFragment extends BaseFragment {
    private static CelebrityFragment instance;

    public static synchronized CelebrityFragment getInstance() {
        if (instance == null) {
            instance = new CelebrityFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent(R.layout.fragment_home_celebrity);
    }

    @Override
    protected void onBaseCreateView() {
        super.onBaseCreateView();
        initView();
    }

    private void initView() {

    }
}
