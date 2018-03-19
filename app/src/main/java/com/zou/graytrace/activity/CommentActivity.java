package com.zou.graytrace.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.zou.graytrace.R;
import com.zou.graytrace.Utils.Constant;
import com.zou.graytrace.Utils.Tools;
import com.zou.graytrace.application.GrayTraceApplication;
import com.zou.graytrace.bean.GsonCommitCommentResultBean;
import com.zou.graytrace.bean.GsonGetCommentResultBean;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zou on 2018/3/19.
 */

public class CommentActivity extends AppCompatActivity {
    @BindView(R.id.recyclerView_comment)
    RecyclerView recyclerView_comment;
    @BindView(R.id.et_comment)
    EditText et_comment;
    private SharedPreferences sharedPreferences;
    private int reply_id = -1;
    private CommitCommentService commitCommentService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);
        initData();
        iniView();
    }

    private void initData() {
        GrayTraceApplication application = (GrayTraceApplication) getApplication();
        sharedPreferences = application.getAccountSharedPreferences();
        commitCommentService = application.getRetrofit().create(CommitCommentService.class);
    }

    private void iniView(){

    }

    //提交评论
    @OnClick(R.id.iv_commit_comment)
    public void commitComment(){
        String text = et_comment.getText().toString();
        if(text.isEmpty()){
            Toast.makeText(CommentActivity.this,R.string.content_empty_error,Toast.LENGTH_SHORT).show();
            return;
        }
        int uploader_id = sharedPreferences.getInt(Constant.SP_USER_ID, 0);
        String type = getIntent().getStringExtra(Constant.INTENT_COMMENT_TYPE);
        int type_id = getIntent().getIntExtra(Constant.INTENT_COMMENT_TYPE_ID,-1);
        String time_stamp = Tools.getTimeStamp();
        if(type_id!=-1) {
            commitCommentService.commitComment(text, uploader_id, reply_id, type, type_id,time_stamp)
                    .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<GsonCommitCommentResultBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(CommentActivity.this,R.string.commit_error,Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(GsonCommitCommentResultBean gsonCommitCommentResultBean) {
                            switch (gsonCommitCommentResultBean.getCode()){
                                case 0:
                                    Toast.makeText(CommentActivity.this,R.string.commit_success,Toast.LENGTH_SHORT).show();
                                    et_comment.setText("");
                                    closeInputMethod();
                                    break;
                                default:
                                    Toast.makeText(CommentActivity.this,R.string.commit_error,Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });
        }else{
            Toast.makeText(this,R.string.commit_error,Toast.LENGTH_SHORT).show();
        }
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

    interface CommitCommentService{
        @POST("comment/add")
        Observable<GsonCommitCommentResultBean> commitComment(@Query("text")String text,@Query("uploader_id")int uploader_id,@Query("reply_id")int reply_id,@Query("type")String type,@Query("type_id")int type_id,@Query("time_stamp")String time_stamp);
        @POST("comment/get")
        Observable<GsonGetCommentResultBean> getComment(@Query("type")String type, @Query("type_id")int type_id);
    }

    private void closeInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
            imm.hideSoftInputFromWindow(et_comment.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
