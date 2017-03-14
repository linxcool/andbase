package com.linxcool.andbase.shower;

import android.os.Bundle;
import android.view.MotionEvent;

import com.linxcool.andbase.BaseActivity;
import com.linxcool.andbase.demo.R;
import com.linxcool.andbase.rx.RxCache;
import com.linxcool.andbase.rx.RxHelper;
import com.linxcool.andbase.util.ui.ToastUtil;

import java.io.Serializable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.observers.SubscriberCompletableObserver;

public class RxCacheActivity extends BaseActivity implements Shower {

    public static class AbcBean implements Serializable {

        private int code;
        private String msg;

        public AbcBean(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        @Override
        public String toString() {
            return code + " | " + msg;
        }
    }

    @Override
    public String getName() {
        return "RxCache";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_cache);
        enableHomeback();
        setTitle(getName());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP) {
            Observable<AbcBean> fromNetwork = Observable
                    .just(new AbcBean(0, "time is " + System.currentTimeMillis()))
                    .compose(RxHelper.<AbcBean>scheduleIo2UiThread());

            RxCache.load(this, "abcBean", 10 * 1000, fromNetwork, false).subscribe(new Observer<AbcBean>() {
                @Override
                public void onSubscribe(Disposable d) {
                    System.out.println(">>>>onSubscribe....");
                }

                @Override
                public void onNext(AbcBean value) {
                    System.out.println(">>>>onNext....");
                    ToastUtil.show(RxCacheActivity.this, value.toString());
                }

                @Override
                public void onError(Throwable e) {
                    System.out.println(">>>>onError....");
                    e.printStackTrace();
                }

                @Override
                public void onComplete() {
                    System.out.println(">>>>onComplete....");
                }
            });


        }
        return super.onTouchEvent(event);
    }

}
