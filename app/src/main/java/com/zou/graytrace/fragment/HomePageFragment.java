package com.zou.graytrace.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.zou.graytrace.R;
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
        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.tab_title_attention));
        titles.add(getString(R.string.tab_title_celebrity));
        titles.add(getString(R.string.tab_title_memento));

        tabLayout.addTab(tabLayout.newTab().setText(titles.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(titles.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(titles.get(2)));

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(AttentionFragment.getInstance());
        fragments.add(CelebrityFragment.getInstance());
        fragments.add(MementoFragment.getInstance());

        viewPager.setOffscreenPageLimit(2);

        FragmentAdapter mFragmentAdapter = new FragmentAdapter(getActivity().getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(mFragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(mFragmentAdapter);
    }
}
