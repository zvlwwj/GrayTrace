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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.CountryPickerListener;
import com.zou.graytrace.R;
import com.zou.graytrace.Utils.Constant;
import com.zou.graytrace.Utils.Tools;
import com.zou.graytrace.Utils.URL;
import com.zou.graytrace.application.GrayTraceApplication;
import com.zou.graytrace.bean.GsonUploadPeopleResultBean;

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
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
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
    private File imageFile;
    private UpLoadPeopleService upLoadPeopleService;
    private GrayTraceApplication app;
    private ProgressDialog loadingDialog;


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
        upLoadPeopleService = retrofit.create(UpLoadPeopleService.class);
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
                if(resultCode == RESULT_OK){

                }
                break;
            case ADD_DESCRIPTION:
                if(resultCode == RESULT_OK){

                }
                break;
        }
    }

    /**
     * 添加事件
     */
    @OnClick(R.id.tv_add_events)
    public void addEvents(){
        Intent intent = new Intent(this,EditEventsActivity.class);
        startActivityForResult(intent,ADD_EVENTS);
    }

    /**
     * 添加描述
     */
    @OnClick(R.id.tv_add_description)
    public void addDescription(){
        if(Tools.isEditTextEmpty(et_celebrity_name)){
            //TODO 设置EditText error
            Toast.makeText(getApplicationContext(),"用户名不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this,EditDescriptionActivity.class);
        intent.putExtra(Constant.INTENT_DESCRIPTION_TYPE,Constant.DESCRIPTION_TYPE_PEOPLE);
        //TODO 从偏好设置中取出用户名
        intent.putExtra(Constant.INTENT_USER_NAME,"13167231015");
        intent.putExtra(Constant.INTENT_PEOPLE_NAME,et_celebrity_name.getText().toString());
        startActivityForResult(intent,ADD_DESCRIPTION);
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
                //TODO 提交
                upLoadPeople();
                break;
        }
        return super.onOptionsItemSelected(item);
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
        upLoadPeopleService.uploadPeople(partList).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
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

    interface UpLoadPeopleService{
        @Multipart
        @POST("commit/people")
        Observable<GsonUploadPeopleResultBean> uploadPeople(@Part List<MultipartBody.Part> partList);
    }
}