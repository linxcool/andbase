package com.linxcool.andbase.rx;


import android.content.Context;

import com.linxcool.andbase.util.NetWorkUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by huchanghai on 2017/3/14.
 */
public class RxHelper {

    public static <T> ObservableTransformer<T, T> scheduleIo2UiThread() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> ObservableTransformer<T, T> autoRetry(final Context context, final long times) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.retry(times).doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if(NetWorkUtil.isNetConnected(context)) {
                            // TODO
                        } else {
                            // TODO
                        }
                    }
                });
            }
        };
    }

}
