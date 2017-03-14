package com.linxcool.andbase.util;

import java.util.Locale;

/**
 * Created by linxcool on 17/3/14.
 */

public class SystemUtil {

    public static boolean isChinese() {
        String locale = Locale.getDefault().getLanguage();
        return "zh".equals(locale);
    }

}
