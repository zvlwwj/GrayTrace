package com.zou.graytrace.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zou.graytrace.R;
import com.zou.graytrace.Utils.Constant;
import com.zou.graytrace.application.GrayTraceApplication;
import com.zou.graytrace.bean.GsonGetPeopleResultBean;
import com.zou.graytrace.view.RichTextViewContainer;
import com.zou.graytrace.view.TextViewPeopleContainer;

import java.util.ArrayList;

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
 * Created by zou on 2018/2/22.
 */
//TODO 底部cardView显示的时候会遮住显示部分
public class PeopleActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_people)
    Toolbar toolbar_people;
    @BindView(R.id.iv_people_cover)
    ImageView iv_people_cover;
    @BindView(R.id.tv_people_name)
    TextView tv_people_name;
    @BindView(R.id.tv_people_description)
    RichTextViewContainer tv_people_description;
    @BindView(R.id.textViewPeopleContainer)
    TextViewPeopleContainer textViewPeopleContainer;
    @BindView(R.id.ll_reputation)
    LinearLayout ll_reputation;
    @BindView(R.id.ll_chat)
    LinearLayout ll_chat;
    @BindView(R.id.ll_collection)
    LinearLayout ll_collection;
    private String from;
    private String people_id;
    private GetPeopleInfoService getPeopleInfoService;
    private GrayTraceApplication app;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);
        ButterKnife.bind(this);
        initData();
        initView();
        getPeopleInfo();
    }



    private void initData() {
        app = (GrayTraceApplication) getApplication();
        Retrofit retrofit = app.getRetrofit();
        getPeopleInfoService = retrofit.create(GetPeopleInfoService.class);
        from = getIntent().getStringExtra(Constant.INTENT_PEOPLE_FROM);
        people_id = getIntent().getStringExtra(Constant.INTENT_PEOPLE_ID);
    }

    private void initView() {
        setSupportActionBar(toolbar_people);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //点赞
    @OnClick(R.id.ll_reputation)
    public void reputation(){

    }

    //跳转到留言板
    @OnClick(R.id.ll_chat)
    public void chat(){
        Intent intent = new Intent(this,CommentActivity.class);
        intent.putExtra(Constant.INTENT_COMMENT_TYPE,Constant.COMMENT_TYPE_PEOPLE);
        intent.putExtra(Constant.INTENT_COMMENT_TYPE_ID,Integer.valueOf(people_id));
        startActivity(intent);
    }

    @OnClick(R.id.ll_collection)
    public void collection(){

    }

    private void getPeopleInfo() {
        getPeopleInfoService.getPeopleInfo(people_id).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<GsonGetPeopleResultBean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(PeopleActivity.this,"服务器错误",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onNext(GsonGetPeopleResultBean gsonGetPeopleResultBean) {
                switch (gsonGetPeopleResultBean.getCode()){
                    case 0:
                        //封面
                        String cover_url = gsonGetPeopleResultBean.getInfo().getCover_url();
                        if(cover_url!=null){
                            Glide.with(PeopleActivity.this).load(cover_url).into(iv_people_cover);
                        }
                        //alive=0表示活着，alive=1表示死了 TODO 决定图片动画
                        int alive = gsonGetPeopleResultBean.getInfo().getAlive();
                        //姓名
                        String name = gsonGetPeopleResultBean.getInfo().getName();
                        if(name!=null){
                            tv_people_name.setText(name);
                        }
                        //生日
                        String birthDay = gsonGetPeopleResultBean.getInfo().getBirth_day();
                        //祭日
                        String deathDay = gsonGetPeopleResultBean.getInfo().getDeath_day();
                        //国籍
                        String nationality = gsonGetPeopleResultBean.getInfo().getNationality();
                        //出生地
                        String birthPlace = gsonGetPeopleResultBean.getInfo().getBirthplace();
                        //长眠地
                        String longSleepPlace = gsonGetPeopleResultBean.getInfo().getGrave_place();
                        //居住地
                        String residence = gsonGetPeopleResultBean.getInfo().getResidence();
                        //行业
                        String industry = gsonGetPeopleResultBean.getInfo().getIndustry();
                        //一句话描述
                        String motto = gsonGetPeopleResultBean.getInfo().getMotto();
                        //描述
                        String people_description_id = gsonGetPeopleResultBean.getInfo().getDescription().getDescription_id();
                        if(people_description_id != null){
                            String description_text = gsonGetPeopleResultBean.getInfo().getDescription().getDescription_text();
//                            tv_people_description.setText(description_text);
                            tv_people_description.setSpanString(description_text);
                            tv_people_description.setOnSelectMediaListener(new RichTextViewContainer.OnSelectMediaListener() {
                                @Override
                                public void OnSelected(int type, String url) {
                                    Toast.makeText(PeopleActivity.this,"type: "+type+" url: "+url,Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        //事件
                        ArrayList<GsonGetPeopleResultBean.Info.Event> events = gsonGetPeopleResultBean.getInfo().getEvents();
                        if(events!=null&&events.size()>0){
                            for(GsonGetPeopleResultBean.Info.Event event : events){
                                textViewPeopleContainer.addEvent(event.getEvent_title(),event.getEvent_text());
                            }
                        }
                        break;
                    default:
                        Toast.makeText(PeopleActivity.this,"获取人物信息失败",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_people,menu);
        MenuItem editItem = menu.findItem(R.id.action_menu_people_edit);
        switch (from){
            case Constant.PEOPLE_FROM_CREATION:
                editItem.setVisible(true);
                break;
            case Constant.PEOPLE_FROM_MAIN:
                editItem.setVisible(false);
                break;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_menu_people_edit:
                if(from.equals(Constant.PEOPLE_FROM_CREATION)){
                    Intent intent = new Intent(PeopleActivity.this, UploadCelebrityActivity.class);
                    intent.putExtra(Constant.INTENT_PEOPLE_STATUS,Constant.PEOPLE_STATUS_EDIT);
                    intent.putExtra(Constant.INTENT_PEOPLE_ID,people_id);
                    startActivity(intent);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    interface GetPeopleInfoService{
        @POST("people/get")
        Observable<GsonGetPeopleResultBean> getPeopleInfo(@Query("people_id")String people_id);
    }
}
