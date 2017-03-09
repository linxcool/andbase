package com.linxcool.andbase.mvp;

import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

/**
 * Created by huchanghai on 2016/10/24.
 */
public class DataParamsInterceptor implements Interceptor {

    private static boolean logable;

    public static void setLogable(boolean logable) {
        DataParamsInterceptor.logable = logable;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request oldRequest = chain.request();

        // 添加新的参数
        HttpUrl.Builder authorizedUrlBuilder = oldRequest.url()
                .newBuilder()
                .scheme(oldRequest.url().scheme())
                .host(oldRequest.url().host());

        // 新的请求
        Request newRequest = oldRequest.newBuilder()
                .method(oldRequest.method(), oldRequest.body())
                .url(authorizedUrlBuilder.build())
                .build();

        Response response = chain.proceed(newRequest);

        if(logable) {
            printLog(newRequest, response);
        }

        return response;
    }

    private void printLog(Request request, Response response) throws IOException {
        ResponseBody body = response.body();
        
        BufferedSource source = body.source();
        source.request(Long.MAX_VALUE);
        okio.Buffer buffer = source.buffer();
        Charset charset = Charset.defaultCharset();
        MediaType contentType = body.contentType();
        if (contentType != null) {
            charset = contentType.charset(charset);
        }

        Log.d("", "request  -> " + request.url() + "\nresponse -> " + buffer.clone().readString(charset));
    }

}
