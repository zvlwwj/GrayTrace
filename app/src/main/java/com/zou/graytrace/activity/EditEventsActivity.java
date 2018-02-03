package com.zou.graytrace.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;
import com.zou.graytrace.R;
import com.zou.graytrace.Utils.Constant;
import com.zou.graytrace.Utils.Tools;
import com.zou.graytrace.application.GrayTraceApplication;
import com.zou.graytrace.bean.GsonCommitEventDraftResultBean;
import com.zou.graytrace.bean.GsonPeopleEvent;
import com.zou.graytrace.bean.GsonPeopleEventFromDraft;
import com.zou.graytrace.bean.GsonUpdateEventDraftResultBean;
import com.zou.graytrace.bean.GsonUploadEventResultBean;
import com.zou.graytrace.view.EditTextPlus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Retrofit;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zou on 2018/1/22.
 */

/**
 * TODO 1.退出Activity动画应该和回退时的不一样
 *      2.android8.0无法找到video路径，找不到解决方案！
 */


public class EditEventsActivity extends AppCompatActivity {
    private static final int ADD_IMAGE = 100;
    private static final int ADD_VIDEO = 101;
    @BindView(R.id.toolbar_upload_edit_events)
    Toolbar toolbar_upload_edit_events;
    @BindView(R.id.et_event_title)
    EditText et_event_title;
    @BindView(R.id.et_event_content)
    EditTextPlus et_event_content;
    private static final String TAG="EditEventsActivity";
    private GrayTraceApplication app;
    private String stauts;
    private EventService eventService;
    private AlertDialog saveDraftDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_events);
        MPermissions.requestPermissions(this, 4, Manifest.permission.READ_EXTERNAL_STORAGE);

    }

    private void initData() {
        app = (GrayTraceApplication) getApplication();
        Retrofit retrofit = app.getRetrofit();
        eventService = retrofit.create(EventService.class);
        stauts = getIntent().getStringExtra(Constant.INTENT_EVENTS_STATUS);
        if(Constant.EVENTS_STATUS_ADD_NEW.equals(stauts)){

        }else if(Constant.EVENTS_STATUS_EDIT.equals(stauts)){
            getEvent();
        }else if(Constant.EVENTS_STATUS_EDIT_DRAFT.equals(stauts)){
            getEventFromDraft();
        }
    }

    private void initView() {
        setSupportActionBar(toolbar_upload_edit_events);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_commit,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                //TODO 保存到草稿 返回主界面
                showSaveDraftDialog();
                break;
            case R.id.action_menu_commit:
                //TODO 提交
                Toast.makeText(getApplicationContext(),"提交",Toast.LENGTH_SHORT).show();
                String type = getIntent().getStringExtra(Constant.INTENT_EVENTS_TYPE);
                switch (type){
                    case Constant.EVENTS_TYPE_PEOPLE:
                        uploadPeopleEvent();
                        break;
                    case Constant.EVENTS_TYPE_THINGS:
                        
                        break;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showSaveDraftDialog();
    }

    /**
     * 编辑状态，获取已提交的文本
     */
    private void getEvent(){
        String people_event_id = getIntent().getStringExtra(Constant.INTENT_PEOPLE_EVENT_ID);
        eventService.getPeopleEvent(people_event_id).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonPeopleEvent>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(app,"服务器错误",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(GsonPeopleEvent gsonPeopleEvent) {
                        switch (gsonPeopleEvent.getCode()){
                            case 0:
                                String event_title = gsonPeopleEvent.getEvent_title();
                                String event_text = gsonPeopleEvent.getEvent_text();
                                et_event_title.setText(event_title);
                                et_event_content.setText(event_text);
                                et_event_content.setSelection(event_text.length());
                                break;
                            default:
                                Toast.makeText(app,"读取数据失败",Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
    }

    /**
     * 编辑状态，获取草稿中的文本
     */
    private void getEventFromDraft(){
        String draft_people_event_id = getIntent().getStringExtra(Constant.INTENT_DRAFT_PEOPLE_EVENT_ID);
        eventService.getPeopleEventFromDraft(draft_people_event_id).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonPeopleEventFromDraft>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(app,"服务器错误",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(GsonPeopleEventFromDraft gsonPeopleEvent) {
                        switch (gsonPeopleEvent.getCode()){
                            case 0:
                                String event_title = gsonPeopleEvent.getEvent_title();
                                String event_text = gsonPeopleEvent.getEvent_text();
                                et_event_title.setText(event_title);
                                et_event_content.setText(event_text);
                                et_event_content.setSelection(event_text.length());
                                break;
                            default:
                                Toast.makeText(app,"读取数据失败",Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
    }
    /**
     * 提交人物事件草稿
     */
    private void commitPeopleEventDraft() {
        if(Tools.isEditTextEmpty(et_event_title)){
            Toast.makeText(app,"标题为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(Tools.isEditTextEmpty(et_event_content)){
            Toast.makeText(app,"内容为空",Toast.LENGTH_SHORT).show();
            return;
        }
        String username = "13167231015";
        String draft_people_id = getIntent().getStringExtra(Constant.INTENT_PEOPLE_DRAFT_ID);
        String event_text = et_event_content.getText().toString();
        String event_title = et_event_title.getText().toString();
        String time_stamp = Tools.getTimeStamp();
        eventService.commitEventDraft(username,time_stamp,draft_people_id,event_title,event_text)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonCommitEventDraftResultBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(app,"服务器错误",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(GsonCommitEventDraftResultBean gsonSaveDraftPeopleDescriptionResultBean) {
                        switch (gsonSaveDraftPeopleDescriptionResultBean.getCode()){
                            case 0:
                                Toast.makeText(app,"已保存到草稿",Toast.LENGTH_SHORT).show();
                                String draft_people_event_id = gsonSaveDraftPeopleDescriptionResultBean.getDraft_people_event_id();
                                Intent intent = new Intent();
                                String title = et_event_title.getText().toString();
                                intent.putExtra(Constant.INTENT_DRAFT_PEOPLE_EVENT_ID,draft_people_event_id);
                                intent.putExtra(Constant.INTENT_PEOPLE_EVENT_TITLE,title);
                                setResult(Constant.RESULT_EVENTS_SAVE_DRAFT_OK,intent);
                                finish();
                                break;
                            default:
                                Toast.makeText(app,"保存草稿失败",Toast.LENGTH_SHORT).show();
                                setResult(Constant.RESULT_DESCRIPTION_SAVE_DRAFT_FAIL);
                                finish();
                                break;
                        }
                    }
                });
    }

    /**
     * 更新人物事件草稿
     */
    private void updatePeopleEventDraft(){
        if(Tools.isEditTextEmpty(et_event_title)){
            Toast.makeText(app,"标题为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(Tools.isEditTextEmpty(et_event_content)){
            Toast.makeText(app,"内容为空",Toast.LENGTH_SHORT).show();
            return;
        }
        String username = "13167231015";
        final String draft_people_event_id = getIntent().getStringExtra(Constant.INTENT_DRAFT_PEOPLE_EVENT_ID);
        String event_text = et_event_content.getText().toString();
        String event_title = et_event_title.getText().toString();
        String time_stamp = Tools.getTimeStamp();
        eventService.updateEventDraft(username,time_stamp,draft_people_event_id,event_title,event_text)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonUpdateEventDraftResultBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(app,"服务器错误",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(GsonUpdateEventDraftResultBean gsonUpdateEventDraftResultBean) {
                        switch (gsonUpdateEventDraftResultBean.getCode()){
                            case 0:
                                Toast.makeText(app,"已更新到草稿",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                String title = et_event_title.getText().toString();
                                intent.putExtra(Constant.INTENT_DRAFT_PEOPLE_EVENT_ID,draft_people_event_id);
                                intent.putExtra(Constant.INTENT_PEOPLE_EVENT_TITLE,title);
                                setResult(Constant.RESULT_EVENTS_SAVE_DRAFT_OK,intent);
                                finish();
                                break;
                            default:
                                Toast.makeText(app,"更新草稿失败",Toast.LENGTH_SHORT).show();
                                setResult(Constant.RESULT_DESCRIPTION_SAVE_DRAFT_FAIL);
                                finish();
                                break;
                        }
                    }
                });
    }

    /**
     * 上传人物事件
     */
    private void uploadPeopleEvent() {
        if(Tools.isEditTextEmpty(et_event_title)){
            Toast.makeText(app,"标题为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(Tools.isEditTextEmpty(et_event_content)){
            Toast.makeText(app,"内容为空",Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO
        String username = "13167231015";
        String draft_people_id = getIntent().getStringExtra(Constant.INTENT_PEOPLE_DRAFT_ID);
        String title = et_event_title.getText().toString();
        String event_text = et_event_content.getText().toString();
        String time_stamp = Tools.getTimeStamp();
        eventService.uploadPeopleEvent(username,draft_people_id,title,event_text,time_stamp)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonUploadEventResultBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(app,"服务器错误",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(GsonUploadEventResultBean gsonUploadEventResultBean) {
                        switch (gsonUploadEventResultBean.getCode()){
                            case 0:
                                //提交成功，返回标题和ID到前一个界面
                                Toast.makeText(app,"提交成功",Toast.LENGTH_SHORT).show();
                                Intent data = new Intent();
                                String title = et_event_title.getText().toString();
                                data.putExtra(Constant.INTENT_PEOPLE_EVENT_TITLE,title);
                                data.putExtra(Constant.INTENT_PEOPLE_EVENT_ID,gsonUploadEventResultBean.getPeople_event_id());
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
     * 权限请求成功
     */
    @PermissionGrant(4)
    public void requestPermissionSuccess(){
        ButterKnife.bind(this);
        initData();
        initView();
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

    /**
     * 添加视频
     */
    @OnClick(R.id.iv_event_add_video)
    public void addVideo(){
        if(et_event_content.isFocused()) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, ADD_VIDEO);
        }
    }

    /**
     * 添加图片
     */
    @OnClick(R.id.iv_event_add_image)
    public void addImage(){
        if(et_event_content.isFocused()) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, ADD_IMAGE);
        }
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
//                        List<String> listPath = new ArrayList<>();
//                        listPath.add();
                        et_event_content.addImage(Tools.getPathFromUri(EditEventsActivity.this,originalUri));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case ADD_VIDEO:
                if(resultCode == RESULT_OK){
                    Uri originalUri = data.getData();
                    Bitmap bm = Tools.getVideoThumbnail(this,originalUri,et_event_content.getWidth(),Tools.dip2px(getApplicationContext(),200));
                    et_event_content.addVideo(originalUri.getPath(),bm);
                }
                break;
        }

    }

    private void showSaveDraftDialog(){
        if(saveDraftDialog == null){
            saveDraftDialog =  new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.dialog_msg_save_draft))
                    .setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //若状态是ADD_NEW或者EDIT，则提交新的草稿
                            //若状态是EDIT_DRAFT，则更新原有的草稿
                            switch (stauts){
                                case Constant.EVENTS_STATUS_ADD_NEW:
                                case Constant.EVENTS_STATUS_EDIT:
                                    commitPeopleEventDraft();
                                    break;
                                case Constant.EVENTS_STATUS_EDIT_DRAFT:
                                    updatePeopleEventDraft();
                                    break;
                            }
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



    interface EventService{
        @POST("people/commit/event")
        Observable<GsonUploadEventResultBean> uploadPeopleEvent(@Query("username")String username, @Query("draft_people_id")String draft_people_id, @Query("event_title")String event_title, @Query("event_text")String event_text, @Query("time_stamp")String time_stamp);
        @POST("draft/people/commit/event")
        Observable<GsonCommitEventDraftResultBean> commitEventDraft(@Query("username")String username, @Query("time_stamp")String time_stamp,@Query("draft_people_id") String draft_people_id,@Query("event_title") String event_title,@Query("event_text")String event_text);
        @POST("draft/people/update/event")
        Observable<GsonUpdateEventDraftResultBean> updateEventDraft(@Query("username")String username, @Query("time_stamp")String time_stamp, @Query("draft_people_event_id")String draft_people_event_id, @Query("event_title")String event_title, @Query("event_text")String event_text);
        @POST("people/get/event")
        Observable<GsonPeopleEvent> getPeopleEvent(@Query("people_event_id")String people_event_id);
        @POST("people/get/event_from_draft")
        Observable<GsonPeopleEventFromDraft> getPeopleEventFromDraft(@Query("draft_people_event_id")String draft_people_event_id);

    }
}
