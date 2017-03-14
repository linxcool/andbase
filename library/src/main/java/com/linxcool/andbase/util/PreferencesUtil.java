package com.linxcool.andbase.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * SharedPreferences操作类
 * @author linxcool
 * created at 2017/3/14 17:37
 */
public class PreferencesUtil {

    private static SharedPreferences preferences;

    private static SharedPreferences get(Context context) {
        if (preferences == null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return get(context);
    }

    public static void setInt(Context context, String key, int value) {
        get(context).edit().putInt(key, value).apply();
    }

    public static int getInt(Context context, String key) {
        return get(context).getInt(key, 0);
    }

    public static void setLong(Context context, String key, long value) {
        get(context).edit().putLong(key, value).apply();
    }

    public static long getLong(Context context, String key) {
        return get(context).getLong(key, 0l);
    }

    public static void setFloat(Context context, String key, float value) {
        get(context).edit().putFloat(key, value).apply();
    }

    public static Float getFloat(Context context, String key) {
        return get(context).getFloat(key, 0f);
    }

    public static void setBoolean(Context context, String key, boolean value) {
        get(context).edit().putBoolean(key, value).apply();
    }

    public static Boolean getBoolean(Context context, String key) {
        return get(context).getBoolean(key, false);
    }

    public static void setString(Context context, String key, String value) {
        get(context).edit().putString(key, value).apply();
    }

    public static String getString(Context context, String key) {
        return get(context).getString(key, "");
    }

}