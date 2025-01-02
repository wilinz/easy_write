package com.eazywrite.app.util;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ResponseInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response originalResponse = chain.proceed(request);
        HttpUrl httpUrl = request.url();
        String urlString = httpUrl.toString();
//        Log.d("ResponseInterceptor", "response from url:" + urlString);
        //okhttp3请求回调中response.body().string()只能有效调用一次
//        if (originalResponse.body()!=null)
//            Log.d("ResponseInterceptor", "Response Body:" + originalResponse.body().string());
        return originalResponse;
    }
}
