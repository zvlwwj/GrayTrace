package com.zou.graytrace.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.zou.graytrace.R;
import com.zou.graytrace.Utils.Constant;
import com.zou.graytrace.application.GrayTraceApplication;
import com.zou.graytrace.bean.GsonGetCreationPeopleSampleResultBean;
import com.zou.graytrace.retrofitInterface.GetCreationPeopleSample;

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
        getCreationPeopleSample.getCreationPeopleSample(user_id+"")
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GsonGetCreationPeopleSampleResultBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(GsonGetCreationPeopleSampleResultBean gsonGetCreationPeopleSampleResultBean) {
                        switch (gsonGetCreationPeopleSampleResultBean.getCode()){
                            case 0:
                                Log.i("getCreationPeopleSample",gsonGetCreationPeopleSampleResultBean.getMsg());
                                break;
                            default:
                                Log.i("getCreationPeopleSample",gsonGetCreationPeopleSampleResultBean.getMsg());
                                break;
                        }
                    }
                });
    }

    private void initView() {

    }

}
