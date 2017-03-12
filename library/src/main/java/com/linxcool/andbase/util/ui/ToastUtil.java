package com.linxcool.andbase.util.ui;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    public static void show(Context context, String text) {
        show(context, text, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, String text, int duration) {
        if (context != null) {
            Toast.makeText(context, text, duration).show();
        }
    }

    public static void showInUiThread(final Activity activity, final String text) {
        showInUiThread(activity, text, Toast.LENGTH_SHORT);
    }

    public static void showInUiThread(final Activity activity, final String text, final int duration) {
        if (activity == null || activity.isFinishing()) return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, text, duration).show();
            }
        });
    }
}
