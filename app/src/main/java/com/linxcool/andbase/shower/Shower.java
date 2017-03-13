package com.linxcool.andbase.shower;

import android.content.Context;

import com.linxcool.andbase.util.ClassUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by linxcool on 17/3/11.
 */

public interface Shower {

    String getName();

    public static final class Tools {

        private static Map<String, Class<?>> showers;

        private static void loadShower(Context context) {
            try {
                if (showers != null) {
                    return;
                }
                showers = new HashMap<>();
                List<Class<?>> list = ClassUtil.findSubClasses(context, Shower.class, Shower.class.getPackage().getName());
                for (Class<?> cls : list) {
                    Shower item = ClassUtil.newObject(cls);
                    showers.put(item.getName(), cls);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static String[] listShowerNames(Context context) {
            loadShower(context);
            String[] names = new String[showers.size()];
            return showers.keySet().toArray(names);
        }

        public static Class<?> findShowerClass(Context context, String name) {
            loadShower(context);
            return showers.get(name);
        }
    }


}
