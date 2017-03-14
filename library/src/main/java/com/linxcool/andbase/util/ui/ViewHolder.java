package com.linxcool.andbase.util.ui;

import android.util.SparseArray;
import android.view.View;

/**
 * ListView或GridView视图缓存辅助工具类
 * @author 胡昌海(linxcool.hu)
 */
public class ViewHolder {

	/**
	 * 获取视图控件，例如：<pre>
	 *  if (convertView == null) {
	 *  	convertView = LayoutInflater.from(context).inflate(R.layout.xxx, parent, false);
	 *  }
	 *  ImageView iv = ViewHolder.get(convertView, R.id.yyy);
	 *  ....
	 * </pre>
	 * @param view convertView
	 * @param id viewId
	 * @param <T>
     * @return
     */
	@SuppressWarnings("unchecked")  
	public static <T extends View> T get(View view, int id) {  
		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();  
		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();  
			view.setTag(viewHolder);  
		}
		View childView = viewHolder.get(id);  
		if (childView == null) {  
			childView = view.findViewById(id);  
			viewHolder.put(id, childView);  
		}  
		return (T) childView;  
	} 
	
}
