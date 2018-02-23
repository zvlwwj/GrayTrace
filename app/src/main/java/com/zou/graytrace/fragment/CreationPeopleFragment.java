package com.zou.graytrace.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.zou.graytrace.R;
import com.zou.graytrace.Utils.Constant;
import com.zou.graytrace.activity.PeopleActivity;
import com.zou.graytrace.activity.UploadCelebrityActivity;
import com.zou.graytrace.adapter.PeopleRecyclerAdapter;
import com.zou.graytrace.application.GrayTraceApplication;
import com.zou.graytrace.bean.GsonGetCreationPeopleSampleResultBean;
import com.zou.graytrace.bean.ItemPeopleSample;

import java.util.ArrayList;

import butterknife.BindView;
import retrofit2.Retrofit;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zou on 2018/2/8.
 */

public class CreationPeopleFragment extends BaseFragment{
    private static CreationPeopleFragment instance;
    @BindView(R.id.recyclerView_creation_people)
    RecyclerView recyclerView;
    private GetCreationPeopleSample getCreationPeopleSample;
    private SharedPreferences sharedPreferences;
    private GrayTraceApplication application;
    private ArrayList<ItemPeopleSample> itemCreationPeoples;
    private PeopleRecyclerAdapter adapter;
    public static synchronized CreationPeopleFragment getInstance() {
        if (instance == null) {
            instance = new CreationPeopleFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent(R.layout.fragment_creation_people);
        application = (GrayTraceApplication) getActivity().getApplication();
        Retrofit retrofit = application.getRetrofit();
        getCreationPeopleSample = retrofit.create(GetCreationPeopleSample.class);
    }

    @Override
    protected void onBaseCreateView() {
        super.onBaseCreateView();
        initData();
        initView();
        setListener();
    }



    private void initData() {
        sharedPreferences = application.getAccountSharedPreferences();
        int user_id = sharedPreferences.getInt(Constant.SP_USER_ID,0);
        getList(user_id);
    }

    private void initView() {
        itemCreationPeoples = new ArrayList<>();
        adapter = new PeopleRecyclerAdapter(getContext(),itemCreationPeoples);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
    private void setListener() {
        adapter.setOnItemClickListener(new PeopleRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ItemPeopleSample itemPeopleSample = itemCreationPeoples.get(position);
                String people_id = itemPeopleSample.getPeople_id();
                Intent intent = new Intent(getActivity(), PeopleActivity.class);
                intent.putExtra(Constant.INTENT_PEOPLE_FROM,Constant.PEOPLE_FROM_CREATION);
                intent.putExtra(Constant.INTENT_PEOPLE_ID,people_id);
                startActivity(intent);
            }
        });
    }

    private void getList(int user_id) {
        getCreationPeopleSample.getCreationPeopleSample(user_id+"")
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonGetCreationPeopleSampleResultBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getContext(),"服务器错误",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(GsonGetCreationPeopleSampleResultBean gsonGetCreationPeopleSampleResultBean) {
                        switch (gsonGetCreationPeopleSampleResultBean.getCode()){
                            case 0:
                                if(gsonGetCreationPeopleSampleResultBean.getInfos()!=null&&gsonGetCreationPeopleSampleResultBean.getInfos().size()>0) {
                                    itemCreationPeoples.clear();
                                    for (GsonGetCreationPeopleSampleResultBean.Info info : gsonGetCreationPeopleSampleResultBean.getInfos()) {
                                        ItemPeopleSample itemCreationPeople = new ItemPeopleSample();
                                        itemCreationPeople.setTitle(info.getName());
                                        itemCreationPeople.setContent(info.getDescriptionText());
                                        itemCreationPeople.setCover_url(info.getCoverUrl());
                                        itemCreationPeople.setPeople_id(info.getPeople_id());
                                        itemCreationPeoples.add(itemCreationPeople);
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                                break;
                            default:
                                Toast.makeText(getContext(),"获取数据失败",Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
    }



    interface GetCreationPeopleSample {
        @POST("people/get/creation_sample")
        Observable<GsonGetCreationPeopleSampleResultBean> getCreationPeopleSample(@Query("user_id") String user_id);
    }

}
