package com.linxcool.andbase.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据存储辅助类
 *
 * @author 胡昌海(linxcool.hu)
 */
public class DbHelper<T extends DbInfo> extends SQLiteOpenHelper {

    protected static final String DATABASE_NAME = "andbase.db";
    protected static final int DATABASE_VERSION = 1;
    protected static Object lock = new Object();

    protected Context context;
    protected T dbInfo;
    protected Constructor<T> constructor;

    public Context getContext() {
        return context;
    }

    public DbHelper(Context context, T dbInfo) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.dbInfo = dbInfo;
        try {
            constructor = (Constructor<T>) dbInfo.getClass().getConstructor(Cursor.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        onCreate(getWritableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(dbInfo.getCreateSql());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(dbInfo.getDropSql());
        this.onCreate(db);
    }

    /**
     * 查询全部信息
     *
     * @return
     */
    public List<T> selectAll() {
        return selectInCase("");
    }

    /**
     * 查询前x条
     *
     * @param limit
     * @return
     */
    public List<T> selectTop(int limit) {
        return selectInCase(String.format("LIMIT %d", limit));
    }

    /**
     * 分页查询
     *
     * @param offset
     * @param pageSize
     * @return
     */
    public List<T> selectPage(int offset, int pageSize) {
        return selectInCase(String.format("LIMIT %d, %d", offset, pageSize));
    }

    /**
     * 指定条件查询
     * @param value
     * @return
     */
    public List<T> selectInCase(String value) {
        synchronized (lock) {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = null;
            List<T> list = new ArrayList<T>();
            try {
                String sql = dbInfo.getSelectSql(value);
                cursor = db.rawQuery(sql, new String[]{});
                while (cursor.moveToNext()) {
                    list.add(constructor.newInstance(cursor));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) cursor.close();
                db.close();
            }
            return list;
        }
    }

    /**
     * 根据某一字段查询
     *
     * @param key
     * @param value
     * @return
     */
    @SuppressWarnings("unchecked")
    public T selectByKey(String key, Object value) {
        synchronized (lock) {
            Cursor cursor = null;
            SQLiteDatabase db = getReadableDatabase();
            try {
                String sql = dbInfo.getSelectSql(key, value);
                cursor = db.rawQuery(sql, new String[]{});
                if (cursor.moveToNext()) {
                    return constructor.newInstance(cursor);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) cursor.close();
                db.close();
            }
            return null;
        }
    }

    /**
     * 插入数据
     *
     * @param info
     */
    public boolean insert(T info) {
        synchronized (lock) {
            SQLiteDatabase db = getWritableDatabase();
            try {
                db.beginTransaction();
                Pair<String, Object[]> pair = info.getInsertParameters();
                db.execSQL(pair.first, pair.second);
                db.setTransactionSuccessful();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                db.endTransaction();
                db.close();
            }
        }
    }

    /**
     * 修改数据
     *
     * @param info
     */
    public void update(T info) {
        synchronized (lock) {
            SQLiteDatabase db = getWritableDatabase();
            try {
                db.beginTransaction();
                Pair<String, Object[]> pair = info.getUpdateParameters();
                db.execSQL(pair.first, pair.second);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
                db.close();
            }
        }
    }

    /**
     * 删除数据
     *
     * @param key
     * @param value
     */
    public void deleteByKey(String key, Object value) {
        synchronized (lock) {
            SQLiteDatabase db = getWritableDatabase();
            try {
                db.beginTransaction();
                Pair<String, Object[]> pair = dbInfo.getDeleteParameters(key, value);
                db.execSQL(pair.first, pair.second);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
                db.close();
            }
        }
    }

    /**
     * 删除已发送数据 WHERE IN(xxx)
     *
     * @param key
     * @param value
     */
    public void deleteInCase(String key, String value) {
        synchronized (lock) {
            SQLiteDatabase db = getWritableDatabase();
            try {
                String sql = dbInfo.getDeleteSqlInCase(key, value);
                db.beginTransaction();
                db.execSQL(sql);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
                db.close();
            }
        }
    }

    /**
     * 删除过期数据 必须有time字段
     *
     * @param time 过期秒数
     */
    public void clearOverdueData(long time) {
        synchronized (lock) {
            SQLiteDatabase db = getWritableDatabase();
            try {
                String sql = String.format("DELETE FROM %s WHERE %s",
                        dbInfo.getTableName(),
                        "(strftime('%s','now')-strftime('%s', time)) > " + time
                );
                db.beginTransaction();
                db.execSQL(sql);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
                db.close();
            }
        }
    }
}
