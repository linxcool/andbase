package com.linxcool.andbase.shower;

import android.os.Bundle;

import com.linxcool.andbase.BaseActivity;
import com.linxcool.andbase.demo.R;

public class RxBusActivity extends BaseActivity implements Shower {

    @Override
    public String getName() {
        return "RxBus";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_bus);
    }

}
