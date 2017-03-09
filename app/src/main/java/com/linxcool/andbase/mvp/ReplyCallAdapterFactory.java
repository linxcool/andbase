package com.linxcool.andbase.mvp;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * Created by huchanghai on 2016/9/11.
 */
public class ReplyCallAdapterFactory extends CallAdapter.Factory {

    @Override
    public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        Class<?> rawType = getRawType(returnType);
        if (rawType == ReplyCall.class && returnType instanceof ParameterizedType) {
            Type callReturnType = getParameterUpperBound(0, (ParameterizedType) returnType);
            return new ReplyCallAdapter(callReturnType);
        }
        return null;
    }

    public static ReplyCallAdapterFactory create() {
        return new ReplyCallAdapterFactory();
    }

}
