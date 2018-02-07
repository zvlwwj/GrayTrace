package com.zou.graytrace.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.zou.graytrace.Utils.Constant;
import com.zou.graytrace.application.GrayTraceApplication;
import com.zou.graytrace.R;
import com.zou.graytrace.bean.GsonLoginBean;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import retrofit2.Retrofit;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zoujingyi on 2018/1/27.
 */

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_login)
    Toolbar toolbar_login;
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.tv_user_name)
    AutoCompleteTextView tv_user_name;
    private GrayTraceApplication application;
    private GrayTraceApplication.PublicService publicService;
    private SharedPreferences sp;
    public static final int RESULT_LOGIN_OK = 1000;
    public static final int RESULT_LOGIN_CANCEL = 1001;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        application = (GrayTraceApplication) getApplication();
        sp = application.getAccountSharedPreferences();
        Retrofit retrofit = application.getRetrofit();
        publicService = retrofit.create(GrayTraceApplication.PublicService.class);
    }

    private void initView(){
        setSupportActionBar(toolbar_login);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    /**
     *密码输入框按键监听
     */
    @OnEditorAction(R.id.et_password)
    boolean passwordEditorAction(int actionId){
        if ( actionId == EditorInfo.IME_ACTION_DONE) {
            Toast.makeText(this,"login",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                //离开登陆界面
                setResult(RESULT_LOGIN_CANCEL);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 登录
     */
    @OnClick(R.id.btn_login)
    void login(){
        final String username = tv_user_name.getText().toString();
        final String password = et_password.getText().toString();
        publicService.toLogin(username,password)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<GsonLoginBean>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(LoginActivity.this,"服务器错误",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(GsonLoginBean gsonLoginBean) {
                    int code = gsonLoginBean.getCode();
                    switch (code){
                        case 0:
                            Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                            //TODO 保存到偏好设置
                            String base64Account = Base64.encodeToString(username.getBytes(), Base64.DEFAULT);
                            String base64Password = Base64.encodeToString(password.getBytes(),Base64.DEFAULT);
                            int user_id = gsonLoginBean.getUser_id();
                            sp.edit().putString(Constant.SP_ACCOUNT,base64Account).apply();
                            sp.edit().putString(Constant.SP_PASSWORD,base64Password).apply();
                            sp.edit().putInt(Constant.SP_USER_ID,user_id).apply();
                            sp.edit().putBoolean(Constant.SP_IS_LOGIN,true).apply();
                            setResult(RESULT_LOGIN_OK);
                            finish();
                            break;
                        case -1:
                            Toast.makeText(LoginActivity.this,"用户名/密码错误",Toast.LENGTH_SHORT).show();
                            break;
                        case -2:
                            Toast.makeText(LoginActivity.this,"没有该用户",Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
    }
}
