package com.linxcool.andbase.util.ui;

import java.lang.reflect.Field;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 布局缩放重构工具
 * @author 胡昌海(linxcool.hu)
 */
public class RelayoutUtil {

	/**
	 * 将原视图 宽高、padding、margin及文本字体大小按比例缩放重新布局
	 * @param view 单个视图，或视图层级
	 * @param scale 缩放比例
	 */
	public static void relayoutViewHierarchy(View view, float scale) {
		if (view == null) {
			return;
		}
		scaleView(view, scale);
		if (view instanceof ViewGroup) {
			View[] children = null;
			try {
				Field field = ViewGroup.class.getDeclaredField("mChildren");
				field.setAccessible(true);
				children = (View[]) field.get(view);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (children != null) {
				for (View child : children) {
					relayoutViewHierarchy(child, scale);
				}
			}
		}
	}

	/**
	 * 将视图按比例缩放，不考虑嵌套视图；
	 * @param view 不考虑嵌套，缩放单个View；
	 * @param scale 缩放比例；
	 */
	private static void scaleView(View view, float scale) {
		if (view instanceof TextView) {
			TextView convertView = (TextView) view;
			resetTextSize(convertView, scale);
			resetTextDrawable(convertView, scale);
		}

		if (view instanceof ImageView) {
			resetImageViewDrawable((ImageView) view, scale);
		}

		int pLeft = convertFloatToInt(view.getPaddingLeft() * scale);
		int pTop = convertFloatToInt(view.getPaddingTop() * scale);
		int pRight = convertFloatToInt(view.getPaddingRight() * scale);
		int pBottom = convertFloatToInt(view.getPaddingBottom() * scale);
		view.setPadding(pLeft, pTop, pRight, pBottom);

		LayoutParams params = view.getLayoutParams();
		scaleLayoutParams(params, scale);
	}

	/**
	 * 将视图布局属性按比例设置；
	 * @param params
	 * @param scale 缩放比例；
	 */
	public static void scaleLayoutParams(LayoutParams params, float scale) {
		if (params == null) {
			return;
		}
		if (params.width > 0) {
			params.width = convertFloatToInt(params.width * scale);
		}
		if (params.height > 0) {
			params.height = convertFloatToInt(params.height * scale);
		}

		if (params instanceof MarginLayoutParams) {
			MarginLayoutParams mParams = (MarginLayoutParams) params;
			if (mParams.leftMargin > 0) {
				mParams.leftMargin = convertFloatToInt(mParams.leftMargin * scale);
			}
			if (mParams.rightMargin > 0) {
				mParams.rightMargin = convertFloatToInt(mParams.rightMargin * scale);
			}
			if (mParams.topMargin > 0) {
				mParams.topMargin = convertFloatToInt(mParams.topMargin * scale);
			}
			if (mParams.bottomMargin > 0) {
				mParams.bottomMargin = convertFloatToInt(mParams.bottomMargin * scale);
			}
		}
	}

	/**
	 * 将 TextView（或其子类）文本大小按比例缩放；
	 * @param textView
	 * @param scale 缩放比例；
	 */
	public static void resetTextSize(TextView textView, float scale) {
		float size = textView.getTextSize();
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size * scale);
	}

	public static void resetTextDrawable(TextView textView, float scale) {
		int drawablePadding = convertFloatToInt(textView.getCompoundDrawablePadding() * scale);
		textView.setCompoundDrawablePadding(drawablePadding);

		Drawable[] drawables = textView.getCompoundDrawables();
		if (drawables == null || drawables.length < 4) {
			return;
		}

		for (Drawable drawable : drawables) {
			if (drawable != null) {
				int width = convertFloatToInt(drawable.getIntrinsicWidth() * scale);
				int height = convertFloatToInt(drawable.getIntrinsicHeight() * scale);
				if (width > 0 && height > 0) {
					drawable.setBounds(0, 0, width, height);
				}
			}
		}

		textView.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
	}

	public static void resetImageViewDrawable(ImageView imageView, float scale) {
		Drawable imgDrawable = imageView.getDrawable();
		if (imgDrawable != null) {
			int width = imgDrawable.getIntrinsicWidth();
			int height = imgDrawable.getIntrinsicHeight();
			if (width > 0 && height > 0) {
				Bitmap oldbmp = drawableToBitmap(imgDrawable);
				Matrix matrix = new Matrix();
				matrix.postScale(scale, scale);
				Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);
				imageView.setImageBitmap(newbmp);
			}
		}
	}

	/**
	 * float转换至int小数四舍五入
	 * @param sourceNum
	 * @return
	 */
	private static int convertFloatToInt(float sourceNum) {
		return (int) (sourceNum + 0.5f);
	}

	private static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565; // 取drawable的颜色格式
		Bitmap bitmap = Bitmap.createBitmap(width, height, config);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

}
