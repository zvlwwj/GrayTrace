package com.zou.graytrace.activity;

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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zou.graytrace.R;
import com.zou.graytrace.Utils.BottomNavigationViewHelper;
import com.zou.graytrace.fragment.HomePageFragment;
import com.zou.graytrace.fragment.MineFragment;
import com.zou.graytrace.fragment.NearFragment;
import com.zou.graytrace.fragment.NotificationFragment;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
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
    private List<View> viewList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        HomePageFragment homePageFragment = HomePageFragment.getInstance();
        switchContent(homePageFragment);
    }


    private void initView() {
        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // If BottomNavigationView has more than 3 items, using reflection to disable shift mode
        BottomNavigationViewHelper.disableShiftMode(bottom_navigation);

//        View view1 = getLayoutInflater().inflate(R.layout.item_view_pager_home, null);
//        View view2 = getLayoutInflater().inflate(R.layout.item_view_pager_near, null);
//        View view3 = getLayoutInflater().inflate(R.layout.item_view_pager_notification, null);
//        View view4 = getLayoutInflater().inflate(R.layout.item_view_pager_mine, null);

//        viewList = new ArrayList<>();
//        viewList.add(view1);
//        viewList.add(view2);
//        viewList.add(view3);
//        viewList.add(view4);
//
//        view_pager_main.setAdapter(pagerAdapter);
//        view_pager_main.addOnPageChangeListener(pageChangeListener);
//        view_pager_main.setPageTransformer(true, new MyPageTransformer());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private PagerAdapter pagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }
    };


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
