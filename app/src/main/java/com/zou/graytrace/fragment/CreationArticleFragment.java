package com.zou.graytrace.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zou.graytrace.R;

/**
 * Created by zou on 2018/2/8.
 */

public class CreationArticleFragment extends BaseFragment{
    private static CreationArticleFragment instance;

    public static synchronized CreationArticleFragment getInstance() {
        if (instance == null) {
            instance = new CreationArticleFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent(R.layout.fragment_creation_article);
    }

    @Override
    protected void onBaseCreateView() {
        super.onBaseCreateView();
        initView();
    }

    private void initView() {

    }
}
