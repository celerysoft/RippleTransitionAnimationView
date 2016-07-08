package com.celerysoft.ripple.util;

import android.content.Context;

/**
 * Created by Celery on 16/5/19.
 * Common util
 */
public class Util {
    /**
     * 获取屏幕宽度，单位像素px
     * @param context Context class
     * @return screen width in pixels
     */
    public static int getScreenWidthPixels(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度，单位像素px
     * @param context Context class
     * @return screen height in pixels
     */
    public static int getScreenHeightPixels(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * Calculate the distance from point1(x1, y1) to point2(x2, y2)
     * @param x1 x coordinate of point 1
     * @param y1 y coordinate of point 1
     * @param x2 x coordinate of point 2
     * @param y2 y coordinate of point 2
     * @return distance
     */
    public static float calculateDistanceFromPointToPoint(int x1, int y1, int x2, int y2) {
        double length = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        return (float) length;
    }
}
