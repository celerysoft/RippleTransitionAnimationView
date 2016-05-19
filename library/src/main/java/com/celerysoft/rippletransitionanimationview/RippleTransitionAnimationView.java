package com.celerysoft.rippletransitionanimationview;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by Celery on 16/5/19.
 */
public class RippleTransitionAnimationView extends View {
    private static final int SHORT_DURATION_TIME = 0;
    private static final int MEDIUM_DURATION_TIME = 1;
    private static final int LONG_DURATION_TIME = 2;

    private Paint mPaint;

    private int mRippleColor;
    private float mInitialRadius;
    private int mAnimatorDuration;

    private int centerX = -1;
    private int centerY = -1;

    private int mRadius;
    public int getRadius() {
        return mRadius;
    }
    public void setRadius(int radius) {
        mRadius = radius;
    }

    private IntEvaluator mEvaluator = new IntEvaluator();
    private float mScale = -1;

    public RippleTransitionAnimationView(Context context) {
        super(context);
    }

    public RippleTransitionAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public RippleTransitionAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        this.setVisibility(INVISIBLE);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RippleTransitionAnimationView);
        mRippleColor = a.getColor(R.styleable.RippleTransitionAnimationView_animator_ripple_color, getResources().getColor(R.color.default_ripple_color));
        mInitialRadius = a.getDimension(R.styleable.RippleTransitionAnimationView_animator_ripple_radius, getResources().getDimension(R.dimen.default_ripple_radius));
        mAnimatorDuration = a.getInt(R.styleable.RippleTransitionAnimationView_animator_ripple_duration, SHORT_DURATION_TIME);
        a.recycle();

        mInitialRadius = mInitialRadius > 0 ? mInitialRadius : 1;
        mRadius = (int) mInitialRadius;

        switch (mAnimatorDuration) {
            case SHORT_DURATION_TIME:
                mAnimatorDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
                break;
            case MEDIUM_DURATION_TIME:
                mAnimatorDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
                break;
            case LONG_DURATION_TIME:
                mAnimatorDuration = getResources().getInteger(android.R.integer.config_longAnimTime);
                break;
            default:
                break;
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mRippleColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (centerX != -1 && centerY != -1) {
            canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        calculateCenterOfRipple(right, bottom);
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        calculateCenterOfRipple(r, b);
        super.layout(centerX - mRadius, centerY - mRadius, centerX + mRadius, centerY + mRadius);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int result;

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = getRadius() * 2;
        }

        return result;
    }

    private int measureHeight(int measureSpec) {
        int result;

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = getRadius() * 2;
        }

        return result;
    }

    public void performAnimation() {
        this.setVisibility(VISIBLE);

        final float scale = calculateScale();

        ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (Integer)animation.getAnimatedValue();

                float fraction = currentValue / 1000f;

                RippleTransitionAnimationView.this.setRadius(mEvaluator.evaluate(fraction, (int) (mInitialRadius), (int) (mInitialRadius * scale)));
                RippleTransitionAnimationView.this.requestLayout();
                RippleTransitionAnimationView.this.invalidate();
            }
        });

        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.setDuration(mAnimatorDuration).start();
    }

    private float calculateScale() {
        if (mScale != -1) {
            return mScale;
        }

        int[] location = new int[2];
        this.getLocationInWindow(location);

        int screenWidth = Util.getScreenWidthPixels(getContext());
        int screenHeight = Util.getScreenHeightPixels(getContext());

        if (centerX == -1) {
            centerX = location[0] + (int) mInitialRadius;
        }
        if (centerY == -1) {
            centerY = location[1] + (int) mInitialRadius;
        }

        float distanceFromTopLeft = calculateDistanceFromPointToPoint(centerX, centerY, 0, 0);
        float scaleTopLeft = distanceFromTopLeft / mInitialRadius;

        float distanceFromTopRight = calculateDistanceFromPointToPoint(centerX, centerY, screenWidth, 0);
        float scaleTopRight = distanceFromTopRight / mInitialRadius;

        float distanceFromBottomLeft = calculateDistanceFromPointToPoint(centerX, centerY, 0, screenHeight);
        float scaleBottomLeft = distanceFromBottomLeft / mInitialRadius;

        float distanceFromBottomRight = calculateDistanceFromPointToPoint(centerX, centerY, screenWidth, screenHeight);
        float scaleBottomRight = distanceFromBottomRight / mInitialRadius;

        mScale = Math.max(scaleTopLeft, scaleTopRight);
        mScale = Math.max(mScale, scaleBottomLeft);
        mScale = Math.max(mScale, scaleBottomRight);

        return mScale;
    }

    private float calculateDistanceFromPointToPoint(int x1, int y1, int x2, int y2) {
        double length = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        return (float) length;
    }

    private void calculateCenterOfRipple(int right, int bottom) {
        if (centerX == -1) {
            centerX = (int) (right - mInitialRadius);
        }
        if (centerY == -1) {
            centerY = (int) (bottom - mInitialRadius);
        }
    }
}
