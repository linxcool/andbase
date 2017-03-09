package com.linxcool.andbase.mvp;

import android.content.Intent;

/**
 * Created by huchanghai on 2016/10/24.
 */
public interface BaseContract {

    interface UiCallback {
        void onUiResult(int requestCode, int resultCode, Intent data);
    }

}
