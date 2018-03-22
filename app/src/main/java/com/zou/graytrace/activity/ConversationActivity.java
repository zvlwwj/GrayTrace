package com.zou.graytrace.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.zou.graytrace.R;
import com.zou.graytrace.Utils.Constant;
import com.zou.graytrace.adapter.CommentRecyclerAdapter;
import com.zou.graytrace.bean.CommentBean;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zou on 2018/3/21.
 */

public class ConversationActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_conversation)
    Toolbar toolbar_conversation;
    @BindView(R.id.recyclerView_conversation)
    RecyclerView recyclerView_conversation;
    private ArrayList<CommentBean> conversationComments;
    private CommentRecyclerAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        conversationComments = (ArrayList<CommentBean>) getIntent().getSerializableExtra(Constant.INTENT_COMMENTS);

    }

    private void initView() {
        setSupportActionBar(toolbar_conversation);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        recyclerView_conversation.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommentRecyclerAdapter(conversationComments,this,true);
//        adapter.setOnUpvoteListener(new CommentRecyclerAdapter.OnUpvoteListener() {
//            @Override
//            public void onUpvote(int position) {
//                //点赞
//            }
//        });
        recyclerView_conversation.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
