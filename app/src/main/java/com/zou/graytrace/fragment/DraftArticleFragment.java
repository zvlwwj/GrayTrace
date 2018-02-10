package com.zou.graytrace.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zou.graytrace.R;

/**
 * Created by zou on 2018/2/8.
 */

public class DraftArticleFragment extends BaseFragment{
    private static DraftArticleFragment instance;

    public static synchronized DraftArticleFragment getInstance() {
        if (instance == null) {
            instance = new DraftArticleFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent(R.layout.fragment_draft_article);
    }

    @Override
    protected void onBaseCreateView() {
        super.onBaseCreateView();
        initView();
    }

    private void initView() {

    }
}
