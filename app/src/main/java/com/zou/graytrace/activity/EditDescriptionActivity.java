package com.zou.graytrace.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;
import com.zou.graytrace.R;
import com.zou.graytrace.Utils.Constant;
import com.zou.graytrace.Utils.Tools;
import com.zou.graytrace.Utils.URL;
import com.zou.graytrace.application.GrayTraceApplication;
import com.zou.graytrace.bean.GsonDeleteResultBean;
import com.zou.graytrace.bean.GsonPeopleDescription;
import com.zou.graytrace.bean.GsonPeopleDescriptionFromDraft;
import com.zou.graytrace.bean.GsonSaveDraftPeopleDescriptionResultBean;
import com.zou.graytrace.bean.GsonUpdateDescriptionResultBean;
import com.zou.graytrace.bean.GsonUploadDescriptionResultBean;
import com.zou.graytrace.bean.GsonUploadFileResultBean;
import com.zou.graytrace.view.EditTextPlus;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import retrofit2.Retrofit;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zoujingyi on 2018/1/26.
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class EditDescriptionActivity extends AppCompatActivity {
    private static final int ADD_IMAGE = 100;
    private static final int ADD_VIDEO = 101;
    @BindView(R.id.et_description_content)
    EditTextPlus et_description_content;
    @BindView(R.id.toolbar_edit_description)
    Toolbar toolbar_edit_description;
    private GrayTraceApplication app;
    private DescriptionService descriptionService;
    private AlertDialog saveDraftDialog;
    //表示是否在编辑状态
    private String stauts;
    private GrayTraceApplication.PublicService publicService;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_description);
        MPermissions.requestPermissions(this, 4, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    /**
     * 权限请求成功
     */
    @PermissionGrant(4)
    public void requestPermissionSuccess(){
        initData();
        ButterKnife.bind(this);
        initView();
    }

    private void initData() {
        app = (GrayTraceApplication) getApplication();
        Retrofit retrofit = app.getRetrofit();
        descriptionService = retrofit.create(DescriptionService.class);
        publicService = retrofit.create(GrayTraceApplication.PublicService.class);
        stauts = getIntent().getStringExtra(Constant.INTENT_DESCRIPTION_STATUS);
        if(Constant.DESCRIPTION_STATUS_ADD_NEW.equals(stauts)){

        }else if(Constant.DESCRIPTION_STATUS_EDIT.equals(stauts)){
            getDescription();
        }else if(Constant.DESCRIPTION_STATUS_EDIT_DRAFT.equals(stauts)){
            getDescriptionFromDraft();
        }
    }

    private void initView() {
        setSupportActionBar(toolbar_edit_description);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * 编辑状态，获取已提交的文本
     */
    private void getDescription(){
        String people_description_id = getIntent().getStringExtra(Constant.INTENT_PEOPLE_DESCRIPTION_ID);
        descriptionService.getPeopleDescription(people_description_id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonPeopleDescription>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(app,"服务器错误",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(GsonPeopleDescription gsonPeopleDescription) {
                        switch (gsonPeopleDescription.getCode()){
                            case 0:
                                String description = gsonPeopleDescription.getDescription_text();
                                et_description_content.setSpanString(description);
                                et_description_content.requestFocus(description.length());
                                break;
                            default:
                                Toast.makeText(app,"获取数据失败",Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
    }

    /**
     * 删除提交的描述
     */
    private void deleteDescription(){
        String people_description_id = getIntent().getStringExtra(Constant.INTENT_PEOPLE_DESCRIPTION_ID);
        descriptionService.deletePeopleDescription(people_description_id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonDeleteResultBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(EditDescriptionActivity.this,R.string.serve_error,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(GsonDeleteResultBean gsonDeleteResultBean) {
                        switch (gsonDeleteResultBean.getCode()){
                            case 0:
                                Toast.makeText(EditDescriptionActivity.this,R.string.delete_success,Toast.LENGTH_SHORT).show();
                                finish();
                                break;
                            default:
                                Toast.makeText(EditDescriptionActivity.this,R.string.delete_error,Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
    }

    /**
     * 编辑状态，获取草稿中的文本
     */
    private void getDescriptionFromDraft(){
        String draft_people_description_id = getIntent().getStringExtra(Constant.INTENT_DRAFT_PEOPLE_DESCRIPTION_ID);
        descriptionService.getPeopleDescriptionFromDraft(draft_people_description_id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonPeopleDescriptionFromDraft>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(EditDescriptionActivity.this,R.string.serve_error,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(GsonPeopleDescriptionFromDraft gsonPeopleDescriptionFromDraft) {
                        switch (gsonPeopleDescriptionFromDraft.getCode()){
                            case 0:
                                String description = gsonPeopleDescriptionFromDraft.getDescription_text();
                                et_description_content.setSpanString(description);
                                et_description_content.requestFocus(description.length());
                                break;
                            default:
                                Toast.makeText(EditDescriptionActivity.this,"获取描述失败",Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
    }

    private void deleteDescriptionFromDraft(){
        String draft_people_description_id = getIntent().getStringExtra(Constant.INTENT_DRAFT_PEOPLE_DESCRIPTION_ID);
        descriptionService.deletePeopleDescriptionFromDraft(draft_people_description_id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonDeleteResultBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(EditDescriptionActivity.this,R.string.serve_error,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(GsonDeleteResultBean gsonDeleteResultBean) {
                        switch (gsonDeleteResultBean.getCode()){
                            case 0:
                                Toast.makeText(EditDescriptionActivity.this,R.string.delete_success,Toast.LENGTH_SHORT).show();
                                finish();
                                break;
                            default:
                                Toast.makeText(EditDescriptionActivity.this,R.string.delete_error,Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
    }

    /**
     * 添加视频
     */
    @OnClick(R.id.iv_description_add_video)
    public void addVideo(){
        if(et_description_content.isFocused()) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, ADD_VIDEO);
        }
    }

    /**
     * 添加图片
     */
    @OnClick(R.id.iv_description_add_image)
    public void addImage(){
        if(et_description_content.isFocused()) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, ADD_IMAGE);
        }
    }


    /**
     * 权限请求失败
     */
    @PermissionDenied(4)
    public void requestPermissionFail(){
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_commit,menu);
        if(Constant.DESCRIPTION_STATUS_ADD_NEW.equals(stauts)){
            MenuItem menuItem = menu.findItem(R.id.action_menu_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_menu_commit:
                String type = getIntent().getStringExtra(Constant.INTENT_DESCRIPTION_TYPE);
                switch (type){
                    case Constant.DESCRIPTION_TYPE_PEOPLE:
                        if(Constant.DESCRIPTION_STATUS_EDIT.equals(stauts)) {
                            updatePeopleDescription();
                        }else{
                            uploadPeopleDescription();
                        }
                        break;
                    case Constant.DESCRIPTION_TYPE_THINGS:
                        break;
                    case Constant.DESCRIPTION_TYPE_EVENTS:
                        break;
                }
                break;
            case R.id.action_menu_delete:
                switch (stauts){
                    case Constant.DESCRIPTION_STATUS_ADD_NEW:

                        break;
                    case Constant.DESCRIPTION_STATUS_EDIT:
                        //删除提交的描述
                        deleteDescription();
                        break;
                    case Constant.DESCRIPTION_STATUS_EDIT_DRAFT:
                        //删除描述草稿
                        deleteDescriptionFromDraft();
                        break;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ADD_IMAGE:
                if (resultCode == RESULT_OK ) {
                    // 获得图片的uri
                    Uri originalUri = data.getData();
                    try {
//                        Bitmap originalBitmap = Tools.getFitSampleBitmap(getContentResolver().openInputStream(originalUri),Tools.dip2px(this,300),Tools.dip2px(this,150));
                        // 将原始图片的bitmap转换为文件
                        // 上传该文件并获取url
                        String url = URL.BASE_PIC_DESCRIPTION;
                        String fileName = "username"+"_"+Tools.getTimeStamp()+".png";
                        String path = Tools.getPathFromUri(EditDescriptionActivity.this,originalUri);
                        et_description_content.addImage(path,url,fileName);
                        //TODO
                        List<MultipartBody.Part> partList = Tools.getFilePartList(path,url,fileName);
                        publicService.uploadFile(partList).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<GsonUploadFileResultBean>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Toast.makeText(app,"服务器错误",Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onNext(GsonUploadFileResultBean gsonUploadFileResultBean) {
                                        switch (gsonUploadFileResultBean.getCode()){
                                            case 0:
                                                Toast.makeText(app,"上传文件成功",Toast.LENGTH_SHORT).show();
                                                break;
                                            default:
                                                Toast.makeText(app,"上传文件失败",Toast.LENGTH_SHORT).show();
                                                break;
                                        }
                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case ADD_VIDEO:
                if(resultCode == RESULT_OK){
                    Uri originalUri = data.getData();
                    String path = Tools.getPathFromUri(this,originalUri);
                    Bitmap bm = Tools.getVideoThumbnail(path,et_description_content.getWidth(),Tools.dip2px(getApplicationContext(),200));
                    String url = URL.BASE_VIDEO_DESCRIPTION;
                    //TODO 替换username
                    String fileName = "username"+"_"+Tools.getTimeStamp()+".mp4";
                    et_description_content.addVideo(url,bm,fileName);
                    List<MultipartBody.Part> partList = Tools.getFilePartList(path,url,fileName);
                    publicService.uploadFile(partList).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<GsonUploadFileResultBean>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(EditDescriptionActivity.this,"服务器错误",Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onNext(GsonUploadFileResultBean gsonUploadFileResultBean) {
                                    switch (gsonUploadFileResultBean.getCode()){
                                        case 0:
                                            Toast.makeText(EditDescriptionActivity.this,"上传文件成功",Toast.LENGTH_SHORT).show();
                                            break;
                                        default:
                                            Toast.makeText(EditDescriptionActivity.this,"上传文件失败",Toast.LENGTH_SHORT).show();
                                            break;
                                    }
                                }
                            });
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(Constant.DESCRIPTION_STATUS_EDIT.equals(stauts)){
            finish();
        }else {
            showSaveDraftDialog();
        }
    }

    /**
     * 上传人物描述
     */
    private void uploadPeopleDescription() {
        if(Tools.isEditTextEmpty(et_description_content)){
            Toast.makeText(app,"文本框为空",Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO
        String username = "13167231015";
        String draft_people_id = getIntent().getStringExtra(Constant.INTENT_PEOPLE_DRAFT_ID);
        String description_text = et_description_content.getText().toString();
        String time_stamp = Tools.getTimeStamp();
        String people_id = getIntent().getStringExtra(Constant.INTENT_PEOPLE_ID);
        descriptionService.uploadPeopleDescription(username,draft_people_id,description_text,time_stamp,people_id)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonUploadDescriptionResultBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(app,"服务器错误",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(GsonUploadDescriptionResultBean gsonUploadDescriptionResultBean) {
                        switch (gsonUploadDescriptionResultBean.getCode()){
                            case 0:
                                Toast.makeText(app,"提交成功",Toast.LENGTH_SHORT).show();
                                Intent data = new Intent();
                                data.putExtra(Constant.INTENT_PEOPLE_DESCRIPTION_ID,gsonUploadDescriptionResultBean.getPeople_description_id());
                                setResult(Constant.RESULT_DESCRIPTION_COMMIT_OK,data);
                                finish();
                                break;
                            default:
                                Toast.makeText(app,"提交失败，请重试",Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
    }

    private void updatePeopleDescription() {
        if(Tools.isEditTextEmpty(et_description_content)){
            Toast.makeText(app,"文本框为空",Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO
        String username = "13167231015";
        final String people_description_id = getIntent().getStringExtra(Constant.INTENT_PEOPLE_DESCRIPTION_ID);
        String description_text = et_description_content.getText().toString();
        String time_stamp = Tools.getTimeStamp();
        descriptionService.updatePeopleDescription(username,people_description_id,description_text,time_stamp)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonUpdateDescriptionResultBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(app,"服务器错误",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(GsonUpdateDescriptionResultBean gsonUpdateDescriptionResultBean) {
                        switch (gsonUpdateDescriptionResultBean.getCode()){
                            case 0:
                                Toast.makeText(app,"提交成功",Toast.LENGTH_SHORT).show();
                                Intent data = new Intent();
                                data.putExtra(Constant.INTENT_PEOPLE_DESCRIPTION_ID,people_description_id);
                                setResult(Constant.RESULT_DESCRIPTION_COMMIT_OK,data);
                                finish();
                                break;
                            default:
                                Toast.makeText(app,"提交失败，请重试",Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
    }

    /**
     * 保存人物描述草稿
     */
    private void savePeopleDraft(){
        if(Tools.isEditTextEmpty(et_description_content)){
            Toast.makeText(app,"文本框为空",Toast.LENGTH_SHORT).show();
            return;
        }
        String username = "13167231015";
        String draft_people_id = getIntent().getStringExtra(Constant.INTENT_PEOPLE_DRAFT_ID);
        String description_text = et_description_content.getText().toString();
        String time_stamp = Tools.getTimeStamp();
        String people_id = getIntent().getStringExtra(Constant.INTENT_PEOPLE_ID);
        descriptionService.saveDraftPeopleDescription(username,draft_people_id,description_text,time_stamp,people_id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonSaveDraftPeopleDescriptionResultBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(app,R.string.serve_error,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(GsonSaveDraftPeopleDescriptionResultBean gsonSaveDraftPeopleDescriptionResultBean) {
                        switch (gsonSaveDraftPeopleDescriptionResultBean.getCode()){
                            case 0:
                                Toast.makeText(app,R.string.save_draft_success,Toast.LENGTH_SHORT).show();
                                String draft_people_description_id = gsonSaveDraftPeopleDescriptionResultBean.getDraft_people_description_id();
                                Intent intent = new Intent();
                                intent.putExtra(Constant.INTENT_DRAFT_PEOPLE_DESCRIPTION_ID,draft_people_description_id);
                                setResult(Constant.RESULT_DESCRIPTION_SAVE_DRAFT_OK,intent);
                                finish();
                                break;
                            default:
                                Toast.makeText(app,R.string.save_draft_error,Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
    }



    private void showSaveDraftDialog(){
        if(saveDraftDialog == null){
            saveDraftDialog =  new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.dialog_msg_save_draft))
                    .setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            savePeopleDraft();
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).create();
        }
        saveDraftDialog.show();
    }

    interface DescriptionService{
        @POST("people/commit/description")
        Observable<GsonUploadDescriptionResultBean> uploadPeopleDescription(@Query("username")String username,@Query("draft_people_id")String draft_people_id,@Query("description_text")String description_text,@Query("time_stamp")String time_stamp,@Query("people_id")String people_id);
        @POST("people/update/description")
        Observable<GsonUpdateDescriptionResultBean> updatePeopleDescription(@Query("username")String username, @Query("people_description_id")String people_description_id, @Query("description_text")String description_text, @Query("time_stamp")String time_stamp);
        @POST("draft/people/description")
        Observable<GsonSaveDraftPeopleDescriptionResultBean> saveDraftPeopleDescription(@Query("username")String username,@Query("draft_people_id")String draft_people_id,@Query("description_text")String description_text,@Query("time_stamp")String time_stamp,@Query("people_id")String people_id);
        @POST("people/get/description")
        Observable<GsonPeopleDescription> getPeopleDescription(@Query("people_description_id")String people_description_id);
        @POST("people/delete/description")
        Observable<GsonDeleteResultBean> deletePeopleDescription(@Query("people_description_id")String people_description_id);
        @POST("draft/people_description/get")
        Observable<GsonPeopleDescriptionFromDraft> getPeopleDescriptionFromDraft(@Query("draft_people_description_id")String draft_people_description_id);
        @POST("draft/people_description/delete")
        Observable<GsonDeleteResultBean>deletePeopleDescriptionFromDraft(@Query("draft_people_description_id")String draft_people_description_id);
    }
}
