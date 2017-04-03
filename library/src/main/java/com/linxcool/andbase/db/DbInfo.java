package com.linxcool.andbase.db;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.util.Log;
import android.util.Pair;

/**
 * 数据基本信息
 *
 * @author 胡昌海(linxcool.hu)
 */
public abstract class DbInfo implements Serializable {

    private static final long serialVersionUID = 0x1233456789L;

    private static final String TAG = "DbInfo";

    protected boolean debug;

    /**
     * 返回表名称
     *
     * @return
     */
    public abstract String getTableName();

    /**
     * 构造器
     */
    public DbInfo() {
        // Empty
    }

    /**
     * 构造器
     *
     * @param cursor
     */
    public DbInfo(Cursor cursor) {
        try {
            setFieldInfos(getClass(), cursor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置属性值
     *
     * @param cls
     * @param cursor
     * @throws Exception
     */
    protected void setFieldInfos(Class<?> cls, Cursor cursor) throws Exception {
        Class<?> superCls = cls.getSuperclass();
        if (superCls != null && superCls != DbInfo.class) {
            setFieldInfos(superCls, cursor);
        }
        Field[] fields = cls.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            if (!fields[i].isAnnotationPresent(DbTag.class)) {
                continue;
            }
            DbTag tag = fields[i].getAnnotation(DbTag.class);
            int columnIndex = cursor.getColumnIndex(tag.name());
            if (tag.type() == DbTag.TYPE_INTEGER) {
                fields[i].setInt(this, cursor.getInt(columnIndex));
            } else if (tag.type() == DbTag.TYPE_FLOAT) {
                fields[i].setFloat(this, cursor.getFloat(columnIndex));
            } else if (tag.type() == DbTag.TYPE_DOUBLE) {
                fields[i].setDouble(this, cursor.getDouble(columnIndex));
            } else {
                fields[i].set(this, cursor.getString(columnIndex));
            }
        }
    }

    /**
     * 返回属性描述
     *
     * @param cls
     * @param cnt
     * @return
     */
    protected String getFieldInfos(Class<?> cls, String cnt) {
        Class<?> superCls = cls.getSuperclass();
        if (superCls != null && superCls != DbInfo.class) {
            cnt = getFieldInfos(superCls, cnt);
        }
        Field[] fields = cls.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            if (!fields[i].isAnnotationPresent(DbTag.class)) {
                continue;
            }
            DbTag tag = fields[i].getAnnotation(DbTag.class);
            cnt += String.format(", %s %s %s", tag.name(), tag.type(), tag.constraint());
        }
        return cnt.substring(1);
    }

    /**
     * 返回创建该表的SQL语句
     *
     * @return
     */
    public String getCreateSql() {
        String cnt = getFieldInfos(getClass(), "");
        cnt = String.format(
                "CREATE TABLE IF NOT EXISTS %s (%s)",
                getTableName(),
                cnt);
        if (debug) {
            Log.d(TAG, "get create sql is " + cnt);
        }
        return cnt;
    }

    /**
     * 返回删除该表的SQL语句
     *
     * @return
     */
    public String getDropSql() {
        String rs = "DROP TABLE IF EXISTS " + getTableName();
        if (debug) {
            Log.d(TAG, "get create sql is " + rs);
        }
        return rs;
    }

    /**
     * 返回查询整表的SQL语句
     *
     * @return
     */
    public String getSelectSql(String append) {
        String rs = "SELECT * FROM " + getTableName() + " " + append;
        if (debug) {
            Log.d(TAG, "get select sql is " + rs);
        }
        return rs;
    }

    public String getSelectSql() {
        return getSelectSql("");
    }

    /**
     * 返回根据条件查询的SQL语句
     *
     * @param key   属性
     * @param value 属性值
     * @return
     */
    public String getSelectSql(String key, Object value) {
        String arg = String.valueOf(value);
        if (value instanceof String) {
            arg = "'" + arg + "'";
        }
        String rs = String.format(
                "SELECT * FROM %s WHERE %s = %s",
                getTableName(), key, arg);
        if (debug) {
            Log.d(TAG, "get select sql is " + rs);
        }
        return rs;
    }

    /**
     * 返回插入语句的参数
     *
     * @return SQL语句及值对象数组
     * @throws Exception
     */
    public Pair<String, Object[]> getInsertParameters() throws Exception {
        List<String> names = new ArrayList<String>();
        List<Object> values = new ArrayList<Object>();
        setColumnNVPairs(getClass(), names, values);

        StringBuilder args = new StringBuilder();
        StringBuilder vars = new StringBuilder();
        Object[] rsSecond = new Object[names.size()];
        for (int i = 0; i < names.size(); i++) {
            args.append("," + names.get(i));
            vars.append(", ?");
            rsSecond[i] = values.get(i);
        }

        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)",
                getTableName(),
                args.substring(1),
                vars.substring(1));

