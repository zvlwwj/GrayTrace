package com.zou.graytrace.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.widget.Toast;

import com.zou.graytrace.R;
import com.zou.graytrace.activity.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @OnClick(R.id.cardView_Login)
    void toLogin(){
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }
}
