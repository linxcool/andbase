package com.linxcool.andbase.util.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UiUtils {

	public static int dp2px(Context context, int dp) {
		float density = context.getResources().getDisplayMetrics().density;
		return (int) (dp * density + 0.5f);
	}

	public static int px2dp(Context context, int px) {
		float density = context.getResources().getDisplayMetrics().density;
		return (int) (px / density + 0.5f);
	}

	public static boolean isLandscape(Activity activity) {
		int orient = activity.getRequestedOrientation();
		if (orient != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && orient != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			DisplayMetrics display = activity.getResources().getDisplayMetrics();
			int screenWidth = display.widthPixels;
			int screenHeight = display.heightPixels;
			orient = screenWidth < screenHeight ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
		}
		return orient == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
	}

}
