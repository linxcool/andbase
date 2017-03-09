package com.linxcool.andbase.mvp;
/**
 * Created by huchanghai on 2016/10/24.
 */
public interface BaseView<T> {

    void setPresenter(T presenter);

    void showToastMessage(String msg);

    boolean isShowing();
    
    void closeSelf();
    
    void showProgress(boolean active);

}
