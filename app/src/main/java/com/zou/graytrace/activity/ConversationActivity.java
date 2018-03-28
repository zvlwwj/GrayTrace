package com.zou.graytrace.activity;

import android.content.ClipboardManager;
import android.content.Context;
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
 * Created by zou on 2018/3/21.
 */

public class ConversationActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_conversation)
    Toolbar toolbar_conversation;
    @BindView(R.id.recyclerView_conversation)
    RecyclerView recyclerView_conversation;
    @BindView(R.id.et_conversation_comment)
    EditText et_conversation_comment;
    @BindView(R.id.ll_conversation_commit_comment)
    LinearLayout ll_conversation_commit_comment;
    private ArrayList<CommentBean> conversationComments;
    private CommentRecyclerAdapter adapter;
    private BottomSheetDialog mBottomSheetDialog;
    private String reply_nickname;
    private SharedPreferences sharedPreferences;
    private int user_id;
    private CommentService commentService;
    private String type;
    private int type_id;
    private int previous_id=-1;
    private int comment_id = -1;
    private String next_ids;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {

        GrayTraceApplication application = (GrayTraceApplication) getApplication();
        sharedPreferences = application.getAccountSharedPreferences();
        user_id = sharedPreferences.getInt(Constant.SP_USER_ID,0);
        commentService = application.getRetrofit().create(CommentService.class);
        previous_id =getIntent().getIntExtra(Constant.INTENT_COMMENT_PREVIOUS_ID,-1);
        comment_id = getIntent().getIntExtra(Constant.INTENT_COMMENT_COMMENT_ID,-1);
        next_ids = getIntent().getStringExtra(Constant.INTENT_COMMENT_NEXT_IDS);
        type = getIntent().getStringExtra(Constant.INTENT_COMMENT_TYPE);
        type_id = getIntent().getIntExtra(Constant.INTENT_COMMENT_TYPE_ID,-1);
        commentService.getConversationList(previous_id,comment_id,next_ids,user_id)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonGetCommentResultBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(ConversationActivity.this,R.string.get_data_error,Toast.LENGTH_SHORT).show();
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
                                    commentBean.setPrevious_id(info.getPrevious_id());
                                    commentBean.setNext_ids(info.getNext_ids());
                                    commentBean.setTime_stamp(info.getTime_stamp());
                                    commentBean.setType(info.getType());
                                    commentBean.setType_id(info.getType_id());
                                    commentBean.setUpvote_count(info.getUpvote_count());
                                    commentBean.setIs_upvote(info.isIs_upvote());
                                    if(conversationComments == null){
                                        conversationComments = new ArrayList<CommentBean>();
                                    }
                                    conversationComments.add(commentBean);
                                }
                                initRecyclerView();
                                break;
                            default:
                                Toast.makeText(ConversationActivity.this,R.string.get_data_error,Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
    }

    private void initRecyclerView() {
        recyclerView_conversation.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommentRecyclerAdapter(conversationComments,this,true);
        adapter.setOnItemClickListener(new CommentRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                if(mBottomSheetDialog == null) {
                    mBottomSheetDialog = new BottomSheetDialog(ConversationActivity.this);
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_bottom_reply, null);
                    mBottomSheetDialog.setContentView(dialogView);
                    LinearLayout ll_copy = dialogView.findViewById(R.id.ll_copy);
                    LinearLayout ll_reply = dialogView.findViewById(R.id.ll_reply);
                    LinearLayout ll_delete = dialogView.findViewById(R.id.ll_delete);
                    ll_copy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //复制回复内容
                            copy(conversationComments.get(position).getText());
                            Toast.makeText(ConversationActivity.this,R.string.copy_success,Toast.LENGTH_SHORT).show();
                            mBottomSheetDialog.dismiss();
                        }
                    });
                    ll_reply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            previous_id = conversationComments.get(position).getComment_id();
                            reply_nickname = conversationComments.get(position).getNick_name();
                            et_conversation_comment.setText("");
                            et_conversation_comment.setHint(getString(R.string.reply_to)+reply_nickname);
                            mBottomSheetDialog.dismiss();
                            ll_conversation_commit_comment.setVisibility(View.VISIBLE);
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
        adapter.setOnUpvoteListener(new CommentRecyclerAdapter.OnUpvoteListener() {
            @Override
            public void onUpvote(int position,boolean isChecked) {
                //点赞
                int comment_id = conversationComments.get(position).getComment_id();
                if(isChecked){
                    upvote(comment_id,user_id,position);
                }else{
                    cancelUpvote(comment_id,user_id,position);
                }
            }
        });
        recyclerView_conversation.setAdapter(adapter);
    }

    private void initView() {
        setSupportActionBar(toolbar_conversation);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
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

    //提交评论
    @OnClick(R.id.iv_conversation_commit_comment)
    public void commitComment(){
        String text = et_conversation_comment.getText().toString();
        if(text.isEmpty()){
            Toast.makeText(ConversationActivity.this,R.string.content_empty_error,Toast.LENGTH_SHORT).show();
            return;
        }

        String time_stamp = Tools.getTimeStamp();
        if(type_id!=-1) {
            commentService.commitComment(text, user_id, previous_id, type, type_id,time_stamp)
                    .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<GsonCommitCommentResultBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(ConversationActivity.this,R.string.commit_error,Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(GsonCommitCommentResultBean gsonCommitCommentResultBean) {
                            switch (gsonCommitCommentResultBean.getCode()){
                                case 0:
                                    Toast.makeText(ConversationActivity.this,R.string.commit_success,Toast.LENGTH_SHORT).show();
                                    et_conversation_comment.setText("");
                                    closeInputMethod();
                                    break;
                                default:
                                    Toast.makeText(ConversationActivity.this,R.string.commit_error,Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });
        }else{
            Toast.makeText(this,R.string.commit_error,Toast.LENGTH_SHORT).show();
        }
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
                        Toast.makeText(ConversationActivity.this,R.string.upvote_error,Toast.LENGTH_SHORT).show();
                        CommentRecyclerAdapter.ViewHolder viewHolder = (CommentRecyclerAdapter.ViewHolder) recyclerView_conversation.findViewHolderForAdapterPosition(position);
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
                                Toast.makeText(ConversationActivity.this,R.string.upvote_error,Toast.LENGTH_SHORT).show();
                                CommentRecyclerAdapter.ViewHolder viewHolder = (CommentRecyclerAdapter.ViewHolder) recyclerView_conversation.findViewHolderForAdapterPosition(position);
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
                        Toast.makeText(ConversationActivity.this,R.string.upvote_error,Toast.LENGTH_SHORT).show();
                        CommentRecyclerAdapter.ViewHolder viewHolder = (CommentRecyclerAdapter.ViewHolder) recyclerView_conversation.findViewHolderForAdapterPosition(position);
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
                                Toast.makeText(ConversationActivity.this,R.string.upvote_error,Toast.LENGTH_SHORT).show();
                                CommentRecyclerAdapter.ViewHolder viewHolder = (CommentRecyclerAdapter.ViewHolder) recyclerView_conversation.findViewHolderForAdapterPosition(position);
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

    private void closeInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
            imm.hideSoftInputFromWindow(et_conversation_comment.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void copy(String content)
    {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }

    interface CommentService {
        @POST("comment/add")
        Observable<GsonCommitCommentResultBean> commitComment(@Query("text")String text,@Query("uploader_id")int uploader_id,@Query("previous_id")int previous_id,@Query("type")String type,@Query("type_id")int type_id,@Query("time_stamp")String time_stamp);
        @POST("comment/delete")
        Observable<GsonDeleteResultBean> deleteComment(@Query("comment_id")int comment_id, @Query("user_id")int user_id);
        @POST("/comment/upvote")
        Observable<GsonCommonResultBean> upvote(@Query("comment_id")int comment_id, @Query("user_id")int user_id);
        @POST("/comment/cancel_upvote")
        Observable<GsonCommonResultBean> cancelUpvote(@Query("comment_id")int comment_id,@Query("user_id")int user_id);
        @POST("/comment/get/conversation")
        Observable<GsonGetCommentResultBean> getConversationList(@Query("previous_id")int previous_id,@Query("comment_id")int comment_id,@Query("next_ids")String next_ids,@Query("user_id")int user_id);
    }
}
