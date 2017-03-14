package com.linxcool.andbase.util.ui;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

    /**
     * 显示有image的toast
     *
     * @param msg
     * @param imgResId
     * @return
     */
    public static void showWithImage(Context context, String msg, int imgResId) {
        Toast toast = new Toast(context);

        int padding = DisplayUtil.dp2px(context, 10);
        LinearLayout layout = new LinearLayout(context);
        layout.setPadding(2 * padding, padding, 2 * padding, padding);
        layout.setOrientation(LinearLayout.VERTICAL);

        ImageView iv = new ImageView(context);
        if (imgResId > 0) {
            iv.setVisibility(View.VISIBLE);
            iv.setImageResource(imgResId);
        } else {
            iv.setVisibility(View.GONE);
        }
        int size = DisplayUtil.dp2px(context, 60);
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(size, size);
        ivParams.bottomMargin = DisplayUtil.dp2px(context, 5);
        layout.addView(iv, ivParams);

        TextView tv = new TextView(context);
        tv.setText(TextUtils.isEmpty(msg) ? "" : msg);

        toast.setView(layout);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
