package com.zou.graytrace.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatDelegate;

import com.zou.graytrace.Utils.URL;
import com.zou.graytrace.bean.GsonDeleteFileResultBean;
import com.zou.graytrace.bean.GsonLoginBean;
import com.zou.graytrace.bean.GsonUploadFileResultBean;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by zou on 2018/1/29.
 */

/**
 * TODO 1.所有toast都要换
 */
public class GrayTraceApplication extends Application {
    private Retrofit retrofit;
    private SharedPreferences accountSharedPreferences;
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

    /**
     * 获取用户相关的SharedPreferences
     * @return
     */
    public SharedPreferences getAccountSharedPreferences(){
        if(accountSharedPreferences==null){
            accountSharedPreferences = getSharedPreferences("account",MODE_PRIVATE);
        }
        return accountSharedPreferences;
    }

    /**
     * 可以共用的接口
     */
    public interface PublicService{
        @Multipart
        @POST("uploadFile")
        Observable<GsonUploadFileResultBean> uploadFile(@Part List<MultipartBody.Part> partList);
        @POST("deleteFile")
        Observable<GsonDeleteFileResultBean> deleteFile(@Query("url") String url);
        @POST("login")
        Observable<GsonLoginBean> toLogin(@Query("username")String username, @Query("password") String pwd);
    }
}
