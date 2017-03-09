package com.linxcool.andbase.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用户数据库的注解标识
 * @author 胡昌海(linxcool.hu)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DbTag {

	public static final String CONSTRAINT_NULL = "";
	public static final String CONSTRAINT_NOT_NULL = "NOT NULL";
	public static final String CONSTRAINT_PRIMARY_KEY = "PRIMARY KEY";
	
	public static final String TYPE_INTEGER = "INTEGER";
	public static final String TYPE_FLOAT = "FLOAT";
	public static final String TYPE_DOUBLE = "DOUBLE";
	public static final String TYPE_VARCHAR_64 = "VARCHAR(64)";
	public static final String TYPE_VARCHAR_512 = "VARCHAR(512)";
	public static final String TYPE_TEXT = "TEXT";
	public static final String TYPE_DATATIME = "DATATIME";
	
	public String name();
	
	public String type() default TYPE_VARCHAR_64;
	
	public String constraint() default CONSTRAINT_NULL;
	
	public boolean ignore() default false;
	
	public boolean autoIncrease() default false;
}
