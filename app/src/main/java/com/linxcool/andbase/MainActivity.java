package com.linxcool.andbase;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.linxcool.andbase.demo.R;
import com.linxcool.andbase.shower.Shower;
import com.linxcool.andbase.util.ui.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * <p>
 * 本页面包含很多ButterKnife用法，详见：
 * http://jakewharton.github.io/butterknife/
 * </p>
 * Created by linxcool on 17/3/11.
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.listView)
    ListView listView;

    // ................just list......................
    // @BindString
    // @BindDrawable
    // @BindColor
    // @BindDimen
    // ................end list.......................

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

    @OnItemClick(R.id.listView)
    public void onItemClick(int position) {
        Object item = listView.getAdapter().getItem(position);
        Intent intent = new Intent(this, Shower.Tools.findShowerClass(this, String.valueOf(item)));
        startActivity(intent);
    }

}