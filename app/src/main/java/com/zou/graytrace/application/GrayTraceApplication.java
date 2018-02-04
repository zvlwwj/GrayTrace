package com.zou.graytrace.application;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.zou.graytrace.Utils.URL;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zou on 2018/1/29.
 */

public class GrayTraceApplication extends Application {
    private Retrofit retrofit;
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    public Retrofit getRetrofit(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .baseUrl(URL.BASE_URL)
                    .build();
        }
        return retrofit;
    }
}
