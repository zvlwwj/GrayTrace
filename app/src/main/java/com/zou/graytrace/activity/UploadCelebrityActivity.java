package com.zou.graytrace.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.CountryPickerListener;
import com.zou.graytrace.R;
import com.zou.graytrace.Utils.Constant;
import com.zou.graytrace.Utils.Tools;
import com.zou.graytrace.application.GrayTraceApplication;
import com.zou.graytrace.bean.GsonSaveDraftPeopleResultBean;
import com.zou.graytrace.bean.GsonUpdateDraftPeopleResultBean;
import com.zou.graytrace.bean.GsonUploadPeopleResultBean;
import com.zou.graytrace.bean.PeopleEventText;
import com.zou.graytrace.view.TextViewContainer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zou on 2018/1/18.
 * 上传界面
 */
public class UploadCelebrityActivity extends AppCompatActivity{
    CountryPicker picker;
    private static final int SELECT_COVER = 100;
    private static final int ADD_EVENTS = 101;
    private static final int ADD_DESCRIPTION = 102;
    @BindView(R.id.toolbar_upload_celebrity)
    Toolbar toolbar_upload_celebrity;
    @BindView(R.id.btn_select_cover)
    Button btn_select_cover;
    @BindView(R.id.iv_cover)
    ImageView iv_cover;
    @BindView(R.id.et_celebrity_name)
    EditText et_celebrity_name;
    @BindView(R.id.et_birthday)
    EditText et_birthday;
    @BindView(R.id.et_death_day)
    EditText et_death_day;
    @BindView(R.id.ic_death_day)
    ImageView ic_death_day;
    @BindView(R.id.textInputLayout_death_day)
    TextInputLayout textInputLayout_death_day;
    @BindView(R.id.et_celebrity_nationality)
    EditText et_celebrity_nationality;
    @BindView(R.id.et_birth_place)
    EditText et_birth_place;
    @BindView(R.id.ic_long_sleep)
    ImageView ic_long_sleep;
    @BindView(R.id.textInputLayout_long_sleep_place)
    TextInputLayout textInputLayout_long_sleep_place;
    @BindView(R.id.et_long_sleep_place)
    EditText et_long_sleep_place;
    @BindView(R.id.et_residence)
    EditText et_residence;
    @BindView(R.id.et_motto)
    EditText et_motto;
    @BindView(R.id.et_industry)
    EditText et_industry;
    @BindView(R.id.textInputLayout_celebrity_nationality)
    TextInputLayout textInputLayout_celebrity_nationality;
    @BindView(R.id.rg_alive)
    RadioGroup rg_alive;
    @BindView(R.id.tv_add_description)
    TextView tv_add_description;
    @BindView(R.id.ll_tv_events)
    TextViewContainer ll_tv_events;
    @BindView(R.id.ic_events)
    ImageView ic_events;

    private File imageFile;
    private AboutPeopleService aboutPeopleService;
    private GrayTraceApplication app;
    private ProgressDialog loadingDialog;
    //若从添加描述或者添加事件中返回则改值为true
    private boolean isCreatedDraft;
    //人物草稿ID
    private String draft_people_id;
    //人物描述ID
    private String people_description_id;
    //人物描述草稿ID
    private String draft_people_description_id;

    //人物事件ID
    private String people_event_id;
    //人物事件草稿ID
    private String draft_people_event_id;

