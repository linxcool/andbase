package com.linxcool.andbase.retrofit;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;

/**
 * Created by huchanghai on 2016/9/11.
 */
public class ReplyCallAdapter<R> implements CallAdapter<R, ReplyCall<?>> {

    private final Type responseType;

    ReplyCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public ReplyCall<?> adapt(Call<R> call) {
        return new ReplyCall<>(call);
    }
}
