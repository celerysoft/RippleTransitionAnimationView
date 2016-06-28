package com.celerysoft.ripple.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;

import com.celerysoft.ripple.util.Const;
import com.celerysoft.ripple.util.Util;
import com.celerysoft.rippletransitionanimationview.R;

/**
 * Created by Celery on 16/6/24.
 *
 */
public class RippleOutView extends RippleView {
    private Bitmap mBitmap;
    private Canvas mCanvas;

    private float mScale = -1;

    public RippleOutView(Context context) {
        super(context);
    }

    public RippleOutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initView(Context context, AttributeSet attrs) {
        super.initView(context, attrs);

        setVisibility(VISIBLE);
        mRippleBackgroundColor = mRippleBackgroundColor == -1 ? 0xFFFFFFFF : mRippleBackgroundColor;

        mInitialRadius = 0.01f;
        mRadius = (int) mInitialRadius;
    }

    @Override
    protected Paint initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setAlpha(0);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        return mPaint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mCenterX != -1 && mCenterY != -1 && mCanvas != null) {
            mCanvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
        }

        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }

    }

    /**
     *
     * @param colorARGB should like 0x8800ff00
     * @param width
     * @param height
     * @return
     */
    private Bitmap createBitmapFromARGB(int colorARGB, int width, int height) {
//        String a = "";
//        colorARGB |= 0xFF000000;
//        colorARGB = colorARGB | 0xFF000000;

        int[] argb = new int[width * height];

        for (int i = 0; i < argb.length; i++) {

            argb[i] = colorARGB;

        }
        return Bitmap.createBitmap(argb, width, height, Bitmap.Config.ARGB_8888);
    }

    /**
     *
     * @param bm
     * @param alpha ,alpha should be between 0 and 255
     * @note set bitmap's alpha
     * @return
     */
    private Bitmap setBitmapAlpha(Bitmap bm, int alpha) {
        int[] argb = new int[bm.getWidth() * bm.getHeight()];
        bm.getPixels(argb, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm
                .getHeight());

        for (int i = 0; i < argb.length; i++) {

            argb[i] = ((alpha << 24) | (argb[i] & 0x00FFFFFF));
        }
        return Bitmap.createBitmap(argb, bm.getWidth(), bm.getHeight(),
                Bitmap.Config.ARGB_8888);
    }

    /**
     *
     * @param bm
     * @note set cover bitmap , which  overlay on background.
     */
    private void setCoverBitmap(Bitmap bm) {
        // converting bitmap into mutable bitmap
        mBitmap = Bitmap.createBitmap(mForcedWidth, mForcedHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas();
        mCanvas.setBitmap(mBitmap);
        // drawXY will result on that Bitmap
        // be sure parameter is bm, not mBitmap
        mCanvas.drawBitmap(bm, 0, 0, null);
    }

    @Override
    protected float calculateScale() {
        if (mScale != -1) {
            return mScale;
        }

        float distanceFromTopLeft = Util.calculateDistanceFromPointToPoint(mCenterX, mCenterY, 0, 0);
        float scaleTopLeft = distanceFromTopLeft / mInitialRadius;

        float distanceFromTopRight = Util.calculateDistanceFromPointToPoint(mCenterX, mCenterY, getWidth(), 0);
        float scaleTopRight = distanceFromTopRight / mInitialRadius;

        float distanceFromBottomLeft = Util.calculateDistanceFromPointToPoint(mCenterX, mCenterY, 0, getHeight());
        float scaleBottomLeft = distanceFromBottomLeft / mInitialRadius;

        float distanceFromBottomRight = Util.calculateDistanceFromPointToPoint(mCenterX, mCenterY, getWidth(), getHeight());
        float scaleBottomRight = distanceFromBottomRight / mInitialRadius;

        mScale = Math.max(scaleTopLeft, scaleTopRight);
        mScale = Math.max(mScale, scaleBottomLeft);
        mScale = Math.max(mScale, scaleBottomRight);

        return mScale;
    }

    @Override
    public void setForcedWidth(int forcedWidth) {
        super.setForcedWidth(forcedWidth);

        initCoverBitmap();
    }

    @Override
    public void setForcedHeight(int forcedHeight) {
        super.setForcedHeight(forcedHeight);

        initCoverBitmap();
    }

    private void initCoverBitmap() {
        if (mForcedWidth != -1 && mForcedHeight != -1) {
            //setBackgroundColor(mRippleBackgroundColor);
            setBackgroundColor(0x00FFFFFF);
            // 1.if cover is a image,you can open MENU_ITEM_COMMENT bellow
            Bitmap bm = createBitmapFromARGB(mRippleBackgroundColor, mForcedWidth, mForcedHeight);
            // if you want to set cover image's alpha,you can open MENU_ITEM_COMMENT bellow
            bm = setBitmapAlpha(bm, 255);
            // if you want to scale cover image,you can open MENU_ITEM_COMMENT bellow
            //bm = scaleBitmapFillScreen(bm);

            // 2.if cover is color
            //Bitmap bm = createBitmapFromARGB(0x8800ff00, SCREEN_W, SCREEN_H);
            setCoverBitmap(bm);
        }
    }

    @Override
    protected void prePerformAnimation() {
        initCoverBitmap();
    }
}
