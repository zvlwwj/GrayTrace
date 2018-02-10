package com.zou.graytrace.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.zou.graytrace.R;
import com.zou.graytrace.adapter.FragmentAdapter;
import com.zou.graytrace.fragment.DraftArticleFragment;
import com.zou.graytrace.fragment.DraftEventsFragment;
import com.zou.graytrace.fragment.DraftPeopleFragment;
import com.zou.graytrace.fragment.DraftThingsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zou on 2018/2/10.
 */

public class DraftBoxActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_draft_box)
    Toolbar toolbar_draft_box;
    @BindView(R.id.tab_layout_draft_box)
    TabLayout tabLayout;
    @BindView(R.id.view_pager_draft_box)
    ViewPager view_pager_draft_box;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft_box);
        ButterKnife.bind(this);
        iniView();
    }

    private void iniView() {
        //返回键
        setSupportActionBar(toolbar_draft_box);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //tabLayout
        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.tab_creation_people));
        titles.add(getString(R.string.tab_creation_things));
        titles.add(getString(R.string.tab_creation_events));
        titles.add(getString(R.string.tab_creation_article));
        tabLayout.addTab(tabLayout.newTab().setText(titles.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(titles.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(titles.get(2)));
        tabLayout.addTab(tabLayout.newTab().setText(titles.get(3)));

        //viewPager
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(DraftPeopleFragment.getInstance());
        fragments.add(DraftThingsFragment.getInstance());
        fragments.add(DraftEventsFragment.getInstance());
        fragments.add(DraftArticleFragment.getInstance());
        view_pager_draft_box.setOffscreenPageLimit(2);
        FragmentAdapter mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments, titles);
        view_pager_draft_box.setAdapter(mFragmentAdapter);
        tabLayout.setupWithViewPager(view_pager_draft_box);
        tabLayout.setTabsFromPagerAdapter(mFragmentAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
