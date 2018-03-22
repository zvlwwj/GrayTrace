package com.zou.graytrace.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zou.graytrace.R;
import com.zou.graytrace.Utils.Constant;
import com.zou.graytrace.Utils.Tools;
import com.zou.graytrace.adapter.CommentRecyclerAdapter;
import com.zou.graytrace.application.GrayTraceApplication;
import com.zou.graytrace.bean.CommentBean;
import com.zou.graytrace.bean.GsonCommitCommentResultBean;
import com.zou.graytrace.bean.GsonCommonResultBean;
import com.zou.graytrace.bean.GsonDeleteResultBean;
import com.zou.graytrace.bean.GsonGetCommentResultBean;


import java.util.ArrayList;

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
    @BindView(R.id.toolbar_comment)
    Toolbar toolbar_comment;
    private SharedPreferences sharedPreferences;
    private int reply_id = -1;
    private CommentService commentService;
    private ArrayList<CommentBean> commentBeans;
    private CommentRecyclerAdapter adapter;
    private BottomSheetDialog mBottomSheetDialog;
    private String reply_nickname;
    private int user_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        GrayTraceApplication application = (GrayTraceApplication) getApplication();
        sharedPreferences = application.getAccountSharedPreferences();
        commentService = application.getRetrofit().create(CommentService.class);
        user_id = sharedPreferences.getInt(Constant.SP_USER_ID,0);
        getCommentList();
    }



    private void initView(){
        setSupportActionBar(toolbar_comment);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void getCommentList() {
        String type = getIntent().getStringExtra(Constant.INTENT_COMMENT_TYPE);
        int type_id = getIntent().getIntExtra(Constant.INTENT_COMMENT_TYPE_ID,-1);
        if(type_id!=-1) {
            commentService.getComment(type,type_id,user_id).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<GsonGetCommentResultBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(CommentActivity.this,R.string.get_data_error,Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(GsonGetCommentResultBean gsonGetCommentResultBean) {
                            switch (gsonGetCommentResultBean.getCode()){
                                case 0:
                                    for (GsonGetCommentResultBean.Info info:gsonGetCommentResultBean.getInfos()) {
                                        CommentBean commentBean = new CommentBean();
                                        commentBean.setText(info.getText());
                                        commentBean.setAvatar_url(info.getAvatar_url());
                                        commentBean.setComment_id(info.getComment_id());
                                        commentBean.setUploader_id(info.getUploader_id());
                                        commentBean.setNick_name(info.getNick_name());
                                        commentBean.setReply_id(info.getReply_id());
                                        commentBean.setTime_stamp(info.getTime_stamp());
                                        commentBean.setType(info.getType());
                                        commentBean.setType_id(info.getType_id());
                                        commentBean.setUpvote_count(info.getUpvote_count());
                                        commentBean.setIs_upvote(info.isIs_upvote());
                                        if(commentBeans == null){
                                            commentBeans = new ArrayList<CommentBean>();
                                        }
                                        commentBeans.add(commentBean);
                                    }
                                    initRecyclerView();

                                    break;
                                default:
                                    Toast.makeText(CommentActivity.this,R.string.get_data_error,Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });
        }
    }

    private void initRecyclerView() {
        recyclerView_comment.setLayoutManager(new LinearLayoutManager(CommentActivity.this));
        adapter = new CommentRecyclerAdapter(commentBeans,CommentActivity.this,false);
        adapter.setOnItemClickListener(new CommentRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                if(mBottomSheetDialog == null) {
                    mBottomSheetDialog = new BottomSheetDialog(CommentActivity.this);
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_bottom_reply, null);
                    mBottomSheetDialog.setContentView(dialogView);
                    LinearLayout ll_copy = dialogView.findViewById(R.id.ll_copy);
                    LinearLayout ll_reply = dialogView.findViewById(R.id.ll_reply);
                    LinearLayout ll_delete = dialogView.findViewById(R.id.ll_delete);
                    ll_copy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //复制回复内容
                            copy(commentBeans.get(position).getText());
                            Toast.makeText(CommentActivity.this,R.string.copy_success,Toast.LENGTH_SHORT).show();
                            mBottomSheetDialog.dismiss();
                        }
                    });
                    ll_reply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            reply_id = commentBeans.get(position).getComment_id();
                            reply_nickname = commentBeans.get(position).getNick_name();
                            et_comment.setText("");
                            et_comment.setHint(getString(R.string.reply_to)+reply_nickname);
                            mBottomSheetDialog.dismiss();
                        }
                    });
                    //TODO 删除评论功能暂时不做
                    ll_delete.setVisibility(View.GONE);
