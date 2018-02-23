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
import com.zou.graytrace.activity.UploadCelebrityActivity;
import com.zou.graytrace.adapter.DraftPeopleRecyclerAdapter;
import com.zou.graytrace.adapter.PeopleRecyclerAdapter;
import com.zou.graytrace.application.GrayTraceApplication;
import com.zou.graytrace.bean.GsonGetDraftPeopleSampleResultBean;
import com.zou.graytrace.bean.ItemPeopleDraftSample;
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

public class DraftPeopleFragment extends BaseFragment {
    private static DraftPeopleFragment instance;
    @BindView(R.id.recyclerView_draft_people)
    RecyclerView recyclerView_draft_people;
    private GrayTraceApplication application;
    private GetDraftPeopleSample getDraftPeopleSample;
    private SharedPreferences sharedPreferences;
    private ArrayList<ItemPeopleDraftSample> itemDraftPeoples;
    private DraftPeopleRecyclerAdapter adapter;

    public static synchronized DraftPeopleFragment getInstance() {
        if (instance == null) {
            instance = new DraftPeopleFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent(R.layout.fragment_draft_people);
        application = (GrayTraceApplication) getActivity().getApplication();
        Retrofit retrofit = application.getRetrofit();
        getDraftPeopleSample = retrofit.create(GetDraftPeopleSample.class);
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
        itemDraftPeoples = new ArrayList<>();
        adapter = new DraftPeopleRecyclerAdapter(getContext(),itemDraftPeoples);
        recyclerView_draft_people.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView_draft_people.setAdapter(adapter);
    }

    private void setListener() {
        adapter.setOnItemClickListener(new DraftPeopleRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ItemPeopleDraftSample itemPeopleDraftSample = itemDraftPeoples.get(position);
                String draft_people_id = itemPeopleDraftSample.getDraft_people_id();
                Intent intent = new Intent(getActivity(), UploadCelebrityActivity.class);
                intent.putExtra(Constant.INTENT_PEOPLE_STATUS,Constant.PEOPLE_STATUS_EDIT_DRAFT);
                intent.putExtra(Constant.INTENT_DRAFT_PEOPLE_ID,draft_people_id);
                startActivity(intent);
            }
        });
    }

    private void getList(int user_id) {
        getDraftPeopleSample.getDraftPeopleSample(""+user_id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonGetDraftPeopleSampleResultBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getContext(),"服务器错误",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(GsonGetDraftPeopleSampleResultBean gsonGetDraftPeopleSampleResultBean) {
                        switch (gsonGetDraftPeopleSampleResultBean.getCode()){
                            case 0:
                                if(gsonGetDraftPeopleSampleResultBean.getInfos()!=null&&gsonGetDraftPeopleSampleResultBean.getInfos().size()>0) {
                                    itemDraftPeoples.clear();
                                    for (GsonGetDraftPeopleSampleResultBean.Info info : gsonGetDraftPeopleSampleResultBean.getInfos()) {
                                        ItemPeopleDraftSample itemPeopleDraftSample = new ItemPeopleDraftSample();
                                        itemPeopleDraftSample.setTitle(info.getName());
                                        itemPeopleDraftSample.setContent(info.getDescriptionText());
                                        itemPeopleDraftSample.setCover_url(info.getCoverUrl());
                                        itemPeopleDraftSample.setDraft_people_id(info.getDraft_people_id());
                                        itemDraftPeoples.add(itemPeopleDraftSample);
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                                break;
                            default:
                                Toast.makeText(getContext(),"获取草稿失败",Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
    }

    interface GetDraftPeopleSample{
        @POST("people/get/draft_sample")
        Observable<GsonGetDraftPeopleSampleResultBean> getDraftPeopleSample(@Query("user_id") String user_id);
    }
}
