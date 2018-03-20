package com.zou.graytrace.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zou.graytrace.R;
import com.zou.graytrace.Utils.Constant;
import com.zou.graytrace.Utils.Tools;
import com.zou.graytrace.activity.LoginActivity;
import com.zou.graytrace.activity.MyCreationActivity;
import com.zou.graytrace.application.GrayTraceApplication;
import com.zou.graytrace.bean.GsonEditAccountInfoResultBean;
import com.zou.graytrace.bean.GsonGetAccountInfoResult;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

/**
 * Created by zou on 2018/1/17.
 */

public class MineFragment extends BaseFragment {
    @BindView(R.id.tv_nick_name)
    TextView tv_nick_name;
    @BindView(R.id.iv_account)
    ImageView iv_account;
    @BindView(R.id.ll_login_view)
    View ll_login_view;
    @BindView(R.id.cardView_unlogin)
    View cardView_unlogin;
    @BindView(R.id.tv_draft_count)
    TextView tv_draft_count;
    private EditText et_nickname;
    private ImageView iv_dialog_avatar;
    private static MineFragment instance;
    private int user_id;
    private SharedPreferences sharedPreferences;
    private AlertDialog exitLoginDialog,editInfoDialog;
    private static final int REQUEST_LOGIN = 100;
    private static final int EDIT_AVATAR = 101;
    private String nick_name,avatar_url;
    private int draft_count;
    private AccountInfoService accountInfoService;
    private File avatarImageFile;

