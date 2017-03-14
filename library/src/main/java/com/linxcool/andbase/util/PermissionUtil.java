package com.linxcool.andbase.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import java.util.Locale;

/**
 * 权限对话框管理
 */
public class PermissionUtil {

    public static void showPermissionDialog(final Activity activity, String content) {
        new AlertDialog.Builder(activity)
                .setTitle(SystemUtil.isChinese() ? "提示" : "Note")
                .setMessage(content)
                .setPositiveButton(SystemUtil.isChinese() ? "去设置" : "Go Setting", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startSettingIntent(activity);
            }
        }).create().show();
    }

    /**
     * 启动app设置授权界面
     * @param context
     */
    public static void startSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(localIntent);
    }
}