        if (debug) {
            Log.d(TAG, "get insert sql is " + sql);
        }

        return new Pair<String, Object[]>(sql, rsSecond);
    }

    /**
     * 返回表列名称和值列表
     *
     * @param cls
     * @param names
     * @param values
     * @throws Exception
     */
    protected void setColumnNVPairs(Class<?> cls, List<String> names, List<Object> values) throws Exception {
        Class<?> superCls = cls.getSuperclass();
        if (superCls != null && superCls != DbInfo.class) {
            setColumnNVPairs(superCls, names, values);
        }
        Field[] fields = cls.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            if (!fields[i].isAnnotationPresent(DbTag.class))
                continue;
            DbTag tag = fields[i].getAnnotation(DbTag.class);
            if (tag.autoIncrease())
                continue;
            names.add(tag.name());
            values.add(fields[i].get(this));
        }
    }

    /**
     * 返回修改记录的参数
     *
     * @return
     * @throws Exception
     */
    public Pair<String, Object[]> getUpdateParameters() throws Exception {
        Field pkField = getPrimaryKeyField(getClass());
        DbTag pkTag = pkField.getAnnotation(DbTag.class);

        List<String> names = new ArrayList<String>();
        List<Object> values = new ArrayList<Object>();
        setColumnNVPairs(getClass(), names, values);

        StringBuilder setArgs = new StringBuilder();
        Object[] rsSecond = new Object[names.size() + 1];
        for (int i = 0; i < names.size(); i++) {
            setArgs.append(", " + names.get(i) + " = ?");
            rsSecond[i] = values.get(i);
        }
        rsSecond[names.size()] = pkField.get(this);

        String sql = String.format("UPDATE %s SET %s WHERE %s = ?",
                getTableName(),
                setArgs.substring(1),
                pkTag.name());

        if (debug) {
            Log.d(TAG, "get setNotified sql is " + sql);
        }

        return new Pair<String, Object[]>(sql, rsSecond);
    }

    /**
     * 返回主键属性
     *
     * @param cls
     * @return
     */
    protected Field getPrimaryKeyField(Class<?> cls) {
        Field[] fields = cls.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            if (!fields[i].isAnnotationPresent(DbTag.class))
                continue;
            DbTag tag = fields[i].getAnnotation(DbTag.class);
            if (tag.autoIncrease())
                return fields[i];
            if (tag.constraint().equals(DbTag.CONSTRAINT_PRIMARY_KEY)) {
                return fields[i];
            }
        }

        Class<?> superCls = cls.getSuperclass();
        if (superCls != null && superCls != DbInfo.class) {
            return getPrimaryKeyField(superCls);
        }

        return null;
    }

    /**
     * 返回删除记录的SQL语句
     *
     * @param key
     * @param value
     * @return
     */
    public Pair<String, Object[]> getDeleteParameters(String key, Object value) {
        String sql = String.format("DELETE FROM %s WHERE %s = ?", getTableName(), key);

        if (debug) {
            Log.d(TAG, "get delete sql is " + sql);
        }

        return new Pair<String, Object[]>(sql, new Object[]{value});
    }

    public String getDeleteSqlInCase(String key, String value) {
        String rs = String.format("DELETE FROM %s WHERE %s IN (%s)",
                getTableName(), key, value);
        if (debug) {
            Log.d(TAG, "get delete sql is " + rs);
        }
        return rs;
    }
}
