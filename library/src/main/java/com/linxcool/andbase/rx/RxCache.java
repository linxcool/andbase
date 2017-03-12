package com.linxcool.andbase.rx;

import android.content.Context;

import com.linxcool.andbase.util.CacheUtil;

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

    public static <T> Observable<T> load(final Context context,
                                         final String cacheKey,
                                         final int expireTime,
                                         Observable<T> fromNetwork,
                                         boolean forceRefresh) {
        Observable<T> fromCache = Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> e) throws Exception {

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        /**
         * 这里的fromNetwork 不需要指定Schedule,在handleRequest中已经变换了
         */
        fromNetwork = fromNetwork.map(new Function<T, T>() {
            @Override
            public T apply(T t) throws Exception {
                return null;
            }
        });
        //强制刷新则返回接口数据
        if (forceRefresh) {
            return fromNetwork;
        } else {
            CacheUtil.get(context);
            //优先返回缓存
            //return Observable.concat(fromCache, fromNetwork).first()
            return null;
        }
    }

}
