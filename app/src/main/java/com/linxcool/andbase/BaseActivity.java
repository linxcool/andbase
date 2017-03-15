package com.linxcool.andbase;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by linxcool on 17/3/11.
 */

public class BaseActivity extends AppCompatActivity {

    ViewGroup contentView;

    public BaseActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        contentView = (ViewGroup) LayoutInflater.from(this).inflate(layoutResID, null);
        super.setContentView(contentView);
    }

    public void addView(View view) {
        addView(view, new ViewGroup.LayoutParams(-1, -1));
    }

    public void addView(View view, ViewGroup.LayoutParams params) {
        contentView.addView(view, params);
    }

    protected void enableHomeback() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            onTouchUp();
        }
        return super.onTouchEvent(event);
    }

    protected void onTouchUp() {
    }
}
