package com.linxcool.andbase.rx;

import android.content.Context;

import com.linxcool.andbase.util.CacheUtil;

import java.io.Serializable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by linxcool on 17/3/12.
 */
public class RxCache {

    /**
     * 优先从缓存中加载数据，若失败或过期则通过fromNetwork对象处理
     * @param context 上下文对象
     * @param cacheKey 缓存KEY
     * @param expireTime 过期事件，单位秒
     * @param fromNetwork 网络处理对象
     * @param forceRefresh 是否强制刷新
     * @param <T>
     * @return
     */
    public static <T> Observable<T> load(final Context context,
                                         final String cacheKey,
                                         final int expireTime,
                                         Observable<T> fromNetwork,
                                         boolean forceRefresh) {
        Observable<T> fromCache = Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> subscriber) throws Exception {
                T cache = (T) CacheUtil.get(context).getAsObject(cacheKey);
                if (cache != null) {
                    subscriber.onNext(cache);
                } else {
                    subscriber.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        fromNetwork = fromNetwork.map(new Function<T, T>() {
            @Override
            public T apply(T obj) throws Exception {
                CacheUtil.get(context).put(cacheKey, (Serializable) obj, expireTime);
                return obj;
            }
        });

        if (forceRefresh) {
            return fromNetwork;
        } else {
            return Observable.concat(fromCache, fromNetwork).firstElement().toObservable();
        }
    }

}
