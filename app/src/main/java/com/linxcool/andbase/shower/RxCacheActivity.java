package com.linxcool.andbase.shower;

import android.os.Bundle;
import android.view.MotionEvent;

import com.linxcool.andbase.BaseActivity;
import com.linxcool.andbase.demo.R;
import com.linxcool.andbase.rx.RxCache;
import com.linxcool.andbase.rx.RxHelper;
import com.linxcool.andbase.rx.RxLoadingObserver;
import com.linxcool.andbase.util.LogUtil;
import com.linxcool.andbase.util.ui.ToastUtil;

import java.io.Serializable;

import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
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
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Observable<AbcBean> fromNetwork = Observable.create(new ObservableOnSubscribe<AbcBean>() {
                @Override
                public void subscribe(ObservableEmitter<AbcBean> e) throws Exception {
                    // 模拟网络请求，休息3秒
                    Thread.sleep(3000);
                    e.onNext(new AbcBean(0, "time is " + System.currentTimeMillis()));
                    e.onComplete();
                }
            }).compose(RxHelper.<AbcBean>scheduleIo2UiThread());

            RxCache.load(this, "abcBean", 5, fromNetwork, false)
                    .subscribe(new RxLoadingObserver<AbcBean>(this) {
                        @Override
                        public void onSubscribe(Disposable d) {
                            LogUtil.i("onSubscribe....");
                            super.onSubscribe(d);
                        }

                        @Override
                        public void onNext(AbcBean value) {
                            LogUtil.i("onNext....");
                            ToastUtil.show(RxCacheActivity.this, value.toString());
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            LogUtil.e("onError....");
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
                            super.onComplete();
                            LogUtil.i("onComplete....");
                        }
                    });


        }
        return super.onTouchEvent(event);
    }

}
