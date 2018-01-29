package com.zou.graytrace.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

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
    GrayTraceApplication application;
    LoginService loginService;
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
        Retrofit retrofit = application.getRetrofit();
        loginService = retrofit.create(LoginService.class);
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
        String username = tv_user_name.getText().toString();
        String password = et_password.getText().toString();
        loginService.toLogin(username,password)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<GsonLoginBean>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(getApplicationContext(),"服务器错误",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(GsonLoginBean gsonLoginBean) {
                    int code = gsonLoginBean.getCode();
                    String msg = gsonLoginBean.getMsg();
                    Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();

                }
            });
    }

    interface LoginService{
        @POST("login")
        Observable<GsonLoginBean> toLogin(@Query("username")String username, @Query("password") String pwd);
    }
}
