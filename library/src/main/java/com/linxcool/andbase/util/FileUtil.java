package com.linxcool.andbase.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by huchanghai on 2016/8/25.
 */
public final class FileUtil {

    private static final int OPTIMIZATION_SIZE = 1024;

    private static boolean forceAppCache;

    private FileUtil() {
    }

    public static void setForceAppCache(boolean forceAppCache) {
        FileUtil.forceAppCache = forceAppCache;
    }

    /**
     * 检查SD卡是否存在
     *
     * @return 存在返回true，否则返回false
     */
    public static boolean isSdCardReady() {
        if (forceAppCache) return false;
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获得SD路径
     *
     * @return sdCard path end with separator
     */
    public static String getSdCardDir() {
        return Environment.getExternalStorageDirectory().toString() + File.separator;
    }

    /**
     * 获取手机该应用的私有路径
     *
     * @param context
     * @return cache directory end with separator
     */
    public static String getAppDataDir(Context context) {
        File cacheDir = context.getCacheDir();
        if (cacheDir != null) return cacheDir.getParent() + File.separator;
        else return "/data/data/" + context.getPackageName() + File.separator;
    }

    /**
     * 检查SD卡中是否存在该文件
     *
     * @param filePath 不包含SD卡目录的文件路径
     * @return
     */
    public static boolean isSdCardFileExist(String filePath) {
        return isFileExist(getSdCardDir() + filePath);
    }

    /**
     * 检查手机内存中是否存在该文件
     *
     * @param context
     * @param filePath 不包含应用内存目录的文件路径
     * @return
     */
    public static boolean isAppDataFileExist(Context context, String filePath) {
        return isFileExist(getAppDataDir(context) + filePath);
    }

    /**
     * 检查文件是否存在
     *
     * @param filePath 全路径
     * @return
     */
    public static boolean isFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * 构建SD目录
     *
     * @param path 不包含SD卡目录的文件全路径
     * @return
     */
    public static boolean mkSdCardFileDirs(String path) {
        return mkFileDirs(getSdCardDir() + path);
    }

    /**
     * 构建手机存储文件路径
     *
     * @param context
     * @param path    不包含应用内存目录的文件全路径
     * @return
     */
    public static boolean mkAppDataDirs(Context context, String path) {
        return mkFileDirs(getAppDataDir(context) + path);
    }

    /**
     * 构建文件路径
     *
     * @param fullPath 完整路径
     */
    public static boolean mkFileDirs(String fullPath) {
        File file = new File(fullPath);
        if (!file.exists()) {
            return file.mkdirs();
        }
        return true;
    }

    /**
     * 保存图片到指定路径 要检查路径存在与否
     *
     * @param bmp
     * @param fullFilePath 全路径
     * @return
     */
    public static boolean writeBitmapToFullPath(Bitmap bmp, String fullFilePath) {
        if (TextUtils.isEmpty(fullFilePath)) return false;
        if (isFileExist(fullFilePath)) {
            deleteFile(fullFilePath);
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fullFilePath);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 从指定目录读取图片资源
     *
     * @param fullFilePath
     * @return
     */
    public static Bitmap readBitmapByFullPath(String fullFilePath) {
        try {
            if (isFileExist(fullFilePath)) {
                return BitmapFactory.decodeFile(fullFilePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean writeString(String fullFilePath, String content) {
        return writeBytes(fullFilePath, content.getBytes());
    }

    /**
     * 保存字节到指定目录 需要检查目录及文件存在与否
     *
     * @param fullFilePath
     * @param content
     * @return
     */
    public static boolean writeBytes(String fullFilePath, byte[] content) {
        return writeInputStream(fullFilePath, new ByteArrayInputStream(content));
    }

    /**
     * 将流的数据写入到文件
     *
     * @param toPath
     * @param is
     * @return
     */
    public static boolean writeInputStream(String toPath, InputStream is) {
        try {
            long startPos = 0;
            File file = new File(toPath + ".tmp");

            if (file.exists()) {
                if (file.isFile()) startPos = file.length();
                else file.delete();
            } else {
                file.createNewFile();
            }

            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(startPos);
            is.skip(startPos);

            byte[] temp = new byte[OPTIMIZATION_SIZE];
            int read = 0;
            while ((read = is.read(temp)) > 0) {
                raf.write(temp, 0, read);
            }
            raf.close();
            file.renameTo(new File(toPath));

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != is) is.close();
            } catch (IOException e) {
            }
        }
        return false;
    }

    public static byte[] readFile(String file) {
        return readFile(new File(file));
    }

    /**
     * 从文件中读取字节
     *
     * @param file
     * @return
     */
    public static byte[] readFile(File file) {
        try {
            return readInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从流中读取字节 会自动关闭流
     *
     * @param is
     * @return
     */
    public static byte[] readInputStream(InputStream is) {
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[OPTIMIZATION_SIZE];
            int length = -1;
            while ((length = is.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
            }
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
            }
            try {
                is.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    /**
     * 实现文件的拷贝 未对文件路径及合法性进行检查
     *
     * @param srcFile    源文件
     * @param targetFile 目标位置
     */
    public static void copyFile(File srcFile, File targetFile) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(srcFile));
            out = new BufferedOutputStream(new FileOutputStream(targetFile));
            byte[] b = new byte[OPTIMIZATION_SIZE];
            int len;
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException e) {
            }
            try {
                if (out != null) out.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 删除目标路径的文件
     *
     * @param path
     * @return
     */
    public static boolean deleteFile(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) return file.delete();
        return false;
    }

    /**
     * 获取文件路径对应的文件夹名
     *
     * @param filePath
     * @return
     */
    public static String getFolder(String filePath) {
        String[] a = filePath.split(File.separator);
        if (a.length <= 0) return null;
        int tL = filePath.length();
        int nL = a[a.length - 1].length();
        return filePath.substring(0, tL - nL);
    }

    /**
     * 解压文件
     *
     * @param fromFile
     * @param toDir
     * @return
     */
    public static boolean unzip(String fromFile, String toDir) {
        try {
            unzipFile(fromFile, toDir);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void unzipFile(String fromFile, String toDir) throws Exception {
        ZipFile zip = new ZipFile(fromFile);

        Enumeration e = zip.entries();
        while (e.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) e.nextElement();
            String entryName = entry.getName();
            String path = toDir + "/" + entryName;
            if (entry.isDirectory()) {
                File decompressDirFile = new File(path);
                if (!decompressDirFile.exists()) {
                    decompressDirFile.mkdirs();
                }
            } else {
                String fileDir = path.substring(0, path.lastIndexOf("/"));
                File fileDirFile = new File(fileDir);
                if (!fileDirFile.exists()) {
                    fileDirFile.mkdirs();
                }
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(toDir + "/" + entryName));
                BufferedInputStream bi = new BufferedInputStream(zip.getInputStream(entry));
                byte[] readContent = new byte[1024];
                int readCount = bi.read(readContent);
                while (readCount != -1) {
                    bos.write(readContent, 0, readCount);
                    readCount = bi.read(readContent);
                }
                bos.close();
            }
        }

        zip.close();
    }

}
