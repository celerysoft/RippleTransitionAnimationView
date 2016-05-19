package com.celerysoft.rippletransitionanimationview;

import android.content.Context;

/**
 * Created by admin on 16/5/19.
 */
public class Util {
    /**
     * 获取屏幕宽度，单位像素px
     */
    public static int getScreenWidthPixels(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度，单位像素px
     */
    public static int getScreenHeightPixels(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}
