package com.testography.devintensive.data.network;

import com.testography.devintensive.data.network.req.UserLoginReq;
import com.testography.devintensive.data.network.res.UserModelRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RestService {

    @POST("login")
    Call<UserModelRes> loginUser(@Body UserLoginReq req);
}
