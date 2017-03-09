package com.linxcool.andbase.mvp;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;

/**
 * Created by huchanghai on 2016/9/11.
 */
public class ReplyCallAdapter implements CallAdapter<ReplyCall<?>> {

    private final Type responseType;

    ReplyCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public <T> ReplyCall<?> adapt(Call<T> call) {
        return new ReplyCall<>(call);
    }

}