    private ArrayList<PeopleEventText> peopleEventTexts;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_celebrity);
        ButterKnife.bind(this);
        initData();
        initView();
        setListener();
    }

    private void initData() {
        app = (GrayTraceApplication) getApplication();
        Retrofit retrofit = app.getRetrofit();
        aboutPeopleService = retrofit.create(AboutPeopleService.class);
        tv_add_description.setTag(Constant.TAG_DESCRIPTION_ADD_NEW);
    }

    private void initView() {
        setSupportActionBar(toolbar_upload_celebrity);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        et_celebrity_nationality.setInputType(InputType.TYPE_NULL);
        //设置TextViewContainer的最大宽度
        int maxWidth = Tools.getScreenWidth(this)-ic_events.getMeasuredWidth()-Tools.dip2px(app,24);
        ll_tv_events.setMaxWidth(maxWidth);
        ll_tv_events.setOnMoreTextClickedListener(new TextViewContainer.OnMoreTextClickedListener() {
            @Override
            public void onMoreTextClicked() {
                if (!isCreatedDraft&&Tools.isEditTextEmpty(et_celebrity_name)) {
                    //TODO 设置EditText error
                    Toast.makeText(getApplicationContext(), "姓名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String[] singleChoiceItems = new String[peopleEventTexts.size()];
                for(int i=0;i<singleChoiceItems.length;i++){
                    singleChoiceItems[i] = peopleEventTexts.get(i).getTitle();
                }
                int itemSelected = 0;
                new AlertDialog.Builder(UploadCelebrityActivity.this)
                        .setTitle(getString(R.string.choose_edit_event))
                        .setSingleChoiceItems(singleChoiceItems, itemSelected, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(UploadCelebrityActivity.this,EditEventsActivity.class);
                                intent.putExtra(Constant.INTENT_EVENTS_TYPE, Constant.EVENTS_TYPE_PEOPLE);
                                String tag = peopleEventTexts.get(i).getTag();
                                String id = peopleEventTexts.get(i).getId();
                                switch (tag){
                                    case Constant.TAG_EVENT_EDIT:
                                        intent.putExtra(Constant.INTENT_EVENTS_STATUS, Constant.EVENTS_STATUS_EDIT);
                                        intent.putExtra(Constant.INTENT_PEOPLE_EVENT_ID,id);
                                        break;
                                    case Constant.TAG_EVENT_EDIT_DRAFT:
                                        intent.putExtra(Constant.INTENT_EVENTS_STATUS, Constant.EVENTS_STATUS_EDIT_DRAFT);
                                        intent.putExtra(Constant.INTENT_DRAFT_PEOPLE_EVENT_ID,id);
                                        break;
                                }
                                //保存或更新草稿
                                if (!isCreatedDraft) {
                                    saveDraft(intent, ADD_EVENTS);
                                } else {
                                    updateDraft(intent, ADD_EVENTS);
                                }
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show();
            }
        });
    }

    private void setListener() {
        rg_alive.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_alive:
                        ic_death_day.setVisibility(View.GONE);
                        textInputLayout_death_day.setVisibility(View.GONE);
                        ic_long_sleep.setVisibility(View.GONE);
                        textInputLayout_long_sleep_place.setVisibility(View.GONE);
                        break;
                    case R.id.rb_dead:
                        ic_death_day.setVisibility(View.VISIBLE);
                        textInputLayout_death_day.setVisibility(View.VISIBLE);
                        ic_long_sleep.setVisibility(View.VISIBLE);
                        textInputLayout_long_sleep_place.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

    private void showLoadingDialog(){
        if(loadingDialog == null) {
            loadingDialog = new ProgressDialog(this);
            loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loadingDialog.setCancelable(false);
            loadingDialog.setCanceledOnTouchOutside(false);
        }
        loadingDialog.show();
    }

    private void hideLoadingDialog(){
        if(loadingDialog!=null){
            loadingDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_COVER:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Glide.with(this).load(uri).into(iv_cover);
                    imageFile = new File(Tools.getPathFromUri(this,uri));
                }
                break;
            case ADD_EVENTS:
                if(resultCode == Constant.RESULT_EVENTS_COMMIT_OK){
                    //事件提交成功,获取标题,事件ID
                    String title = data.getStringExtra(Constant.INTENT_PEOPLE_EVENT_TITLE);
                    String id = data.getStringExtra(Constant.INTENT_PEOPLE_EVENT_ID);
                    addEventsInContainer(title,Constant.TAG_EVENT_EDIT,id);
                    String deleted_people_event_draft_id = data.getStringExtra(Constant.INTENT_PEOPLE_EVENT_DELETE_DRAFT_ID);
                    if(deleted_people_event_draft_id!=null) {
                        removeDeletedDraftIdInContains(deleted_people_event_draft_id);
                    }
                }else if(resultCode == Constant.RESULT_EVENTS_SAVE_DRAFT_OK){
                    //保存事件草稿成功,获取标题，事件草稿ID
                    String title = data.getStringExtra(Constant.INTENT_PEOPLE_EVENT_TITLE);
                    String id = data.getStringExtra(Constant.INTENT_DRAFT_PEOPLE_EVENT_ID);
                    addEventsInContainer(title,Constant.TAG_EVENT_EDIT_DRAFT,id);
                }else if(resultCode == Constant.RESULT_EVENTS_SAVE_DRAFT_FAIL){
                    //TODO 保存事件草稿失败

                }

                break;
            case ADD_DESCRIPTION:
                if(resultCode == Constant.RESULT_DESCRIPTION_COMMIT_OK){
                    //描述提交成功
                    tv_add_description.setText(R.string.edit_description);
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_edit);
                    drawable.setBounds(0,0,Tools.dip2px(this,16),Tools.dip2px(this,16));
                    tv_add_description.setCompoundDrawables(drawable,null,null,null);
                    tv_add_description.setCompoundDrawablePadding(Tools.dip2px(this,2));
                    tv_add_description.setTag(Constant.TAG_DESCRIPTION_EDIT);
                    people_description_id = data.getStringExtra(Constant.INTENT_PEOPLE_DESCRIPTION_ID);
                } else if(resultCode == Constant.RESULT_DESCRIPTION_SAVE_DRAFT_OK){
                    //保存描述草稿成功
                    tv_add_description.setText(R.string.edit_description);
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_edit);
                    drawable.setBounds(0,0,Tools.dip2px(this,16),Tools.dip2px(this,16));
                    tv_add_description.setCompoundDrawables(drawable,null,null,null);
                    tv_add_description.setCompoundDrawablePadding(Tools.dip2px(this,2));
                    tv_add_description.setTag(Constant.TAG_DESCRIPTION_EDIT_DRAFT);
                    draft_people_description_id = data.getStringExtra(Constant.INTENT_DRAFT_PEOPLE_DESCRIPTION_ID);
                }else if(resultCode == Constant.RESULT_DESCRIPTION_SAVE_DRAFT_FAIL){
                    //TODO 保存描述草稿失败
                }

                break;
        }
    }

    /**
     * 将事件添加到TextViewContainer中
     */
    private void addEventsInContainer(String title,String tag,String id){
        //添加事件数据到内存中
        if(peopleEventTexts == null){
            peopleEventTexts = new ArrayList<>();
        }
        for(int i=0;i<peopleEventTexts.size();i++){
            if(id.equals(peopleEventTexts.get(i).getId())&&tag.equals(peopleEventTexts.get(i).getTag())){
                peopleEventTexts.remove(i);
            }
        }
        peopleEventTexts.add(new PeopleEventText(title,tag,id));

        for(int i=0;i<ll_tv_events.getChildCount();i++){
            View child = ll_tv_events.getChildAt(i);
            String status = (String) child.getTag(R.string.tag_event_status);
            String status_id = (String)child.getTag(R.string.tag_event_status_id);
            //如果添加的TextView status和status_id 都相同，则认为是同一个，只要覆盖title，不用重新new
            if(tag.equals(status)&&id.equals(status_id)){
                TextView tv = (TextView) child;
                tv.setText(title);
                return;
            }
        }
        TextView event = new TextView(this);
        event.setText(title);
        event.setTag(R.string.tag_event_status,tag);
        event.setTag(R.string.tag_event_status_id,id);
        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadCelebrityActivity.this,EditEventsActivity.class);
                if (!isCreatedDraft&&Tools.isEditTextEmpty(et_celebrity_name)) {
                    //TODO 设置EditText error
                    Toast.makeText(getApplicationContext(), "姓名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra(Constant.INTENT_EVENTS_TYPE, Constant.EVENTS_TYPE_PEOPLE);
                switch ((String)v.getTag(R.string.tag_event_status)){
                    case Constant.TAG_EVENT_EDIT:
                        intent.putExtra(Constant.INTENT_EVENTS_STATUS, Constant.EVENTS_STATUS_EDIT);
                        intent.putExtra(Constant.INTENT_PEOPLE_EVENT_ID,(String)v.getTag(R.string.tag_event_status_id));
                        break;
                    case Constant.TAG_EVENT_EDIT_DRAFT:
                        intent.putExtra(Constant.INTENT_EVENTS_STATUS, Constant.EVENTS_STATUS_EDIT_DRAFT);
                        intent.putExtra(Constant.INTENT_DRAFT_PEOPLE_EVENT_ID,(String)v.getTag(R.string.tag_event_status_id));
                        break;
                }
                //保存或更新草稿
                if (!isCreatedDraft) {
                    saveDraft(intent, ADD_EVENTS);
                } else {
                    updateDraft(intent, ADD_EVENTS);
                }
            }
        });
        ll_tv_events.addTextView(event);

    }

    private void removeDeletedDraftIdInContains(String deleted_people_event_draft_id){
        for(int i=0;i<ll_tv_events.getChildCount();i++){
            View child = ll_tv_events.getChildAt(i);
            if(Constant.TAG_EVENT_EDIT_DRAFT.equals(child.getTag(R.string.tag_event_status))
                    && deleted_people_event_draft_id.equals(child.getTag(R.string.tag_event_status_id))){
                ll_tv_events.removeView(child);
            }
        }
    }

    /**
     * 添加事件
     */
    @OnClick(R.id.tv_add_events)
    public void addEvents(){
        Intent intent = new Intent(this,EditEventsActivity.class);
        if (!isCreatedDraft&&Tools.isEditTextEmpty(et_celebrity_name)) {
            //TODO 设置EditText error
            Toast.makeText(getApplicationContext(), "姓名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        intent.putExtra(Constant.INTENT_EVENTS_TYPE, Constant.EVENTS_TYPE_PEOPLE);
        intent.putExtra(Constant.INTENT_EVENTS_STATUS, Constant.EVENTS_STATUS_ADD_NEW);
        //保存或更新草稿
        if (!isCreatedDraft) {
            saveDraft(intent, ADD_EVENTS);
        } else {
            updateDraft(intent, ADD_EVENTS);
        }
    }

    /**
     * 添加描述
     */
    @OnClick(R.id.tv_add_description)
    public void addDescription(){
        Intent intent = new Intent(this, EditDescriptionActivity.class);
        if (!isCreatedDraft&&Tools.isEditTextEmpty(et_celebrity_name)) {
            //TODO 设置EditText error
            Toast.makeText(getApplicationContext(), "姓名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        intent.putExtra(Constant.INTENT_DESCRIPTION_TYPE, Constant.DESCRIPTION_TYPE_PEOPLE);
        if(Constant.TAG_DESCRIPTION_ADD_NEW.equals(tv_add_description.getTag())) {
            intent.putExtra(Constant.INTENT_DESCRIPTION_STATUS, Constant.DESCRIPTION_STATUS_ADD_NEW);
        }else if(Constant.TAG_DESCRIPTION_EDIT.equals(tv_add_description.getTag())){
            intent.putExtra(Constant.INTENT_DESCRIPTION_STATUS, Constant.DESCRIPTION_STATUS_EDIT);
            intent.putExtra(Constant.INTENT_PEOPLE_DESCRIPTION_ID,people_description_id);
        }else if(Constant.TAG_DESCRIPTION_EDIT_DRAFT.equals(tv_add_description.getTag())){
            intent.putExtra(Constant.INTENT_DESCRIPTION_STATUS, Constant.DESCRIPTION_STATUS_EDIT_DRAFT);
            intent.putExtra(Constant.INTENT_DRAFT_PEOPLE_DESCRIPTION_ID,draft_people_description_id);
        }
        //保存或更新草稿
        if (!isCreatedDraft) {
            saveDraft(intent, ADD_DESCRIPTION);
        } else {
            updateDraft(intent, ADD_DESCRIPTION);
        }

    }



    /**
     * 选择封面
     */
    @OnClick(R.id.btn_select_cover)
    public void selectCover(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, SELECT_COVER);
    }

    @OnFocusChange(R.id.et_celebrity_nationality)
    public void nationalityFocusChange(boolean focus){
        if(focus) {
            showCountryPicker();
        }
    }

    /**
     * 点击国籍EditText
     */
    @OnClick(R.id.et_celebrity_nationality)
    public void clickNationality(){
        showCountryPicker();
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
                //TODO 给个dialog提示 返回主界面
                finish();
                break;
            case R.id.action_menu_commit:
                upLoadPeople();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 保存到草稿
     * @param intent 保存草稿成功后的意图
     */
    private void saveDraft(@Nullable final Intent intent, final int requestCode){
        showLoadingDialog();

        final MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);//表单类型
        builder.addFormDataPart("username", "13167231015");
        if(Tools.isEditTextEmpty(et_celebrity_name)) {
            hideLoadingDialog();
            return;
        }
        builder.addFormDataPart("name", et_celebrity_name.getText().toString());
        if(!Tools.isEditTextEmpty(et_celebrity_nationality)) {
            builder.addFormDataPart("nationality", et_celebrity_nationality.getText().toString());
        }
        if(!Tools.isEditTextEmpty(et_birth_place)) {
            builder.addFormDataPart("birthplace", et_birth_place.getText().toString());
        }
        if(!Tools.isEditTextEmpty(et_residence)) {
            builder.addFormDataPart("residence", et_residence.getText().toString());
        }
        if(!Tools.isEditTextEmpty(et_long_sleep_place)) {
            builder.addFormDataPart("grave_place", et_long_sleep_place.getText().toString());
        }
        if(!Tools.isEditTextEmpty(et_birthday)) {
            builder.addFormDataPart("birth_day", et_birthday.getText().toString());
        }
        if(!Tools.isEditTextEmpty(et_death_day)) {
            builder.addFormDataPart("death_day", et_death_day.getText().toString());
        }
        if(!Tools.isEditTextEmpty(et_motto)) {
            builder.addFormDataPart("motto", et_motto.getText().toString());
        }
        if(!Tools.isEditTextEmpty(et_industry)) {
            builder.addFormDataPart("industry", et_industry.getText().toString());
        }
        if(imageFile!=null) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), imageFile);
            builder.addFormDataPart("cover", imageFile.getName(), fileBody);
        }
        builder.addFormDataPart("time_stamp",Tools.getTimeStamp());
        List<MultipartBody.Part> partList = builder.build().parts();
        aboutPeopleService.saveDraftPeople(partList).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonSaveDraftPeopleResultBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(app,"服务器错误",Toast.LENGTH_SHORT).show();
                        hideLoadingDialog();
                    }

                    @Override
                    public void onNext(GsonSaveDraftPeopleResultBean gsonSaveDraftPeopleResultBean) {
                        hideLoadingDialog();
                        switch (gsonSaveDraftPeopleResultBean.getCode()){
                            case 0:
                                if(intent!=null) {
                                    draft_people_id = gsonSaveDraftPeopleResultBean.getDraft_people_id();
                                    isCreatedDraft = true;
                                    intent.putExtra(Constant.INTENT_PEOPLE_DRAFT_ID,draft_people_id);
                                    UploadCelebrityActivity.this.startActivityForResult(intent,requestCode);
                                }
                                Toast.makeText(app,"已保存到草稿",Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(app,"保存草稿失败",Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
    }

    /**
     *更新草稿
     * @param intent 更新草稿成功后的意图
     */
    private void updateDraft(final Intent intent, final int requestCode) {
        showLoadingDialog();

        final MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);//表单类型
        builder.addFormDataPart("draft_people_id",draft_people_id);
        builder.addFormDataPart("username", "13167231015");
        if(Tools.isEditTextEmpty(et_celebrity_name)) {
            hideLoadingDialog();
            return;
        }
        builder.addFormDataPart("name", et_celebrity_name.getText().toString());
        if(!Tools.isEditTextEmpty(et_celebrity_nationality)) {
            builder.addFormDataPart("nationality", et_celebrity_nationality.getText().toString());
        }
        if(!Tools.isEditTextEmpty(et_birth_place)) {
            builder.addFormDataPart("birthplace", et_birth_place.getText().toString());
        }
        if(!Tools.isEditTextEmpty(et_residence)) {
            builder.addFormDataPart("residence", et_residence.getText().toString());
        }
        if(!Tools.isEditTextEmpty(et_long_sleep_place)) {
            builder.addFormDataPart("grave_place", et_long_sleep_place.getText().toString());
        }
        if(!Tools.isEditTextEmpty(et_birthday)) {
            builder.addFormDataPart("birth_day", et_birthday.getText().toString());
        }
        if(!Tools.isEditTextEmpty(et_death_day)) {
            builder.addFormDataPart("death_day", et_death_day.getText().toString());
        }
        if(!Tools.isEditTextEmpty(et_motto)) {
            builder.addFormDataPart("motto", et_motto.getText().toString());
        }
        if(!Tools.isEditTextEmpty(et_industry)) {
            builder.addFormDataPart("industry", et_industry.getText().toString());
        }
        if(imageFile!=null) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), imageFile);
            builder.addFormDataPart("cover", imageFile.getName(), fileBody);
        }
        builder.addFormDataPart("time_stamp",Tools.getTimeStamp());
        List<MultipartBody.Part> partList = builder.build().parts();
        aboutPeopleService.updateDraftPeople(partList).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonUpdateDraftPeopleResultBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(app,"服务器错误",Toast.LENGTH_SHORT).show();
                        hideLoadingDialog();
                    }

                    @Override
                    public void onNext(GsonUpdateDraftPeopleResultBean gsonUpdateDraftPeopleResultBean) {
                        hideLoadingDialog();
                        switch (gsonUpdateDraftPeopleResultBean.getCode()){
                            case 0:
                                if(intent!=null) {
                                    isCreatedDraft = true;
                                    intent.putExtra(Constant.INTENT_PEOPLE_DRAFT_ID,draft_people_id);
                                    UploadCelebrityActivity.this.startActivityForResult(intent,requestCode);
                                }
                                Toast.makeText(app,"已更新草稿",Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(app,"更新草稿失败",Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
    }

    /**
     * 提交
     */
    private void upLoadPeople() {
        showLoadingDialog();

        final MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);//表单类型
        builder.addFormDataPart("username", "13167231015");
        if(Tools.isEditTextEmpty(et_celebrity_name)) {
            hideLoadingDialog();
            return;
        }
        builder.addFormDataPart("name", et_celebrity_name.getText().toString());
        if(!Tools.isEditTextEmpty(et_celebrity_nationality)) {
            builder.addFormDataPart("nationality", et_celebrity_nationality.getText().toString());
        }
        if(!Tools.isEditTextEmpty(et_birth_place)) {
            builder.addFormDataPart("birthplace", et_birth_place.getText().toString());
        }
        if(!Tools.isEditTextEmpty(et_residence)) {
            builder.addFormDataPart("residence", et_residence.getText().toString());
        }
        if(!Tools.isEditTextEmpty(et_long_sleep_place)) {
            builder.addFormDataPart("grave_place", et_long_sleep_place.getText().toString());
        }
        if(!Tools.isEditTextEmpty(et_birthday)) {
            builder.addFormDataPart("birth_day", et_birthday.getText().toString());
        }
        if(!Tools.isEditTextEmpty(et_death_day)) {
            builder.addFormDataPart("death_day", et_death_day.getText().toString());
        }
        if(!Tools.isEditTextEmpty(et_motto)) {
            builder.addFormDataPart("motto", et_motto.getText().toString());
        }
        if(!Tools.isEditTextEmpty(et_industry)) {
            builder.addFormDataPart("industry", et_industry.getText().toString());
        }
        if(imageFile!=null) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), imageFile);
            builder.addFormDataPart("cover", imageFile.getName(), fileBody);
        }
        builder.addFormDataPart("time_stamp","201801301419");
        List<MultipartBody.Part> partList = builder.build().parts();
        aboutPeopleService.uploadPeople(partList).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonUploadPeopleResultBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(app,"服务器错误",Toast.LENGTH_SHORT).show();
                        hideLoadingDialog();
                    }

                    @Override
                    public void onNext(GsonUploadPeopleResultBean gsonUploadPeopleResultBean) {
                        hideLoadingDialog();
                        switch (gsonUploadPeopleResultBean.getCode()){
                            case 0:
                                //TODO 提交成功
                                break;
                            default:
                                Toast.makeText(app,"提交失败",Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
    }

    /**
     * 选择国籍的dialog
     */
    private void showCountryPicker() {
        if (picker == null) {
            picker = CountryPicker.newInstance(getResources().getString(R.string.select_country));
            picker.setListener(new CountryPickerListener() {
                @Override
                public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                    // Implement your code here
                    Drawable image = getResources().getDrawable(flagDrawableResID);
                    int h = image.getIntrinsicHeight();
                    int w = image.getIntrinsicWidth();
                    image.setBounds(0, 0, w / 2, h / 2);
                    et_celebrity_nationality.setCompoundDrawables(image, null, null, null);
                    et_celebrity_nationality.setCompoundDrawablePadding(Tools.dip2px(UploadCelebrityActivity.this, 5));
                    et_celebrity_nationality.setText(name);
                    picker.dismiss();
                }
            });
        }
        if (!picker.isVisible()) {
            picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
        }
    }

    interface AboutPeopleService{
        @Multipart
        @POST("people/commit")
        Observable<GsonUploadPeopleResultBean> uploadPeople(@Part List<MultipartBody.Part> partList);
        @Multipart
        @POST("draft/people/commit")
        Observable<GsonSaveDraftPeopleResultBean> saveDraftPeople(@Part List<MultipartBody.Part> partList);
        @Multipart
        @POST("draft/people/update")
        Observable<GsonUpdateDraftPeopleResultBean> updateDraftPeople(@Part List<MultipartBody.Part> partList);
    }
}
//
//                       _oo0oo_
//                      o8888888o
//                      88" . "88
//                      (| -_- |)
//                      0\  =  /0
//                    ___/`---'\___
//                  .' \\|     |// '.
//                 / \\|||  :  |||// \
//                / _||||| -:- |||||- \
//               |   | \\\  -  /// |   |
//               | \_|  ''\---/''  |_/ |
//               \  .-\__  '-'  ___/-. /
//             ___'. .'  /--.--\  `. .'___
//          ."" '<  `.___\_<|>_/___.' >' "".
//         | | :  `- \`.;`\ _ /`;.`/ - ` : | |
//         \  \ `_.   \_ __\ /__ _/   .-` /  /
//     =====`-.____`.___ \_____/___.-`___.-'=====
//                       `=---='
//
//
//     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//
//               佛祖保佑         永无BUG
//
//
//

