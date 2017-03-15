package com.linxcool.andbase.retrofit;
import android.os.Handler;

import com.linxcool.andbase.util.LogUtil;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by linxcool on 16/8/30.
 */
public abstract class ReplyCallback<T> implements Callback<Reply<T>> {

    public static final int CODE_NET_ERROR = 601;
    public static final int CODE_SYS_ERROR = 602;

    private Handler handler;
    private String cacheName;

    public String getCacheName() {
        return cacheName;
    }

    public ReplyCallback() {
        this(true);
    }

    public ReplyCallback(String cacheName) {
        this(true);
        this.cacheName = cacheName;
    }

    public ReplyCallback(boolean withHandler) {
        if (withHandler) handler = new Handler();
    }

    @Override
    public void onResponse(Call<Reply<T>> call, final Response<Reply<T>> response) {
        if (handler == null) processResponse(response);
        else handler.post(new Runnable() {
            @Override
            public void run() {
                processResponse(response);
            }
        });
    }

    @Override
    public void onFailure(Call<Reply<T>> call, Throwable t) {
        if (t instanceof ConnectException || t instanceof SocketTimeoutException) {
            processFailure(CODE_NET_ERROR, t.getMessage());
        } else {
            processFailure(CODE_SYS_ERROR, t.getMessage());
        }
        t.printStackTrace();
    }

    private void processResponse(Response<Reply<T>> response) {
        Reply<T> reply = response.body();
        if (reply == null) {
            processFailure(response.code(), response.message());
        } else if (reply.isOk()) {
            processSuccess(reply.getData());
        } else {
            processFailure(reply.getCode(), reply.getErrMsg());
        }
    }

    private void processSuccess(T t) {
        onSuccess(t);
    }

    private void processFailure(int code, String msg) {
        LogUtil.eFormat("net callback failure(%d | %s)", code, msg);
        onFailure(code, msg);
    }

    public abstract void onSuccess(T t);

    public void onFailure(int code, String msg) {
    }

    public void tryAgain() {
        if (handler == null) onRetry();
        else handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onRetry();
            }
        }, 3000);
    }

    public void onRetry() {
    }

}
