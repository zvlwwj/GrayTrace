package com.zou.graytrace.fragment;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.zou.graytrace.R;
import com.zou.graytrace.Utils.Tools;
import com.zou.graytrace.activity.UploadCelebrityActivity;
import com.zou.graytrace.adapter.FragmentAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by zou on 2018/1/17.
 */

public class HomePageFragment extends BaseFragment {
    private static HomePageFragment instance;
    @BindView(R.id.tab_layout_home_page)
    TabLayout tabLayout;
    @BindView(R.id.view_pager_home)
    ViewPager viewPager;
    @BindView(R.id.toolbar_main)
    Toolbar toolbar;

    private AppCompatActivity mActivity;
    public static synchronized HomePageFragment getInstance() {
        if (instance == null) {
            instance = new HomePageFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent(R.layout.fragment_home);
    }

    @Override
    protected void onBaseCreateView() {
        super.onBaseCreateView();
        initView();
    }

    private void initView() {
        setHasOptionsMenu(true);
        mActivity = (AppCompatActivity)getActivity();
        mActivity.setSupportActionBar(toolbar);
        mActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.tab_title_attention));
        titles.add(getString(R.string.tab_title_recommendation));
        titles.add(getString(R.string.tab_title_leaderboards));

        tabLayout.addTab(tabLayout.newTab().setText(titles.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(titles.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(titles.get(2)));

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(AttentionFragment.getInstance());
        fragments.add(CelebrityFragment.getInstance());
        fragments.add(MementoFragment.getInstance());

        viewPager.setOffscreenPageLimit(2);

        FragmentAdapter mFragmentAdapter = new FragmentAdapter(mActivity.getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(mFragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(mFragmentAdapter);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);
        MenuItem searchItem = menu.findItem(R.id.action_menu_main_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setMaxWidth(Tools.dip2px(getContext(),300));
        final Point p = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(p);
        // Create LayoutParams with width set to screen's width
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(p.x, Toolbar.LayoutParams.MATCH_PARENT);
        searchView.setLayoutParams(params);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    Toast.makeText(getContext(),"onClick",Toast.LENGTH_SHORT).show();
                }
            }
        });
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_menu_main_upload:
                //TODO 跳转到上传Activity
                Intent intent = new Intent(getActivity(), UploadCelebrityActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
