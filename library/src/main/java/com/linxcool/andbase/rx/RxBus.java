package com.linxcool.andbase.rx;

/**
 * Created by huchanghai on 2017/3/9.
 */
public class RxBus {

    private static RxBus instance = new RxBus();

    public static RxBus getInstance() {
        return instance;
    }

    private RxBus() {
    }

}