    public static synchronized MineFragment getInstance() {
        if (instance == null) {
            instance = new MineFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent(R.layout.fragment_mine);
    }

    @Override
    protected void onBaseCreateView() {
        super.onBaseCreateView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }



    private void initData() {
        GrayTraceApplication application = (GrayTraceApplication) getActivity().getApplication();
        sharedPreferences = application.getAccountSharedPreferences();
        boolean isLogin = sharedPreferences.getBoolean(Constant.SP_IS_LOGIN, false);
        if(isLogin){
            //显示已登录的界面
            user_id = sharedPreferences.getInt(Constant.SP_USER_ID,0);
            getAccountInfo();
            initLoginView();
        }else{
            //显示未登录的界面
            initUnLoginView();
        }
    }

    private void initLoginView() {
        ll_login_view.setVisibility(View.VISIBLE);
        cardView_unlogin.setVisibility(View.GONE);
    }

    private void initUnLoginView(){
        ll_login_view.setVisibility(View.GONE);
        cardView_unlogin.setVisibility(View.VISIBLE);
    }
    /**
     * 获取用户信息
     */
    private void getAccountInfo(){
        GrayTraceApplication application = (GrayTraceApplication) getActivity().getApplication();
        accountInfoService = application.getRetrofit().create(AccountInfoService.class);
        accountInfoService.getAccountInfoService(user_id)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonGetAccountInfoResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getContext(),"服务器错误",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(GsonGetAccountInfoResult gsonGetAccountInfoResult) {
                        switch (gsonGetAccountInfoResult.getCode()){
                            case 0:
                                nick_name = gsonGetAccountInfoResult.getInfo().getNick_name();
                                avatar_url = gsonGetAccountInfoResult.getInfo().getAvatar_url();
                                String draft_people_ids = gsonGetAccountInfoResult.getInfo().getDraft_people_ids();
                                String draft_thing_ids = gsonGetAccountInfoResult.getInfo().getDraft_thing_ids();
                                String draft_event_ids = gsonGetAccountInfoResult.getInfo().getDraft_event_ids();
                                String draft_article_ids = gsonGetAccountInfoResult.getInfo().getDraft_article_ids();
                                int draft_people_count=0;
                                int draft_things_count=0;
                                int draft_events_count=0;
                                int draft_article_count=0;
                                if(draft_people_ids!=null){
                                    draft_people_count = draft_people_ids.split(",").length;
                                }
                                if(draft_thing_ids!=null){
                                    draft_things_count = draft_thing_ids.split(",").length;
                                }
                                if(draft_event_ids!=null){
                                    draft_events_count = draft_event_ids.split(",").length;
                                }
                                if(draft_article_ids!=null){
                                    draft_article_count = draft_article_ids.split(",").length;
                                }

                                draft_count = draft_people_count+draft_things_count+draft_events_count+draft_article_count;
                                if(draft_count!=0){
                                    tv_draft_count.setText(draft_count+getResources().getString(R.string.count_draft));
                                }
                                if(nick_name!=null) {
                                    tv_nick_name.setText(nick_name);
                                }else{
                                    tv_nick_name.setText("用户"+user_id);
                                }
                                if(avatar_url!=null) {
                                    Glide.with(getContext()).load(avatar_url).into(iv_account);
                                }
                                break;
                            default:
                                Toast.makeText(getContext(),"获取用户信息失败",Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
    }

    private void showExitLoginDialog(){
        if(exitLoginDialog == null){
            exitLoginDialog = new AlertDialog.Builder(getContext())
                    .setTitle(R.string.exit_login)
                    .setMessage(R.string.exit_login_msg)
                    .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sharedPreferences.edit().clear().apply();
                            initData();
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
        }
        exitLoginDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_LOGIN:
                if(resultCode == LoginActivity.RESULT_LOGIN_OK){
                    initData();
                }else if(requestCode == LoginActivity.RESULT_LOGIN_CANCEL){

                }
                break;
            case EDIT_AVATAR:
                if(resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    Glide.with(getContext()).load(uri).into(iv_dialog_avatar);
                    avatarImageFile = new File(Tools.getPathFromUri(getContext(),uri));
                }
                break;
        }
    }

    /**
     * 编辑头像和昵称
     */
    @OnClick(R.id.cardView_login)
    void showEditInfoDialog(){
        if(editInfoDialog == null){
            View view =View.inflate(getActivity(),R.layout.dialog_edit_info,null);
            editInfoDialog = new AlertDialog.Builder(getContext()).setView(view).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    editInfo();
                }
            }).create();
            iv_dialog_avatar = view.findViewById(R.id.iv_dialog_avatar);
            et_nickname = view.findViewById(R.id.et_dialog_nickname);

            iv_dialog_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, EDIT_AVATAR);
                }
            });
        }
        if(avatar_url!=null) {
            Glide.with(getContext()).load(avatar_url).into(iv_dialog_avatar);
        }
        if(nick_name!=null) {
            et_nickname.setText(nick_name);
        }
        editInfoDialog.show();
    }

    private void editInfo(){
        //点击确认，提交编辑信息
        if(Tools.isEditTextEmpty(et_nickname)){
            Toast.makeText(getContext(),R.string.nickname_empty_error,Toast.LENGTH_SHORT).show();
            return;
        }
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if(avatarImageFile!=null) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), avatarImageFile);
            String fileName = "username"+"_"+ Tools.getTimeStamp()+".png";
            builder.addFormDataPart("file", fileName, fileBody);
        }
        builder.addFormDataPart("user_id", String.valueOf(user_id));
        builder.addFormDataPart("nick_name",et_nickname.getText().toString());
        List<MultipartBody.Part> partList = builder.build().parts();
        accountInfoService.editAccountInfoService(partList)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonEditAccountInfoResultBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(),R.string.update_error,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(GsonEditAccountInfoResultBean gsonEditAccountInfoResultBean) {
                        switch (gsonEditAccountInfoResultBean.getCode()){
                            case 0:
                                getAccountInfo();
                                Toast.makeText(getActivity(),R.string.update_success,Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(getActivity(),R.string.update_error,Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
    }
    /**
     * 跳转到登录界面
     */
    @OnClick(R.id.cardView_unlogin)
    void toLogin(){
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivityForResult(intent,REQUEST_LOGIN);
    }

    /**
     * 我的创作
     */
    @OnClick(R.id.rl_mine_creation)
    void toMyCreation(){
        Intent intent = new Intent(getActivity(), MyCreationActivity.class);
        intent.putExtra(Constant.INTENT_DRAFT_BOX_COUNT,draft_count);
        startActivity(intent);
    }

    /**
     * 我的收藏
     */
    @OnClick(R.id.rl_mine_collection)
    void toMyCollection(){

    }

    /**
     * 我的关注
     */
    @OnClick(R.id.rl_mine_care)
    void toMyCare(){

    }

    /**
     * 退出登录
     */
    @OnClick(R.id.cardView_account_exit)
    void exitLogin(){
        showExitLoginDialog();
    }


    interface AccountInfoService {
        @POST("accountInfo/get")
        Observable<GsonGetAccountInfoResult> getAccountInfoService(@Query("user_id")int user_id);
        @Multipart
        @POST("accountInfo/edit")
        Observable<GsonEditAccountInfoResultBean> editAccountInfoService(@Part List<MultipartBody.Part> partList);
    }
}
