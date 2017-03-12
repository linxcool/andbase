package com.linxcool.andbase.shower;

import android.os.Bundle;
import android.view.MotionEvent;

import com.linxcool.andbase.BaseActivity;
import com.linxcool.andbase.demo.R;
import com.linxcool.andbase.rx.RxBus;
import com.linxcool.andbase.util.ui.ToastUtil;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class RxBusActivity extends BaseActivity implements Shower {

    public static class AbcEvent {

        private String msg;

        public String getMsg() {
            return msg;
        }

        public AbcEvent(String msg) {
            this.msg = msg;
        }
    }

    private Disposable disposable;

    @Override
    public String getName() {
        return "RxBus";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_bus);
        enableHomeback();
        setTitle(getName());

        disposable = RxBus.get().toFlowable(AbcEvent.class).subscribe(new Consumer<AbcEvent>() {
            @Override
            public void accept(AbcEvent abcEvent) throws Exception {
                ToastUtil.showInUiThread(RxBusActivity.this, abcEvent.getMsg());
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP) {
            RxBus.get().post(new AbcEvent("hello rxbus!"));
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        disposable.dispose();
    }
}
