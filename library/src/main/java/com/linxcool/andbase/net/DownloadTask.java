package com.linxcool.andbase.net;

import com.linxcool.andbase.db.DbHelper;
import com.linxcool.andbase.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 下载任务
 * @author: 胡昌海(linxcool.hu)
 */
public class DownloadTask implements Runnable {

    public static final int SUCCESS_NORMAL = 200;
    public static final int SUCCESS_ON_CHECK = 201;

    public static final int ERROR_UNKNOW = 400;
    public static final int ERROR_OPEN_CONNECTION_FAIL = 401;
    public static final int ERROR_CONTENT_LENGTH_INVALID = 402;
    public static final int ERROR_PAUSE = 403;

    private DbHelper<DownloadFile> dbHelper;

    private DownloadFile fileInfo;
    private DownloadListener listener;

    private int lastProgress;
    private boolean pause;

    /**
     * 将根据DbHelper是否为null 进行数据存储 否则仅限下载
     *
     * @param dbHelper
     * @param listener
     */
    public DownloadTask(DbHelper<DownloadFile> dbHelper, DownloadListener listener) {
        this.dbHelper = dbHelper;
        this.listener = listener;
    }

    private void loadDbCache(DownloadFile fileInfo) {
        if (dbHelper != null) {
            DownloadFile dbCache = dbHelper.selectByKey(DownloadFile.KEY_RELATION, fileInfo.relation);
            if (dbCache == null) {
                dbHelper.insert(fileInfo);
                dbCache = dbHelper.selectByKey(DownloadFile.KEY_RELATION, fileInfo.relation);
            }
            fileInfo.fillData(dbCache);
        }
    }

    private void updateDbCache(DownloadFile fileInfo) {
        if (dbHelper != null) {
            dbHelper.update(fileInfo);
        }
    }

    protected Integer doInBackground(DownloadFile fileInfo) {
        loadDbCache(fileInfo);

        InputStream is = null;
        RandomAccessFile raf = null;
        HttpURLConnection conn = null;

        String folder = FileUtil.getFolder(fileInfo.filePath);
        FileUtil.mkFileDirs(folder);

        if (listener != null) {
            listener.onBegin(fileInfo);
        }

        try {
            // 检测本地文件
            File localFile = new File(fileInfo.filePath);
            if (localFile.exists()) {
                fileInfo.downedSize = fileInfo.totalSize = (int) localFile.length();
                return SUCCESS_ON_CHECK;
            }

            // 检测本地临时文件
            int startPos = 0;
            File tmpFile = new File(fileInfo.filePath + ".tmp");
            if (tmpFile.exists()) {
                if (tmpFile.isFile()) startPos = (int) tmpFile.length();
                else tmpFile.delete();
            } else {
                tmpFile.createNewFile();
            }

            raf = new RandomAccessFile(tmpFile, "rw");
            raf.seek(startPos);

            // 创建连接
            String url = fileInfo.url;
            conn = getHttpConnection(url, startPos);
            if (conn == null) {
                return ERROR_OPEN_CONNECTION_FAIL;
            }

            conn.connect();
            int resCode = conn.getResponseCode();
            if (resCode != 206 && resCode != 200) {
                return ERROR_OPEN_CONNECTION_FAIL;
            }

            int cntLength = conn.getContentLength();
            int totalLength = startPos + cntLength;
            if(totalLength > 0) {
                fileInfo.totalSize = totalLength;
            } else {
                return ERROR_CONTENT_LENGTH_INVALID;
            }

            fileInfo.status = DownloadFile.STATUS_DOWNLOADING;

            // 文件拷贝
            is = conn.getInputStream();
            int curLength = startPos;

            int read = 0;
            byte buf[] = new byte[64];

            while ((read = is.read(buf)) != -1) {
                raf.write(buf, 0, read);
                curLength += read;
                fileInfo.downedSize = curLength;

                // 以下代码影响下载速度
                //updateDbCache(fileInfo);

                onProgressUpdate(curLength, totalLength);

                if (pause) {
                    raf.close();
                    is.close();
                    return ERROR_PAUSE;
                }
            }

            if (read <= 0) {
                tmpFile.renameTo(new File(fileInfo.filePath));
                updateDbCache(fileInfo);
                return SUCCESS_NORMAL;
            }

            return ERROR_UNKNOW;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (raf != null) raf.close();
            } catch (Exception e) {
            }
            try {
                if (is != null) is.close();
            } catch (Exception e) {
            }
            try {
                if (conn != null) conn.disconnect();
            } catch (Exception e) {
            }
        }
        return ERROR_UNKNOW;
    }

    private HttpURLConnection getHttpConnection(String url, int start) {
        try {
            URL httpUrl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) httpUrl.openConnection();
            httpURLConnection.setAllowUserInteraction(true);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setRequestProperty("Range", "bytes=" + start + "-");
            return httpURLConnection;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void onPreExecute() {
        pause = false;
    }

    protected void onPostExecute(Integer result) {
        updateDbCache(fileInfo);
        switch (result) {
            case SUCCESS_NORMAL:
            case SUCCESS_ON_CHECK:
                fileInfo.status = DownloadFile.STATUS_DOWNLOADED;
                if (listener != null) {
                    listener.onComplete(fileInfo);
                }
                break;
            case ERROR_PAUSE:
            case ERROR_UNKNOW:
            case ERROR_OPEN_CONNECTION_FAIL:
            case ERROR_CONTENT_LENGTH_INVALID:
                fileInfo.status = DownloadFile.STATUS_PAUSE;
                if (listener != null) {
                    listener.onError(result, fileInfo);
                }
                break;
        }
    }

    protected void onProgressUpdate(int current, int total) {
        int progress = (int) (current * 100.0 / total);
        if (progress <= lastProgress) return;
        lastProgress = progress;
        if (listener != null) {
            listener.onUpdate(fileInfo);
        }
    }

    public void start(DownloadFile fileInfo) {
        this.fileInfo = fileInfo;
        new Thread(this).start();
    }

    public void cancel() {
        pause = true;
    }

    @Override
    public void run() {
        onPreExecute();
        int result = doInBackground(fileInfo);
        onPostExecute(result);
    }
}
