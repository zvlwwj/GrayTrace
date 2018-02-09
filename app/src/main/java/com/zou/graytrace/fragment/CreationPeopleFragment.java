package com.zou.graytrace.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.zou.graytrace.R;
import com.zou.graytrace.Utils.Constant;
import com.zou.graytrace.adapter.CreationPeopleRecyclerAdapter;
import com.zou.graytrace.application.GrayTraceApplication;
import com.zou.graytrace.bean.GsonGetCreationPeopleSampleResultBean;
import com.zou.graytrace.bean.ItemCreationPeople;
import com.zou.graytrace.retrofitInterface.GetCreationPeopleSample;

import java.util.ArrayList;

import butterknife.BindView;
import retrofit2.Retrofit;
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
    private ArrayList<ItemCreationPeople> itemCreationPeoples;
    private CreationPeopleRecyclerAdapter adapter;
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
    }

    private void initData() {
        sharedPreferences = application.getAccountSharedPreferences();
        int user_id = sharedPreferences.getInt(Constant.SP_USER_ID,0);
        getList(user_id);
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
                                itemCreationPeoples.clear();
                                for(GsonGetCreationPeopleSampleResultBean.Info info :gsonGetCreationPeopleSampleResultBean.getInfos()){
                                    ItemCreationPeople itemCreationPeople = new ItemCreationPeople();
                                    itemCreationPeople.setTitle(info.getName());
                                    itemCreationPeople.setContent(info.getDescriptionText());
                                    itemCreationPeople.setCover_url(info.getCoverUrl());
                                    itemCreationPeoples.add(itemCreationPeople);
                                }
                                adapter.notifyDataSetChanged();
                                break;
                            default:
                                Toast.makeText(getContext(),"获取数据失败",Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
    }

    private void initView() {
        itemCreationPeoples = new ArrayList<>();
        adapter = new CreationPeopleRecyclerAdapter(getContext(),itemCreationPeoples);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

}
