package com.zou.graytrace.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
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
import com.zou.graytrace.view.TextViewContainer;

import java.io.File;
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
//TODO 1.editText获取焦点，但是不弹出键盘
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
    private String description_id;
    private String event_ids;
    //若从添加描述或者添加事件中返回则改值为true
    private boolean isCreatedDraft;
    //人物草稿ID
    private String draft_people_id;
    //人物描述ID
    private String people_description_id;
    //人物描述草稿ID
    private String draft_people_description_id;


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
                    //事件提交成功

                }else if(resultCode == Constant.RESULT_EVENTS_SAVE_DRAFT_OK){
                    //保存事件草稿成功

                }else if(resultCode == Constant.RESULT_EVENTS_SAVE_DRAFT_FAIL){
                    //TODO 保存事件草稿失败

                }

                break;
            case ADD_DESCRIPTION:
                if(resultCode == Constant.RESULT_DESCRIPTION_COMMIT_OK){
                    //描述提交成功
                    tv_add_description.setText(R.string.edit_description);
                    tv_add_description.setTag(Constant.TAG_DESCRIPTION_EDIT);
                    people_description_id = data.getStringExtra(Constant.INTENT_PEOPLE_DESCRIPTION_ID);
                } else if(resultCode == Constant.RESULT_DESCRIPTION_SAVE_DRAFT_OK){
                    //保存描述草稿成功
                    tv_add_description.setText(R.string.edit_description);
                    tv_add_description.setTag(Constant.TAG_DESCRIPTION_EDIT_DRAFT);
                    draft_people_description_id = data.getStringExtra(Constant.INTENT_DRAFT_PEOPLE_DESCRIPTION_ID);
                }else if(resultCode == Constant.RESULT_DESCRIPTION_SAVE_DRAFT_FAIL){
                    //TODO 保存描述草稿失败
                }

                break;
        }
    }

    int count = 0;
    @OnClick(R.id.ic_events)
    public void test(){
        count ++;
        int maxMidth = Tools.getScreenWidth(this)-ic_events.getMeasuredWidth()-Tools.dip2px(app,24);
        ll_tv_events.setMaxWidth(maxMidth);
        TextView tv_test = new TextView(this);
        tv_test.setText(count+"测试测试");
        ll_tv_events.addTextView(tv_test);
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
        builder.addFormDataPart("time_stamp","201801301419");
        if(description_id!=null) {
            builder.addFormDataPart("description_id", description_id);
        }
        if(event_ids!=null) {
            builder.addFormDataPart("event_ids", event_ids);
        }
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
        builder.addFormDataPart("time_stamp","201801301419");
        if(description_id!=null) {
            builder.addFormDataPart("description_id", description_id);
        }
        if(event_ids!=null) {
            builder.addFormDataPart("event_ids", event_ids);
        }
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
        if(description_id!=null) {
            builder.addFormDataPart("description_id", description_id);
        }
        if(event_ids!=null) {
            builder.addFormDataPart("event_ids", event_ids);
        }
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