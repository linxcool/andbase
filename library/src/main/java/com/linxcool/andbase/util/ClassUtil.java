package com.linxcool.andbase.util;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;

/**
 * Created by linxcool on 17/3/11.
 */

public final class ClassUtil {

    private ClassUtil() {
    }

    /**
     * 查找子类或接口实现类
     *
     * @param context           上下文对象
     * @param supperOrInterface 父类或接口
     * @return
     * @throws Exception
     */
    public static List<Class<?>> findSubClasses(
            Context context, Class<?> supperOrInterface) throws Exception {
        return findSubClasses(context, supperOrInterface, null);
    }

    /**
     * 查找子类或接口实现类
     *
     * @param context           上下文对象
     * @param supperOrInterface 父类或接口
     * @param clsPackage        类包名
     * @return
     * @throws Exception
     */
    public static List<Class<?>> findSubClasses(
            Context context, Class<?> supperOrInterface, String clsPackage) throws Exception {
        List<Class<?>> list = new ArrayList<>();
        DexFile dexFile = new DexFile(context.getPackageCodePath());
        Enumeration<String> entries = dexFile.entries();
        while (entries.hasMoreElements()) {
            String clsName = (String) entries.nextElement();
            // 存在过滤，且不符合要求跳过
            if (!TextUtils.isEmpty(clsPackage) && !clsName.startsWith(clsPackage)) {
                continue;
            }
            // 查找并加入list
            Class<?> cls = findClass(clsName);
            if (cls != null && supperOrInterface.isAssignableFrom(cls) && cls != supperOrInterface) {
                list.add(cls);
            }
        }

        return list;
    }

    /**
     * 构建拥有无参构造函数类的对象
     * @param clsName
     * @param <T>
     * @return
     */
    public static <T> T newObject(String clsName) {
        return newObject(findClass(clsName));
    }

    /**
     * 构建拥有无参构造函数类的对象
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T newObject(Class<?> cls) {
        try {
            return (T) cls.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据类名查找类
     *
     * @param clsName 类名
     * @return 存在返回Class，否则Null
     */
    public static Class<?> findClass(String clsName) {
        try {
            return Class.forName(clsName);
        } catch (ClassNotFoundException e) {
            // e.printStackTrace();
            return null;
        }
    }

}
