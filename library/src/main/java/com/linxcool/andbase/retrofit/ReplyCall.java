package com.linxcool.andbase.retrofit;

import android.text.TextUtils;

import java.io.IOException;
import java.io.Serializable;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by huchanghai on 2016/9/11.
 */
public class ReplyCall<T> implements Callback<T>, Call<T> {

    private final Call<T> call;
    private Callback<T> callback;
    private boolean notifyed;
    private String cache;

    public ReplyCall(Call<T> call) {
        this.call = call;
    }

    @Override
    public Response<T> execute() throws IOException {
        return call.execute();
    }

    @Override
    public void enqueue(Callback<T> callback) {
        this.callback = callback;
        this.notifyed = false;
        this.cache = checkCache(callback);

        if (!TextUtils.isEmpty(cache)) {
            loadCache(cache);
        }

        call.enqueue(this);
    }

    String checkCache(Callback<T> callback) {
        return null;
    }

    void loadCache(String cache) {
    }

    void saveCache(String cache, Serializable data) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.body() instanceof Reply) {
			Reply<T> reply = (Reply<T>) response.body();
            if (reply.isOk() && !TextUtils.isEmpty(cache)) {
                saveCache(cache, (Serializable) reply.getData());
            }
        }

        if (!notifyed) {
            callback.onResponse(call, response);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (!notifyed) {
            callback.onFailure(call, t);
        }
    }

    @Override
    public boolean isExecuted() {
        return call.isExecuted();
    }

    @Override
    public void cancel() {
        call.cancel();
    }

    @Override
    public boolean isCanceled() {
        return call.isCanceled();
    }

    @Override
    public Call<T> clone() {
        return null;
    }

    @Override
    public Request request() {
        return call.request();
    }

}