package com.linxcool.andbase.util;

import android.text.TextUtils;

import java.text.DecimalFormat;

/**
 * Created by huchanghai on 2017/3/14.
 */
public class TextUtil {

    public static String toTwoPoint(double d){
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(d);
    }

    public static boolean notEmpty(CharSequence str) {
        return !TextUtils.isEmpty(str);
    }
}
