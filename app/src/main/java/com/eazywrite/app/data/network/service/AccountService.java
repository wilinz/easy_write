package com.eazywrite.app.data.network.service;

import com.eazywrite.app.data.model.RegisterResponse;
import com.eazywrite.app.data.model.Transcriptions;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface AccountService {

    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("account/verify")
    Call<RegisterResponse> postVerify(@Body RequestBody body);

    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("account/register")
    Call<RegisterResponse> postSignUp(@Body RequestBody body);

    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("account/login")
    Call<RegisterResponse> postLogin(@Body RequestBody body);

    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @PUT("account/password/reset")
    Call<RegisterResponse> postReset(@Body RequestBody body);

    @Headers({"Content-Type: multipart/form-data; boundary=WebAppBoundary"})//需要添加头
    @POST ("feedback")
    Call<RegisterResponse> feedback(@Body MultipartBody multipartBody);


}
