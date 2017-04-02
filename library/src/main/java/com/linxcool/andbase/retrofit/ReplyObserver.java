package com.linxcool.andbase.retrofit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static android.R.attr.data;

/**
 * Created by linxcool on 17/4/2.
 */

public abstract class ReplyObserver<T> implements Observer<Reply<T>> {

    private int CODE_SUC;
    Disposable disposable;
    boolean onlyOnce;

    public ReplyObserver() {
        this(0);
    }

    public ReplyObserver(int sucCode) {
        this(sucCode, true);
    }

    public ReplyObserver(int sucCode, boolean onlyOnce) {
        this.CODE_SUC = sucCode;
        this.onlyOnce = onlyOnce;
    }

    @Override
    public void onSubscribe(Disposable d) {
        this.disposable = d;
    }

    @Override
    public void onNext(Reply<T> value) {
        if (value == null) {
            onError(new Exception("server return null data"));
        } else if (value.getCode() == CODE_SUC) {
            onSuccess(value.getData());
        } else {
            onFailure(value.getCode(), value.getErrMsg());
        }
    }


    @Override
    public void onError(Throwable e) {
        unregistSelf();
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        unregistSelf();
    }

    private void unregistSelf() {
        if (onlyOnce) {
            disposable.dispose();
        }
    }

    public abstract void onSuccess(T t);

    public abstract void onFailure(int code, String msg);
}
