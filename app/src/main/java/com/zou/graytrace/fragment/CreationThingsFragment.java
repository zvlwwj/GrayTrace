package com.zou.graytrace.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zou.graytrace.R;

/**
 * Created by zou on 2018/2/8.
 */

public class CreationThingsFragment extends BaseFragment{
    private static CreationThingsFragment instance;

    public static synchronized CreationThingsFragment getInstance() {
        if (instance == null) {
            instance = new CreationThingsFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent(R.layout.fragment_creation_things);
    }

    @Override
    protected void onBaseCreateView() {
        super.onBaseCreateView();
        initView();
    }

    private void initView() {

    }
}
