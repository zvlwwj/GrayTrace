package com.zou.graytrace.activity;

import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.zou.graytrace.R;
import com.zou.graytrace.Utils.BottomNavigationViewHelper;
import com.zou.graytrace.Utils.Constant;
import com.zou.graytrace.application.GrayTraceApplication;
import com.zou.graytrace.bean.GsonLoginBean;
import com.zou.graytrace.fragment.HomePageFragment;
import com.zou.graytrace.fragment.MineFragment;
import com.zou.graytrace.fragment.NearFragment;
import com.zou.graytrace.fragment.NotificationFragment;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/*code is far away from bug with the animal protecting
    *  ┏┓　　　┏┓
    *┏┛┻━━━┛┻┓
    *┃　　　　　　　┃ 　
    *┃　　　━　　　┃
    *┃　┳┛　┗┳　┃
    *┃　　　　　　　┃
    *┃　　　┻　　　┃
    *┃　　　　　　　┃
    *┗━┓　　　┏━┛
    *　　┃　　　┃神兽保佑
    *　　┃　　　┃代码无BUG！
    *　　┃　　　┗━━━┓
    *　　┃　　　　　　　┣┓
    *　　┃　　　　　　　┏┛
    *　　┗┓┓┏━┳┓┏┛
    *　　　┃┫┫　┃┫┫
    *　　　┗┻┛　┗┻┛
    *　　　
    */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class MainActivity extends AppCompatActivity {
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottom_navigation;
    @BindView(R.id.fl_content)
    FrameLayout fl_content;
    private Fragment currentFragment;
    private GrayTraceApplication application;
    private GrayTraceApplication.PublicService publicService;
    private SharedPreferences sp_account;
    private boolean isLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(bottom_navigation);
        HomePageFragment homePageFragment = HomePageFragment.getInstance();
        switchContent(homePageFragment);
    }

    private void initData() {
        application = (GrayTraceApplication) getApplication();
        sp_account = application.getAccountSharedPreferences();
        isLogin = sp_account.getBoolean(Constant.SP_IS_LOGIN,false);
        //如果是登录状态，则去验证用户名/密码
        if(isLogin){
            Retrofit retrofit = application.getRetrofit();
            publicService = retrofit.create(GrayTraceApplication.PublicService.class);
            autoLogin();
        }else{
            //TODO 未登录状态
        }
    }

    //自动登录
    private void autoLogin() {
        String base64Account = sp_account.getString(Constant.SP_ACCOUNT,null);
        String base64Password = sp_account.getString(Constant.SP_PASSWORD,null);
        final String username = new String(Base64.decode(base64Account.getBytes(), Base64.DEFAULT));
        final String password = new String(Base64.decode(base64Password.getBytes(), Base64.DEFAULT));
        publicService.toLogin(username,password)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonLoginBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this,"服务器错误",Toast.LENGTH_SHORT).show();
                        sp_account.edit().putBoolean(Constant.SP_IS_LOGIN,false).apply();
                    }

                    @Override
                    public void onNext(GsonLoginBean gsonLoginBean) {
                        int code = gsonLoginBean.getCode();
                        switch (code){
                            case 0:
                                break;
                            case -1:
                                Toast.makeText(MainActivity.this,"密码已被更改，请重新登录",Toast.LENGTH_SHORT).show();
                                sp_account.edit().putBoolean(Constant.SP_IS_LOGIN,false).apply();
                                break;
                            case -2:
                                Toast.makeText(MainActivity.this,"该用户资料已被清除，请重新注册",Toast.LENGTH_SHORT).show();
                                sp_account.edit().putBoolean(Constant.SP_IS_LOGIN,false).apply();
                                break;
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.bottom_navigation_home_page:
//                    viewPager.setCurrentItem(0);
                    HomePageFragment homePageFragment = HomePageFragment.getInstance();
                    switchContent(homePageFragment);
                    return true;
                case R.id.bottom_navigation_near:
                    NearFragment nearFragment = NearFragment.getInstance();
                    switchContent(nearFragment);
//                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.bottom_navigation_notification:
                    NotificationFragment notificationFragment = NotificationFragment.getInstance();
                    switchContent(notificationFragment);
//                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.bottom_navigation_mine:
//                    viewPager.setCurrentItem(3);
                    MineFragment mineFragment = MineFragment.getInstance();
                    switchContent(mineFragment);
                    return true;
            }
            return false;
        }
    };



    /** 修改显示的内容 不会重新加载 **/
    public void switchContent(Fragment to) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        if(currentFragment == null){
            transaction.add(R.id.fl_content,to).commitAllowingStateLoss();
            currentFragment = to;
            return;
        }
        if (currentFragment != to) {
            if (!to.isAdded()) { // 先判断是否被add过
                transaction.hide(currentFragment).add(R.id.fl_content, to).commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(currentFragment).show(to).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
            }
            currentFragment = to;
        }
    }
}
