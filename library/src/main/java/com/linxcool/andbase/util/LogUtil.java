package com.linxcool.andbase.util;

import android.util.Log;

public final class LogUtil {

    private static final String TAG = "andbase";

    private LogUtil() {
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }

    public static void eFormat(String format, Object... args) {
        Log.e(TAG, String.format(format, args));
    }

    public static void w(String msg) {
        Log.w(TAG, msg);
    }

    public static void i(String msg) {
        Log.i(TAG, msg);
    }

    public static void iFormat(String format, Object... args) {
        Log.i(TAG, String.format(format, args));
    }

    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    public static void dFormat(String format, Object... args) {
        Log.d(TAG, String.format(format, args));
    }

}
