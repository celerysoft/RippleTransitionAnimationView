package com.celerysoft.ripple.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.celerysoft.ripple.util.Util;

/**
 * Created by Celery on 16/5/19.
 *
 */
public class RippleInView extends RippleView {
    public RippleInView(Context context) {
        super(context);
    }

    public RippleInView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RippleInView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(mCenterX - mRadius, mCenterY - mRadius, mCenterX + mRadius, mCenterY + mRadius);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int result;

        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);

        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = getRadius() * 2;
        }

        return result;
    }

    private int measureHeight(int measureSpec) {
        int result;

        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);

        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = getRadius() * 2;
        }

        return result;
    }

    @Override
    protected Paint initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mRippleColor);

        return mPaint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mCenterX != -1 && mCenterY != -1) {
            canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        }
    }

    @Override
    protected float calculateScale() {
        float scale;

        int[] location = new int[2];
        this.getLocationInWindow(location);

        int screenWidth = Util.getScreenWidthPixels(getContext());
        int screenHeight = Util.getScreenHeightPixels(getContext());

        if (mCenterX == -1) {
            mCenterX = location[0] + (int) mInitialRadius;
        }
        if (mCenterY == -1) {
            mCenterY = location[1] + (int) mInitialRadius;
        }

        float distanceFromTopLeft = Util.calculateDistanceFromPointToPoint(mCenterX, mCenterY, 0, 0);
        float scaleTopLeft = distanceFromTopLeft / mInitialRadius;

        float distanceFromTopRight = Util.calculateDistanceFromPointToPoint(mCenterX, mCenterY, screenWidth, 0);
        float scaleTopRight = distanceFromTopRight / mInitialRadius;

        float distanceFromBottomLeft = Util.calculateDistanceFromPointToPoint(mCenterX, mCenterY, 0, screenHeight);
        float scaleBottomLeft = distanceFromBottomLeft / mInitialRadius;

        float distanceFromBottomRight = Util.calculateDistanceFromPointToPoint(mCenterX, mCenterY, screenWidth, screenHeight);
        float scaleBottomRight = distanceFromBottomRight / mInitialRadius;

        scale = Math.max(scaleTopLeft, scaleTopRight);
        scale = Math.max(scale, scaleBottomLeft);
        scale = Math.max(scale, scaleBottomRight);

        return scale;
    }
}
