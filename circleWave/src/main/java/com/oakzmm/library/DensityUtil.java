package com.oakzmm.library;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;

public class DensityUtil {

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	public static float sp2px(Resources resources, float sp){
		final float scale = resources.getDisplayMetrics().scaledDensity;
		return sp * scale;
	}

	/**
	 * measure view
	 * 
	 * @param view
	 */
	public static void calcViewMeasure(View view) {
		int width = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int height = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		view.measure(width, height);
	}
}
