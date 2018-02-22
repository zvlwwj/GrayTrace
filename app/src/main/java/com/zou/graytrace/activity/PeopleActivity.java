package com.zou.graytrace.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.zou.graytrace.R;
import com.zou.graytrace.Utils.Constant;
import com.zou.graytrace.view.TextViewPeopleContainer;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zou on 2018/2/22.
 */

public class PeopleActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_people)
    Toolbar toolbar_people;
    @BindView(R.id.iv_people_cover)
    ImageView iv_people_cover;
    @BindView(R.id.tv_people_name)
    TextView tv_people_name;
    @BindView(R.id.tv_people_description)
    TextView tv_people_description;
    @BindView(R.id.textViewPeopleContainer)
    TextViewPeopleContainer textViewPeopleContainer;
    private String from;
    private String people_id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        from = getIntent().getStringExtra(Constant.INTENT_PEOPLE_FROM);
        people_id = getIntent().getStringExtra(Constant.INTENT_PEOPLE_ID);
    }

    private void initView() {
        setSupportActionBar(toolbar_people);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_people,menu);
        MenuItem editItem = menu.findItem(R.id.action_menu_people_edit);
        switch (from){
            case Constant.PEOPLE_FROM_CREATION:
                editItem.setVisible(true);
                break;
            case Constant.PEOPLE_FROM_MAIN:
                editItem.setVisible(false);
                break;
        }
        return super.onCreateOptionsMenu(menu);
    }
}