/*code is far away from bug with the animal protecting
    *  ┏┓　　　┏┓
    *┏┛┻━━━┛┻┓
    *┃　　　　　　　┃ 　
    *┃　　　━　　　┃
    *┃　┳┛　┗┳　┃
    *┃　　　　　　　┃
    *┃　　　┻　　　┃
    *┃　　　　　　　┃
    *┗━┓　　　┏━┛
    *　　┃　　　┃神兽保佑
    *　　┃　　　┃代码无BUG！
    *　　┃　　　┗━━━┓
    *　　┃　　　　　　　┣┓
    *　　┃　　　　　　　┏┛
    *　　┗┓┓┏━┳┓┏┛
    *　　　┃┫┫　┃┫┫
    *　　　┗┻┛　┗┻┛
    *　　　
    */

/**
 *　　　　　　　　┏┓　　　┏┓+ +
 *　　　　　　　┏┛┻━━━┛┻┓ + +
 *　　　　　　　┃　　　　　　　┃ 　
 *　　　　　　　┃　　　━　　　┃ ++ + + +
 *　　　　　　 ████━████ ┃+
 *　　　　　　　┃　　　　　　　┃ +
 *　　　　　　　┃　　　┻　　　┃
 *　　　　　　　┃　　　　　　　┃ + +
 *　　　　　　　┗━┓　　　┏━┛
 *　　　　　　　　　┃　　　┃　　　　　　　　　　　
 *　　　　　　　　　┃　　　┃ + + + +
 *　　　　　　　　　┃　　　┃　　　　Code is far away from bug with the animal protecting　　　　　　　
 *　　　　　　　　　┃　　　┃ + 　　　　神兽保佑,代码无bug　　
 *　　　　　　　　　┃　　　┃
 *　　　　　　　　　┃　　　┃　　+　　　　　　　　　
 *　　　　　　　　　┃　 　　┗━━━┓ + +
 *　　　　　　　　　┃ 　　　　　　　┣┓
 *　　　　　　　　　┃ 　　　　　　　┏┛
 *　　　　　　　　　┗┓┓┏━┳┓┏┛ + + + +
 *　　　　　　　　　　┃┫┫　┃┫┫
 *　　　　　　　　　　┗┻┛　┗┻┛+ + + +
 */