//                    if(user_id == commentBeans.get(position).getUploader_id()){
//                        ll_delete.setVisibility(View.VISIBLE);
//                    }else{
//                        ll_delete.setVisibility(View.GONE);
//                    }
//                    ll_delete.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            //删除评论
//                            deleteComment(commentBeans.get(position).getComment_id(),user_id);
//                        }
//                    });
                }
                mBottomSheetDialog.show();
            }
        });
        adapter.setOnLookConversationClickListener(new CommentRecyclerAdapter.OnLookConversationClickListener() {
            @Override
            public void onLookConversationClick(int position) {
                //查看对话
                CommentBean currentComment = commentBeans.get(position);
                ArrayList<CommentBean> conversationComments = new ArrayList<CommentBean>();
                for(CommentBean commentBean:commentBeans){
                    if(currentComment.getComment_id()==commentBean.getReply_id()
                            ||currentComment.getReply_id()==commentBean.getComment_id()
                            ||currentComment.getComment_id()==commentBean.getComment_id()){
                        conversationComments.add(commentBean);
                    }
                }
                Intent intent = new Intent(CommentActivity.this,ConversationActivity.class);
                intent.putExtra(Constant.INTENT_COMMENTS,conversationComments);
                startActivity(intent);
            }
        });
        adapter.setOnUpvoteListener(new CommentRecyclerAdapter.OnUpvoteListener() {
            @Override
            public void onUpvote(int position,boolean isChecked) {
                //点赞
                int comment_id = commentBeans.get(position).getComment_id();
                if(isChecked){
                    upvote(comment_id,user_id,position);
                }else{
                    cancelUpvote(comment_id,user_id,position);
                }
            }
        });
        recyclerView_comment.setAdapter(adapter);
    }

    /**
     * 点赞
     * @param comment_id
     * @param user_id
     */
    private void upvote(int comment_id, int user_id, final int position){
        commentService.upvote(comment_id,user_id)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonCommonResultBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(CommentActivity.this,R.string.upvote_error,Toast.LENGTH_SHORT).show();
                        CommentRecyclerAdapter.ViewHolder viewHolder = (CommentRecyclerAdapter.ViewHolder) recyclerView_comment.findViewHolderForAdapterPosition(position);
                        viewHolder.iv_upvote.setSelected(!viewHolder.iv_upvote.isSelected());
                        viewHolder.tv_upvote_count.setSelected(!viewHolder.tv_upvote_count.isSelected());
                        int count = Integer.parseInt(viewHolder.tv_upvote_count.getText().toString());
                        if(viewHolder.tv_upvote_count.isSelected()){
                            count ++ ;
                        }else{
                            count --;
                        }
                        viewHolder.tv_upvote_count.setText(String.valueOf(count));
                    }

                    @Override
                    public void onNext(GsonCommonResultBean gsonCommonResultBean) {
                        switch (gsonCommonResultBean.getCode()){
                            case 0:

                                break;
                            default:
                                Toast.makeText(CommentActivity.this,R.string.upvote_error,Toast.LENGTH_SHORT).show();
                                CommentRecyclerAdapter.ViewHolder viewHolder = (CommentRecyclerAdapter.ViewHolder) recyclerView_comment.findViewHolderForAdapterPosition(position);
                                viewHolder.iv_upvote.setSelected(!viewHolder.iv_upvote.isSelected());
                                viewHolder.tv_upvote_count.setSelected(!viewHolder.tv_upvote_count.isSelected());
                                int count = Integer.parseInt(viewHolder.tv_upvote_count.getText().toString());
                                if(viewHolder.tv_upvote_count.isSelected()){
                                    count ++ ;
                                }else{
                                    count --;
                                }
                                viewHolder.tv_upvote_count.setText(String.valueOf(count));
                                break;
                        }
                    }
                });
    }

    /**
     * 取消点赞
     * @param comment_id
     * @param user_id
     */
    private void cancelUpvote(int comment_id, int user_id, final int position){
        commentService.cancelUpvote(comment_id,user_id)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonCommonResultBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(CommentActivity.this,R.string.upvote_error,Toast.LENGTH_SHORT).show();
                        CommentRecyclerAdapter.ViewHolder viewHolder = (CommentRecyclerAdapter.ViewHolder) recyclerView_comment.findViewHolderForAdapterPosition(position);
                        viewHolder.iv_upvote.setSelected(!viewHolder.iv_upvote.isSelected());
                        viewHolder.tv_upvote_count.setSelected(!viewHolder.tv_upvote_count.isSelected());
                        int count = Integer.parseInt(viewHolder.tv_upvote_count.getText().toString());
                        if(viewHolder.tv_upvote_count.isSelected()){
                            count ++ ;
                        }else{
                            count --;
                        }
                        viewHolder.tv_upvote_count.setText(String.valueOf(count));
                    }

                    @Override
                    public void onNext(GsonCommonResultBean gsonCommonResultBean) {
                        switch (gsonCommonResultBean.getCode()){
                            case 0:

                                break;
                            default:
                                Toast.makeText(CommentActivity.this,R.string.upvote_error,Toast.LENGTH_SHORT).show();
                                CommentRecyclerAdapter.ViewHolder viewHolder = (CommentRecyclerAdapter.ViewHolder) recyclerView_comment.findViewHolderForAdapterPosition(position);
                                viewHolder.iv_upvote.setSelected(!viewHolder.iv_upvote.isSelected());
                                viewHolder.tv_upvote_count.setSelected(!viewHolder.tv_upvote_count.isSelected());
                                int count = Integer.parseInt(viewHolder.tv_upvote_count.getText().toString());
                                if(viewHolder.tv_upvote_count.isSelected()){
                                    count ++ ;
                                }else{
                                    count --;
                                }
                                viewHolder.tv_upvote_count.setText(String.valueOf(count));
                                break;
                        }
                    }
                });
    }

    /**
     * 删除评论
     * @param comment_id
     * @param user_id
     */
    private void deleteComment(int comment_id, int user_id) {
        commentService.deleteComment(comment_id,user_id)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonDeleteResultBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(CommentActivity.this,R.string.delete_error,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(GsonDeleteResultBean gsonDeleteResultBean) {
                        switch (gsonDeleteResultBean.getCode()){
                            case 0:
                                Toast.makeText(CommentActivity.this,R.string.delete_success,Toast.LENGTH_SHORT).show();
                                initRecyclerView();
                                break;
                            default:
                                Toast.makeText(CommentActivity.this,R.string.delete_error,Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
    }

    //提交评论
    @OnClick(R.id.iv_commit_comment)
    public void commitComment(){
        String text = et_comment.getText().toString();
        if(text.isEmpty()){
            Toast.makeText(CommentActivity.this,R.string.content_empty_error,Toast.LENGTH_SHORT).show();
            return;
        }
        String type = getIntent().getStringExtra(Constant.INTENT_COMMENT_TYPE);
        int type_id = getIntent().getIntExtra(Constant.INTENT_COMMENT_TYPE_ID,-1);
        String time_stamp = Tools.getTimeStamp();
        if(type_id!=-1) {
            commentService.commitComment(text, user_id, reply_id, type, type_id,time_stamp)
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

    interface CommentService {
        @POST("comment/add")
        Observable<GsonCommitCommentResultBean> commitComment(@Query("text")String text,@Query("uploader_id")int uploader_id,@Query("reply_id")int reply_id,@Query("type")String type,@Query("type_id")int type_id,@Query("time_stamp")String time_stamp);
        @POST("comment/get")
        Observable<GsonGetCommentResultBean> getComment(@Query("type")String type, @Query("type_id")int type_id, @Query("user_id")int user_id);
        @POST("comment/delete")
        Observable<GsonDeleteResultBean> deleteComment(@Query("comment_id")int comment_id,@Query("user_id")int user_id);
        @POST("/comment/upvote")
        Observable<GsonCommonResultBean> upvote(@Query("comment_id")int comment_id,@Query("user_id")int user_id);
        @POST("/comment/cancel_upvote")
        Observable<GsonCommonResultBean> cancelUpvote(@Query("comment_id")int comment_id,@Query("user_id")int user_id);
    }

    private void closeInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
            imm.hideSoftInputFromWindow(et_comment.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void copy(String content)
    {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }
}
