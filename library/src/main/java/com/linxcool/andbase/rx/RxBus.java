package com.linxcool.andbase.rx;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * 使用RxJava实现EventBus
 * <pre>
 *     // regist
 *     disposable = RxBus.get().toFlowable(AbcEvent.class).subscribe(new Consumer<AbcEvent>()});
 *     // send event
 *     RxBus.get().post(new AbcEvent("hello rxbus!"));
 *     // unregist
 *     disposable.dispose();
 * </pre>
 * Created by huchanghai on 2017/3/9.
 */
public class RxBus {

    private static RxBus instance = new RxBus();

    public static RxBus get() {
        return instance;
    }

    private final FlowableProcessor<Object> eventBus;

    private RxBus() {
        eventBus = PublishProcessor.create().toSerialized();
    }

    /**
     * 发射事件
     * @param event
     */
    public void post(Object event) {
        eventBus.onNext(event);
    }

    /**
     * 获取事件
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> Flowable<T> toFlowable(Class<T> tClass) {
        return eventBus.ofType(tClass);
    }

    public Flowable<Object> toFlowable() {
        return eventBus;
    }

    public boolean hasSubscribers() {
        return eventBus.hasSubscribers();
    }

}
