package com.linxcool.andbase.db;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

/**
 * 数据存储辅助类
 * @author 胡昌海(linxcool.hu)
 */
public class DbHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "preach.db";
	private static final int DATABASE_VERSION = 100;
	protected static Object lock = new Object();

	protected Context context;
	protected DbInfo dbInfo;
	protected Constructor<? extends DbInfo> constructor;
	
	public Context getContext() {
		return context;
	}
	
	public DbHelper(Context context, DbInfo dbInfo) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
		this.dbInfo = dbInfo;
		try {
			constructor = dbInfo.getClass().getConstructor(Cursor.class);
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
	 * @return
	 */
	public List<? extends DbInfo> selectAll(){
		synchronized (lock) {
			SQLiteDatabase db = getReadableDatabase();
			Cursor cursor = null;
			List<DbInfo> list = new ArrayList<DbInfo>();
			try{
				String sql = dbInfo.getSelectSql();
				cursor = db.rawQuery(sql, new String[]{});
				while (cursor.moveToNext()){
					list.add(constructor.newInstance(cursor));
				}
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursor!=null) cursor.close();
				db.close();
			}
			return list;
		}
	}
	
	public List<? extends DbInfo> select(int limit){
		synchronized (lock) {
			SQLiteDatabase db = getReadableDatabase();
			Cursor cursor = null;
			List<DbInfo> list = new ArrayList<DbInfo>();
			try{
				String sql = dbInfo.getSelectSql() + " LIMIT " + limit;
				cursor = db.rawQuery(sql, new String[]{});
				while (cursor.moveToNext()){
					list.add(constructor.newInstance(cursor));
				}
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursor!=null) cursor.close();
				db.close();
			}
			return list;
		}
	}

	/**
	 * 根据某一字段查询
	 * @param key
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T>T select(String key, Object value){
		synchronized (lock) {
			Cursor cursor = null;
			SQLiteDatabase db = getReadableDatabase();
			try{
				String sql = dbInfo.getSelectSql(key, value);
				cursor = db.rawQuery(sql, new String[]{});
				if (cursor.moveToNext()){
					return (T) constructor.newInstance(cursor);
				}
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursor!=null) cursor.close();
				db.close();
			}
			return null;
		}
	}

	/**
	 * 插入数据
	 * @param dbInfo
	 */
	public boolean insert(DbInfo dbInfo) {
		synchronized (lock) {
			SQLiteDatabase db = getWritableDatabase();
			try {
				db.beginTransaction();
				Pair<String, Object[]> pair = dbInfo.getInsertParameters();
				db.execSQL(pair.first, pair.second);
				db.setTransactionSuccessful();
				return true;
			}catch (Exception e) {
				e.printStackTrace();
				return false;
			}finally {
				db.endTransaction();
				db.close();
			}
		}
	}

	/**
	 * 修改数据
	 * @param info
	 */
	public void update(DbInfo info) {
		synchronized (lock) {
			SQLiteDatabase db = getWritableDatabase();
			try {
				db.beginTransaction();
				Pair<String, Object[]> pair = info.getUpdateParameters();
				db.execSQL(pair.first, pair.second);
				db.setTransactionSuccessful();
			}catch (Exception e) {
				e.printStackTrace();
			}finally {
				db.endTransaction();
				db.close();
			}
		}
	}

	/**
	 * 删除数据
	 * @param key
	 * @param value
	 */
	public void delete(String key, Object value) {
		synchronized (lock) {
			SQLiteDatabase db = getWritableDatabase();
			try{
				db.beginTransaction();
				Pair<String, Object[]> pair = dbInfo.getDeleteParameters(key, value);
				db.execSQL(pair.first, pair.second);
				db.setTransactionSuccessful();
			}catch (Exception e) {
				e.printStackTrace();
			}finally {
				db.endTransaction();
				db.close();
			}
		}
	}
	
	/**
	 * 删除已发送数据 WHERE IN(xxx)
	 * @param key
	 * @param value
	 */
	public void deleteInCase(String key, String value) {
		synchronized (lock) {
			SQLiteDatabase db = getWritableDatabase();
			try{
				String sql = dbInfo.getDeleteSqlInCase(key, value);
				db.beginTransaction();
				db.execSQL(sql);
				db.setTransactionSuccessful();
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				db.endTransaction();
				db.close();
			}
		}
	}
	
	/**
	 * 删除过期数据 必须有time字段
	 * @param time 过期秒数
	 */
	public void clearOverdueData(long time) {
		synchronized (lock) {
			SQLiteDatabase db = getWritableDatabase();
			try{
				String sql = String.format("DELETE FROM %s WHERE %s",
						dbInfo.getTableName(),
						"(strftime('%s','now')-strftime('%s', time)) > " + time
				);
				db.beginTransaction();
				db.execSQL(sql);
				db.setTransactionSuccessful();
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				db.endTransaction();
				db.close();
			}
		}
	}
}
