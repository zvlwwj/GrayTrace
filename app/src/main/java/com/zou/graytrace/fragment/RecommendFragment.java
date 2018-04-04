package com.zou.graytrace.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zou.graytrace.R;
import com.zou.graytrace.Utils.Constant;
import com.zou.graytrace.activity.PeopleActivity;
import com.zou.graytrace.adapter.MainRecyclerAdapter;
import com.zou.graytrace.application.GrayTraceApplication;
import com.zou.graytrace.bean.GsonGetCreationPeopleSampleResultBean;
import com.zou.graytrace.bean.GsonMainItem;
import com.zou.graytrace.bean.ItemPeopleSample;
import com.zou.graytrace.bean.ItemSampleBean;

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
 * Created by zou on 2018/1/17.
 */

public class RecommendFragment extends BaseFragment {
    private static RecommendFragment instance;
    @BindView(R.id.recyclerView_recommend)
    RecyclerView recyclerView_recommend;
    private ArrayList<ItemSampleBean> itemSampleBeans;
    private MainRecyclerAdapter adapter;
    private GetRecommendListService getRecommendListService;
    private GrayTraceApplication application;
    private SharedPreferences sharedPreferences;
    public static synchronized RecommendFragment getInstance() {
        if (instance == null) {
            instance = new RecommendFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent(R.layout.fragment_home_recommend);
        application = (GrayTraceApplication) getActivity().getApplication();
        Retrofit retrofit = application.getRetrofit();
        getRecommendListService = retrofit.create(GetRecommendListService.class);
    }

    @Override
    protected void onBaseCreateView() {
        super.onBaseCreateView();
        getRecommendList();
        initView();
        setListener();
    }

    private void getRecommendList() {
        sharedPreferences = application.getAccountSharedPreferences();
        int user_id = sharedPreferences.getInt(Constant.SP_USER_ID,0);
        getRecommendListService.getRecommendList(user_id+"")
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonMainItem>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getContext(),"服务器错误",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(GsonMainItem gsonMainItem) {
                        switch (gsonMainItem.getCode()){
                            case 0:
                                if(gsonMainItem.getInfos()!=null&&gsonMainItem.getInfos().size()>0) {
                                    itemSampleBeans.clear();
                                    for (GsonMainItem.Info info : gsonMainItem.getInfos()) {
                                        ItemSampleBean itemSampleBean = new ItemSampleBean();
                                        itemSampleBean.setTitle(info.getName());
                                        itemSampleBean.setContent(info.getDescriptionText());
                                        itemSampleBean.setCover_url(info.getCoverUrl());
                                        itemSampleBean.setId(info.getId());
                                        itemSampleBean.setType(info.getType());
                                        itemSampleBeans.add(itemSampleBean);
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

    private void setListener() {
        adapter.setOnItemClickListener(new MainRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ItemSampleBean itemSampleBean = itemSampleBeans.get(position);
                String id = itemSampleBean.getId();
                String type = itemSampleBean.getType();
                if(Constant.COMMENT_TYPE_PEOPLE.equals(type)) {
                    Intent intent = new Intent(getActivity(), PeopleActivity.class);
                    intent.putExtra(Constant.INTENT_PEOPLE_FROM, Constant.PEOPLE_FROM_MAIN);
                    intent.putExtra(Constant.INTENT_PEOPLE_ID, id);
                    startActivity(intent);
                }
            }
        });
    }

    private void initView() {
        itemSampleBeans = new ArrayList<>();
        adapter = new MainRecyclerAdapter(getActivity(),itemSampleBeans);
        recyclerView_recommend.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView_recommend.setAdapter(adapter);
    }

    public interface GetRecommendListService{
        @POST("homepage/recommend/getlist")
        Observable<GsonMainItem> getRecommendList(@Query("user_id")String user_id);
    }
}
