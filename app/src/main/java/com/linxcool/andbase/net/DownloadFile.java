package com.linxcool.andbase.net;

import android.database.Cursor;

import com.linxcool.andbase.db.DbInfo;
import com.linxcool.andbase.db.DbTag;

/**
 * 下载的文件信息
 * @author: 胡昌海(linxcool.hu)
 */
public class DownloadFile extends DbInfo {

	private static final long serialVersionUID = 0x12334567891L;

	/** 列表中 */
	public static final int STATUS_NONE = 0;
	/** 队列中 */
	public static final int STATUS_QUEUE = 1;
	/** 下载中 */
	public static final int STATUS_DOWNLOADING = 2;
	/** 暂停 */
	public static final int STATUS_PAUSE = 3;
	/** 下载完成 */
	public static final int STATUS_DOWNLOADED = 4;

	/** 文件ID */
	@DbTag(name = "id",
			type = DbTag.TYPE_INTEGER,
			constraint = DbTag.CONSTRAINT_PRIMARY_KEY,
			autoIncrease = true)
	public int id;

	/** 外部关联 如外键 下载时作为唯一标识 */
	@DbTag(name = KEY_RELATION)
	public String relation;
	public static final String KEY_RELATION = "relation";

	/** 下载地址 */
	@DbTag(name = "down_url", type = DbTag.TYPE_VARCHAR_512)
	public String url;

	/** 已下载大小 */
	@DbTag(name = "downed_size", type = DbTag.TYPE_INTEGER)
	public int downedSize;

	/** 文件总大小 */
	@DbTag(name = "total_size", type = DbTag.TYPE_INTEGER)
	public int totalSize;

	/** 文件存储路径 */
	@DbTag(name = "file_path", type = DbTag.TYPE_VARCHAR_512)
	public String filePath;

	/** 文件状态 */
	@DbTag(name = "status", type = DbTag.TYPE_INTEGER)
	public int status;

	public DownloadTask task;

	@Override
	public String getTableName() {
		return "downloads";
	}

	public DownloadFile(){
		super();
	}

	public DownloadFile(Cursor cursor){
		super(cursor);
	}

	public void fillData(DownloadFile info){
		this.id = info.id;
		this.relation = info.relation;
		this.url = info.url;
		this.downedSize = info.downedSize;
		this.totalSize = info.totalSize;
		this.filePath = info.filePath;
		this.status = info.status;
	}
}
