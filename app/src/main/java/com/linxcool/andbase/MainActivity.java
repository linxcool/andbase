package com.linxcool.andbase;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.linxcool.andbase.shower.Shower;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.linxcool.andbase.demo.R;

public class MainActivity extends BaseActivity {

    @BindView(R.id.listView)
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                Shower.Tools.listShowerNames(this));
        listView.setAdapter(adapter);
    }

}
