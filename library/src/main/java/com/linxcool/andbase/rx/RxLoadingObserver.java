package com.linxcool.andbase.rx;

import android.app.Activity;
import android.app.ProgressDialog;

import com.linxcool.andbase.util.SystemUtil;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by huchanghai on 2017/3/14.
 */
public abstract class RxLoadingObserver<T> implements Observer<T> {

    Disposable disposable;
    ProgressDialog progressDialog;
    Activity activity;
    String message;
    boolean onlyOnce;

    public RxLoadingObserver(Activity activity) {
        this(activity, false);
    }

    public RxLoadingObserver(Activity activity, boolean onlyOnce) {
        this(activity, SystemUtil.isChinese() ? "加载中..." : "Loading...");
        this.onlyOnce = onlyOnce;
    }

    public RxLoadingObserver(Activity activity, String message) {
        this(activity, message, false);
    }

    public RxLoadingObserver(Activity activity, String message, boolean onlyOnce) {
        this.activity = activity;
        this.message = message;
        this.onlyOnce = onlyOnce;
    }

    @Override
    public void onSubscribe(Disposable d) {
        this.disposable = d;
        this.progressDialog = ProgressDialog.show(activity, null, message);
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        dismissProgressDialog();
        unregistSelf();
    }

    @Override
    public void onComplete() {
        dismissProgressDialog();
        unregistSelf();
    }

    void dismissProgressDialog() {
        try {
            progressDialog.dismiss();
            progressDialog = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void unregistSelf() {
        if(onlyOnce) {
            disposable.dispose();
        }
    }
}
