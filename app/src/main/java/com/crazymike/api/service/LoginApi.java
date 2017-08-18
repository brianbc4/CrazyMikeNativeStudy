package com.crazymike.api.service;

import com.crazymike.api.response.LoginResponse;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Elliot on 2017/5/17.
 */

public interface LoginApi {
    @GET("https://openapi.devel.crazymike.tw/?func=appSdkLogin")
    Observable<LoginResponse> login(
            @Query("user") String user,
            @Query("token") String token,
            @Query("c_type") String cType,
            @Query("time") int time,
            @Query("sig") String sig);
}
