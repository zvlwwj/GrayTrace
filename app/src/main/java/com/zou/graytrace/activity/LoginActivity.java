package com.zou.graytrace.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.zou.graytrace.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zoujingyi on 2018/1/27.
 */

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_login)
    Toolbar toolbar_login;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
    }

    private void initView(){
        setSupportActionBar(toolbar_login);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }
}
