package com.zou.graytrace.retrofitInterface;

import com.zou.graytrace.bean.GsonGetCreationPeopleSampleResultBean;

import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by zou on 2018/2/8.
 */

public interface GetCreationPeopleSample {
    @POST("people/get/creation_sample")
    Observable<GsonGetCreationPeopleSampleResultBean> getCreationPeopleSample(@Query("user_id") String user_id);
}
