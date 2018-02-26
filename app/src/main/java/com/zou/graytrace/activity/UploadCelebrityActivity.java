package com.zou.graytrace.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.CountryPickerListener;
import com.zou.graytrace.R;
import com.zou.graytrace.Utils.Constant;
import com.zou.graytrace.Utils.Tools;
import com.zou.graytrace.Utils.URL;
import com.zou.graytrace.application.GrayTraceApplication;
import com.zou.graytrace.bean.GsonGetDraftPeopleResultBean;
import com.zou.graytrace.bean.GsonGetPeopleResultBean;
import com.zou.graytrace.bean.GsonSaveDraftPeopleResultBean;
import com.zou.graytrace.bean.GsonUpdateDraftPeopleResultBean;
import com.zou.graytrace.bean.GsonUploadFileResultBean;
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
import okhttp3.MultipartBody;
import retrofit2.Retrofit;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
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
/**
 * Created by zou on 2018/1/18.
 * 上传界面
 * TODO 没有考虑正在上传封面时用户点击上传的情况
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
    @BindView(R.id.textInputLayout_celebrity_name)
    TextInputLayout textInputLayout_celebrity_name;
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
    private GrayTraceApplication.PublicService publicService;
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

    private ArrayList<PeopleEventText> peopleEventTexts;

    private String cover_url;

    private AlertDialog saveDraftDialog;

    private String stauts;

    private boolean alive = true;
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
        publicService = retrofit.create(GrayTraceApplication.PublicService.class);
        tv_add_description.setTag(Constant.TAG_DESCRIPTION_ADD_NEW);
        stauts = getIntent().getStringExtra(Constant.INTENT_PEOPLE_STATUS);
        if(Constant.PEOPLE_STATUS_EDIT.equals(stauts)){
            getPeopleInfo();
        }else if(Constant.PEOPLE_STATUS_EDIT_DRAFT.equals(stauts)){
            isCreatedDraft = true;
            getPeopleInfoFromDraft();
        }
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
                    textInputLayout_celebrity_name.setError(getResources().getString(R.string.name_empty_error));
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
                                if(stauts.equals(Constant.PEOPLE_STATUS_ADD_NEW)||stauts.equals(Constant.PEOPLE_STATUS_EDIT_DRAFT)) {
                                    if (!isCreatedDraft) {
                                        saveDraft(intent, ADD_EVENTS);
                                    } else {
                                        updateDraft(intent, ADD_EVENTS);
                                    }
                                }else if(stauts.equals(Constant.PEOPLE_STATUS_EDIT)){
                                    String people_id = getIntent().getStringExtra(Constant.INTENT_PEOPLE_ID);
                                    intent.putExtra(Constant.INTENT_PEOPLE_ID,people_id);
                                    startActivityForResult(intent,ADD_EVENTS);
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
                        alive = true;
                        break;
                    case R.id.rb_dead:
                        ic_death_day.setVisibility(View.VISIBLE);
                        textInputLayout_death_day.setVisibility(View.VISIBLE);
                        ic_long_sleep.setVisibility(View.VISIBLE);
                        textInputLayout_long_sleep_place.setVisibility(View.VISIBLE);
                        alive = false;
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
                    //TODO 替换username
                    String fileName = "username"+"_"+Tools.getTimeStamp()+".png";
                    String url = URL.BASE_PIC_COVER;
                    cover_url = url + fileName;
                    List<MultipartBody.Part> partList = Tools.getFilePartList(imageFile, url,fileName);
                    publicService.uploadFile(partList).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<GsonUploadFileResultBean>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(UploadCelebrityActivity.this,"服务器错误",Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onNext(GsonUploadFileResultBean gsonUploadFileResultBean) {
                                    switch (gsonUploadFileResultBean.getCode()){
                                        case 0:
                                            Toast.makeText(UploadCelebrityActivity.this,"上传图片成功",Toast.LENGTH_SHORT).show();
                                            break;
                                        default:
                                            Toast.makeText(UploadCelebrityActivity.this,"上传图片失败",Toast.LENGTH_SHORT).show();
                                            break;
                                    }
                                }
                            });
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
                    draft_people_description_id = null;
                } else if(resultCode == Constant.RESULT_DESCRIPTION_SAVE_DRAFT_OK){
                    //保存描述草稿成功
                    tv_add_description.setText(R.string.edit_description);
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_edit);
                    drawable.setBounds(0,0,Tools.dip2px(this,16),Tools.dip2px(this,16));
                    tv_add_description.setCompoundDrawables(drawable,null,null,null);
                    tv_add_description.setCompoundDrawablePadding(Tools.dip2px(this,2));
                    tv_add_description.setTag(Constant.TAG_DESCRIPTION_EDIT_DRAFT);
                    draft_people_description_id = data.getStringExtra(Constant.INTENT_DRAFT_PEOPLE_DESCRIPTION_ID);
                    people_description_id = null;
                }

                break;
        }
    }

    /**
     * 将事件添加到TextViewContainer中
     */
    private void addEventsInContainer(String title, String tag, String id){
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
                    textInputLayout_celebrity_name.setError(getResources().getString(R.string.name_empty_error));
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
                //若是ADD_NEW或者EDIT_DRAFT 则保存或更新草稿
                if(stauts.equals(Constant.PEOPLE_STATUS_ADD_NEW)||stauts.equals(Constant.PEOPLE_STATUS_EDIT_DRAFT)){
                    if (!isCreatedDraft) {
                        saveDraft(intent, ADD_EVENTS);
                    } else {
                        updateDraft(intent, ADD_EVENTS);
                    }
                }else if(stauts.equals(Constant.PEOPLE_STATUS_EDIT)){
                    String people_id = getIntent().getStringExtra(Constant.INTENT_PEOPLE_ID);
                    intent.putExtra(Constant.INTENT_PEOPLE_ID,people_id);
                    startActivityForResult(intent,ADD_EVENTS);
                }
            }
        });
        ll_tv_events.addTextView(event);

    }

    private void removeDeletedDraftIdInContains(String deleted_people_event_draft_id){
        for(int i=0;i<peopleEventTexts.size();i++){
            if(deleted_people_event_draft_id.equals(peopleEventTexts.get(i).getId())&&Constant.TAG_EVENT_EDIT_DRAFT.equals(peopleEventTexts.get(i).getTag())){
                peopleEventTexts.remove(i);
            }
        }
        for(int i=0;i<ll_tv_events.getChildCount();i++){
            View child = ll_tv_events.getChildAt(i);
            if(Constant.TAG_EVENT_EDIT_DRAFT.equals(child.getTag(R.string.tag_event_status))
                    && deleted_people_event_draft_id.equals(child.getTag(R.string.tag_event_status_id))){
                ll_tv_events.removeView(child);
            }
        }
    }

    /**
     * 获取人物信息
     */
    private void getPeopleInfo() {
        String people_id = getIntent().getStringExtra(Constant.INTENT_PEOPLE_ID);
        aboutPeopleService.getPeopleInfo(people_id).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<GsonGetPeopleResultBean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(UploadCelebrityActivity.this,"服务器错误",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(GsonGetPeopleResultBean gsonGetPeopleResultBean) {
                switch (gsonGetPeopleResultBean.getCode()){
                    case 0:
                        //封面
                        String cover_url = gsonGetPeopleResultBean.getInfo().getCover_url();
                        if(cover_url!=null){
                            Glide.with(UploadCelebrityActivity.this).load(cover_url).into(iv_cover);
                        }
                        //alive=0表示活着，alive=1表示死了
                        int alive = gsonGetPeopleResultBean.getInfo().getAlive();
                        if(alive == 1){
                            rg_alive.check(R.id.rb_dead);
                        }
                        //姓名栏
                        String name = gsonGetPeopleResultBean.getInfo().getName();
                        if(name!=null){
                            et_celebrity_name.setText(name);
                        }
                        //生日
                        String birthDay = gsonGetPeopleResultBean.getInfo().getBirth_day();
                        if(birthDay!=null){
                            et_birthday.setText(birthDay);
                        }
                        //祭日
                        String deathDay = gsonGetPeopleResultBean.getInfo().getDeath_day();
                        if(deathDay!=null){
                            et_death_day.setText(deathDay);
                        }
                        //国籍
                        String nationality = gsonGetPeopleResultBean.getInfo().getNationality();
                        if(nationality!=null){
                            et_celebrity_nationality.setText(nationality);
                        }
                        //出生地
                        String birthPlace = gsonGetPeopleResultBean.getInfo().getBirthplace();
                        if(birthPlace!=null){
                            et_birth_place.setText(birthPlace);
                        }
                        //长眠地
                        String longSleepPlace = gsonGetPeopleResultBean.getInfo().getGrave_place();
                        if(longSleepPlace!=null){
                            et_long_sleep_place.setText(longSleepPlace);
                        }
                        //居住地
                        String residence = gsonGetPeopleResultBean.getInfo().getResidence();
                        if(residence!=null){
                            et_residence.setText(residence);
                        }
                        //行业
                        String industry = gsonGetPeopleResultBean.getInfo().getIndustry();
                        if(industry!=null) {
                            et_industry.setText(industry);
                        }
                        //一句话描述
                        String motto = gsonGetPeopleResultBean.getInfo().getMotto();
                        if(motto!=null) {
                            et_motto.setText(motto);
                        }
                        //描述
                        String people_description_id = gsonGetPeopleResultBean.getInfo().getDescription().getDescription_id();
                        if(people_description_id != null){
                            UploadCelebrityActivity.this.people_description_id = people_description_id;
                            tv_add_description.setText(R.string.edit_description);
                            Drawable drawable = getResources().getDrawable(R.drawable.ic_edit);
                            drawable.setBounds(0,0,Tools.dip2px(UploadCelebrityActivity.this,16),Tools.dip2px(UploadCelebrityActivity.this,16));
                            tv_add_description.setCompoundDrawables(drawable,null,null,null);
                            tv_add_description.setCompoundDrawablePadding(Tools.dip2px(UploadCelebrityActivity.this,2));
                            tv_add_description.setTag(Constant.TAG_DESCRIPTION_EDIT);
                        }
                        //事件
                        ArrayList<GsonGetPeopleResultBean.Info.Event> events = gsonGetPeopleResultBean.getInfo().getEvents();
                        if(events!=null&&events.size()>0){
                            for(GsonGetPeopleResultBean.Info.Event event : events){
                                addEventsInContainer(event.getEvent_title(),Constant.TAG_EVENT_EDIT,event.getEvent_id());
                            }
                        }
                        break;
                    default:
                        Toast.makeText(UploadCelebrityActivity.this,"获取人物信息失败",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    /**
     * 获取人物草稿中的信息
     */
    private void getPeopleInfoFromDraft() {
        String draft_people_id = getIntent().getStringExtra(Constant.INTENT_DRAFT_PEOPLE_ID);
        UploadCelebrityActivity.this.draft_people_id = draft_people_id;
        aboutPeopleService.getDraftPeopleInfo(draft_people_id).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<GsonGetDraftPeopleResultBean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(UploadCelebrityActivity.this,"服务器错误",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(GsonGetDraftPeopleResultBean gsonGetDraftPeopleResultBean) {
                switch (gsonGetDraftPeopleResultBean.getCode()){
                    case 0:
                        //封面
                        String cover_url = gsonGetDraftPeopleResultBean.getInfo().getCover_url();
                        if(cover_url!=null){
                            Glide.with(UploadCelebrityActivity.this).load(cover_url).into(iv_cover);
                        }
                        //alive=0表示活着，alive=1表示死了
                        int alive = gsonGetDraftPeopleResultBean.getInfo().getAlive();
                        if(alive == 1){
                            rg_alive.check(R.id.rb_dead);
                        }
                        //姓名栏
                        String name = gsonGetDraftPeopleResultBean.getInfo().getName();
                        if(name!=null){
                            et_celebrity_name.setText(name);
                        }
                        //生日
                        String birthDay = gsonGetDraftPeopleResultBean.getInfo().getBirth_day();
                        if(birthDay!=null){
                            et_birthday.setText(birthDay);
                        }
                        //祭日
                        String deathDay = gsonGetDraftPeopleResultBean.getInfo().getDeath_day();
                        if(deathDay!=null){
                            et_death_day.setText(deathDay);
                        }
                        //国籍
                        String nationality = gsonGetDraftPeopleResultBean.getInfo().getNationality();
                        if(nationality!=null){
                            et_celebrity_nationality.setText(nationality);
                        }
                        //出生地
                        String birthPlace = gsonGetDraftPeopleResultBean.getInfo().getBirthplace();
                        if(birthPlace!=null){
                            et_birth_place.setText(birthPlace);
                        }
                        //长眠地
                        String longSleepPlace = gsonGetDraftPeopleResultBean.getInfo().getGrave_place();
                        if(longSleepPlace!=null){
                            et_long_sleep_place.setText(longSleepPlace);
                        }
                        //居住地
                        String residence = gsonGetDraftPeopleResultBean.getInfo().getResidence();
                        if(residence!=null){
                            et_residence.setText(residence);
                        }
                        //行业
                        String industry = gsonGetDraftPeopleResultBean.getInfo().getIndustry();
                        if(industry!=null) {
                            et_industry.setText(industry);
                        }
                        //一句话描述
                        String motto = gsonGetDraftPeopleResultBean.getInfo().getMotto();
                        if(motto!=null) {
                            et_motto.setText(motto);
                        }
                        //描述
                        String people_description_id = gsonGetDraftPeopleResultBean.getInfo().getDescription().getDescription_id();
                        if(people_description_id != null){
                            UploadCelebrityActivity.this.people_description_id = people_description_id;
                            tv_add_description.setText(R.string.edit_description);
                            Drawable drawable = getResources().getDrawable(R.drawable.ic_edit);
                            drawable.setBounds(0,0,Tools.dip2px(UploadCelebrityActivity.this,16),Tools.dip2px(UploadCelebrityActivity.this,16));
                            tv_add_description.setCompoundDrawables(drawable,null,null,null);
                            tv_add_description.setCompoundDrawablePadding(Tools.dip2px(UploadCelebrityActivity.this,2));
                            tv_add_description.setTag(Constant.TAG_DESCRIPTION_EDIT);
                        }
                        //事件
                        ArrayList<GsonGetDraftPeopleResultBean.Info.Event> events = gsonGetDraftPeopleResultBean.getInfo().getEvents();
                        if(events!=null&&events.size()>0){
                            for(GsonGetDraftPeopleResultBean.Info.Event event : events){
                                addEventsInContainer(event.getEvent_title(),Constant.TAG_EVENT_EDIT,event.getEvent_id());
                            }
                        }
                        break;
                    default:
                        Toast.makeText(UploadCelebrityActivity.this,"获取人物草稿信息失败",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    /**
     * 添加事件
     */
    @OnClick(R.id.tv_add_events)
    public void addEvents(){
        Intent intent = new Intent(this,EditEventsActivity.class);
        if (!isCreatedDraft&&Tools.isEditTextEmpty(et_celebrity_name)) {
            textInputLayout_celebrity_name.setError(getResources().getString(R.string.name_empty_error));
            return;
        }
        intent.putExtra(Constant.INTENT_EVENTS_TYPE, Constant.EVENTS_TYPE_PEOPLE);
        intent.putExtra(Constant.INTENT_EVENTS_STATUS, Constant.EVENTS_STATUS_ADD_NEW);
        //保存或更新草稿
        if(stauts.equals(Constant.PEOPLE_STATUS_ADD_NEW)||stauts.equals(Constant.PEOPLE_STATUS_EDIT_DRAFT)) {
            if (!isCreatedDraft) {
                saveDraft(intent, ADD_EVENTS);
            } else {
                updateDraft(intent, ADD_EVENTS);
            }
        }else if(stauts.equals(Constant.PEOPLE_STATUS_EDIT)){
            String people_id = getIntent().getStringExtra(Constant.INTENT_PEOPLE_ID);
            intent.putExtra(Constant.INTENT_PEOPLE_ID,people_id);
            startActivityForResult(intent,ADD_EVENTS);
        }
    }

    /**
     * 添加描述
     */
    @OnClick(R.id.tv_add_description)
    public void addDescription(){
        Intent intent = new Intent(this, EditDescriptionActivity.class);
        if (!isCreatedDraft&&Tools.isEditTextEmpty(et_celebrity_name)) {
            textInputLayout_celebrity_name.setError(getResources().getString(R.string.name_empty_error));
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
        if(stauts.equals(Constant.PEOPLE_STATUS_ADD_NEW)||stauts.equals(Constant.PEOPLE_STATUS_EDIT_DRAFT)) {
            if (!isCreatedDraft) {
                saveDraft(intent, ADD_DESCRIPTION);
            } else {
                updateDraft(intent, ADD_DESCRIPTION);
            }
        }else if(stauts.equals(Constant.PEOPLE_STATUS_EDIT)){
            String people_id = getIntent().getStringExtra(Constant.INTENT_PEOPLE_ID);
            intent.putExtra(Constant.INTENT_PEOPLE_ID,people_id);
            startActivityForResult(intent,ADD_DESCRIPTION);
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
                onBackPressed();

                break;
            case R.id.action_menu_commit:
                if(stauts.equals(Constant.PEOPLE_STATUS_EDIT)){
                    updatePeople();
                }else {
                    upLoadPeople();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 保存到草稿
     * @param intent 保存草稿成功后的意图
     */
    private void saveDraft(@Nullable final Intent intent, @Nullable final int requestCode){
        if(Tools.isEditTextEmpty(et_celebrity_name)) {
            hideLoadingDialog();
            return;
        }
        showLoadingDialog();
        //TODO 替换用户名
        String username = "13167231015";
        String name = et_celebrity_name.getText().toString();
        String nationality = et_celebrity_nationality.getText().toString();
        String birthplace = et_birth_place.getText().toString();
        String residence = et_residence.getText().toString();
        String grave_place = et_long_sleep_place.getText().toString();
        String birth_day = et_birthday.getText().toString();
        String death_day = et_death_day.getText().toString();
        String motto = et_motto.getText().toString();
        String industry = et_industry.getText().toString();
        String cover_url = this.cover_url;
        String time_stamp = Tools.getTimeStamp();
        int alive = this.alive?0:1;
        String event_ids = getEventIds();
        String description_id = UploadCelebrityActivity.this.people_description_id;

        aboutPeopleService.saveDraftPeople(username,name,nationality,birthplace,residence,grave_place,birth_day,death_day,motto,industry,cover_url,time_stamp,alive,event_ids,description_id)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
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
                                }else{
                                    finish();
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

    private String getEventIds(){
        String event_ids = null;
        if(peopleEventTexts!=null&&peopleEventTexts.size()>0){
            StringBuilder stringBuilder = new StringBuilder();
            for(PeopleEventText peopleEventText:peopleEventTexts){
                if(peopleEventText.getTag().equals(Constant.TAG_EVENT_EDIT)) {
                    stringBuilder.append(peopleEventText.getId()).append(",");
                }
            }
            if(stringBuilder.length()>0){
                stringBuilder.deleteCharAt(stringBuilder.length()-1);
            }
            event_ids = stringBuilder.toString();
        }
        return event_ids;
    }

    /**
     *更新草稿
     * @param intent 更新草稿成功后的意图
     */
    private void updateDraft(final Intent intent, final int requestCode) {
        showLoadingDialog();
        //TODO 替换用户名
        String username = "13167231015";
        String name = et_celebrity_name.getText().toString();
        String nationality = et_celebrity_nationality.getText().toString();
        String birthplace = et_birth_place.getText().toString();
        String residence = et_residence.getText().toString();
        String grave_place = et_long_sleep_place.getText().toString();
        String birth_day = et_birthday.getText().toString();
        String death_day = et_death_day.getText().toString();
        String motto = et_motto.getText().toString();
        String industry = et_industry.getText().toString();
        String cover_url = this.cover_url;
        String time_stamp = Tools.getTimeStamp();
        final String draft_people_id = this.draft_people_id;
        int alive = this.alive?0:1;
        String event_ids = getEventIds();
        String description_id = UploadCelebrityActivity.this.people_description_id;
        aboutPeopleService.updateDraftPeople(username,name,nationality,birthplace,residence,grave_place,birth_day,death_day,motto,industry,cover_url,time_stamp,draft_people_id,alive,event_ids,description_id)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
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
                                }else{
                                    finish();
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
        //TODO 替换用户名
        String username = "13167231015";
        String name = et_celebrity_name.getText().toString();
        String nationality = et_celebrity_nationality.getText().toString();
        String birthplace = et_birth_place.getText().toString();
        String residence = et_residence.getText().toString();
        String grave_place = et_long_sleep_place.getText().toString();
        String birth_day = et_birthday.getText().toString();
        String death_day = et_death_day.getText().toString();
        String motto = et_motto.getText().toString();
        String industry = et_industry.getText().toString();
        String cover_url = this.cover_url;
        String time_stamp = Tools.getTimeStamp();
        int alive = this.alive?0:1;
        aboutPeopleService.uploadPeople(username,name,nationality,birthplace,residence,grave_place,birth_day,death_day,motto,industry,cover_url,time_stamp,draft_people_id,alive)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonUploadPeopleResultBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(UploadCelebrityActivity.this,"服务器错误",Toast.LENGTH_SHORT).show();
                        hideLoadingDialog();
                    }

                    @Override
                    public void onNext(GsonUploadPeopleResultBean gsonUploadPeopleResultBean) {
                        hideLoadingDialog();
                        switch (gsonUploadPeopleResultBean.getCode()){
                            case 0:
                                Toast.makeText(UploadCelebrityActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
                                finish();
                                break;
                            default:
                                Toast.makeText(UploadCelebrityActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
    }
    /**
     * 更新
     */
    private void updatePeople(){
        showLoadingDialog();
        //TODO 替换用户名
        String people_id = getIntent().getStringExtra(Constant.INTENT_PEOPLE_ID);
        String username = "13167231015";
        String name = et_celebrity_name.getText().toString();
        String nationality = et_celebrity_nationality.getText().toString();
        String birthplace = et_birth_place.getText().toString();
        String residence = et_residence.getText().toString();
        String grave_place = et_long_sleep_place.getText().toString();
        String birth_day = et_birthday.getText().toString();
        String death_day = et_death_day.getText().toString();
        String motto = et_motto.getText().toString();
        String industry = et_industry.getText().toString();
        String cover_url = this.cover_url;
        String time_stamp = Tools.getTimeStamp();
        int alive = this.alive?0:1;
        aboutPeopleService.updatePeople(people_id,username,name,nationality,birthplace,residence,grave_place,birth_day,death_day,motto,industry,cover_url,time_stamp,alive)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonUploadPeopleResultBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(UploadCelebrityActivity.this,"服务器错误",Toast.LENGTH_SHORT).show();
                        hideLoadingDialog();
                    }

                    @Override
                    public void onNext(GsonUploadPeopleResultBean gsonUploadPeopleResultBean) {
                        hideLoadingDialog();
                        switch (gsonUploadPeopleResultBean.getCode()){
                            case 0:
                                Toast.makeText(UploadCelebrityActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                                finish();
                                break;
                            default:
                                Toast.makeText(UploadCelebrityActivity.this,"更新失败",Toast.LENGTH_SHORT).show();
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
                            if(!isCreatedDraft) {
                                saveDraft(null, 0);
                            }else{
                                updateDraft(null,0);
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

    @Override
    public void onBackPressed() {
        if(Constant.PEOPLE_STATUS_EDIT.equals(stauts)) {
            //编辑提交的人物状态，点close，直接退出
            finish();
        }else{
            //编辑人物草稿状态或者是添加新人物状态，点close，弹出保存草稿的弹出框
            showSaveDraftDialog();
        }
    }

    interface AboutPeopleService{
        @POST("people/commit")
        Observable<GsonUploadPeopleResultBean> uploadPeople(@Query("username")String uploader,@Query("name")String name,
                                                            @Query("nationality")String nationality,@Query("birthplace")String birthplace,
                                                            @Query("residence")String residence,@Query("grave_place")String grave_place,
                                                            @Query("birth_day")String birth_day,@Query("death_day")String death_day,
                                                            @Query("motto")String motto,@Query("industry")String industry,
                                                            @Query("cover_url")String cover_url,@Query("time_stamp")String time_stamp,
                                                            @Query("draft_people_id")String draft_people_id,@Query("alive")int alive);

        @POST("people/update")
        Observable<GsonUploadPeopleResultBean> updatePeople(@Query("people_id")String people_id,
                                                            @Query("username")String uploader,@Query("name")String name,
                                                            @Query("nationality")String nationality,@Query("birthplace")String birthplace,
                                                            @Query("residence")String residence,@Query("grave_place")String grave_place,
                                                            @Query("birth_day")String birth_day,@Query("death_day")String death_day,
                                                            @Query("motto")String motto,@Query("industry")String industry,
                                                            @Query("cover_url")String cover_url,@Query("time_stamp")String time_stamp,
                                                            @Query("alive")int alive);

        @POST("people/get")
        Observable<GsonGetPeopleResultBean> getPeopleInfo(@Query("people_id")String people_id);

        @POST("draft/people/get")
        Observable<GsonGetDraftPeopleResultBean> getDraftPeopleInfo(@Query("draft_people_id")String draft_people_id);

        @POST("draft/people/commit")
        Observable<GsonSaveDraftPeopleResultBean> saveDraftPeople(@Query("username")String uploader,@Query("name")String name,
                                                                  @Query("nationality")String nationality,@Query("birthplace")String birthplace,
                                                                  @Query("residence")String residence,@Query("grave_place")String grave_place,
                                                                  @Query("birth_day")String birth_day,@Query("death_day")String death_day,
                                                                  @Query("motto")String motto,@Query("industry")String industry,
                                                                  @Query("cover_url")String cover_url,@Query("time_stamp")String time_stamp,
                                                                  @Query("alive")int alive,@Query("event_ids")String event_ids,
                                                                  @Query("description_id")String description_id);
        @POST("draft/people/update")
        Observable<GsonUpdateDraftPeopleResultBean> updateDraftPeople(@Query("username")String uploader,@Query("name")String name,
                                                                      @Query("nationality")String nationality,@Query("birthplace")String birthplace,
                                                                      @Query("residence")String residence,@Query("grave_place")String grave_place,
                                                                      @Query("birth_day")String birth_day,@Query("death_day")String death_day,
                                                                      @Query("motto")String motto,@Query("industry")String industry,
                                                                      @Query("cover_url")String cover_url,@Query("time_stamp")String time_stamp,
                                                                      @Query("draft_people_id")String draft_people_id,
                                                                      @Query("alive")int alive,@Query("event_ids")String event_ids,
                                                                      @Query("description_id")String description_id);
    }
}


