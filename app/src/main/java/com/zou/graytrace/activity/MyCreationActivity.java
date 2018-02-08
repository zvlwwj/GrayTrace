package com.zou.graytrace.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.zou.graytrace.R;
import com.zou.graytrace.Utils.Constant;
import com.zou.graytrace.adapter.FragmentAdapter;
import com.zou.graytrace.fragment.CreationArticleFragment;
import com.zou.graytrace.fragment.CreationEventsFragment;
import com.zou.graytrace.fragment.CreationPeopleFragment;
import com.zou.graytrace.fragment.CreationThingsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zou on 2018/2/8.
 */

public class MyCreationActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_mine_creation)
    Toolbar toolbar_mine_creation;
    @BindView(R.id.tab_layout_mine_creation)
    TabLayout tabLayout;
    @BindView(R.id.view_pager_creation)
    ViewPager view_pager_creation;
    private int draft_count;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_mine_creation);
        ButterKnife.bind(this);
        //返回键
        setSupportActionBar(toolbar_mine_creation);
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
        fragments.add(CreationPeopleFragment.getInstance());
        fragments.add(CreationThingsFragment.getInstance());
        fragments.add(CreationEventsFragment.getInstance());
        fragments.add(CreationArticleFragment.getInstance());
        view_pager_creation.setOffscreenPageLimit(2);
        FragmentAdapter mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments, titles);
        view_pager_creation.setAdapter(mFragmentAdapter);
        tabLayout.setupWithViewPager(view_pager_creation);
        tabLayout.setTabsFromPagerAdapter(mFragmentAdapter);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        draft_count = getIntent().getIntExtra(Constant.INTENT_DRAFT_BOX_COUNT,0);
        if(draft_count!=0){
            menu.findItem(R.id.action_draft_box).setTitle(getResources().getString(R.string.draft_box)+"("+draft_count+")");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_creation,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_draft_box:
                //草稿箱
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
