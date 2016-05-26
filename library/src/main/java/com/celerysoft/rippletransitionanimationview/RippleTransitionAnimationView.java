package com.celerysoft.rippletransitionanimationview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by Celery on 16/5/19.
 *
 */
public class RippleTransitionAnimationView extends View {
    private static final int SHORT_DURATION_TIME = 0;
    private static final int MEDIUM_DURATION_TIME = 1;
    private static final int LONG_DURATION_TIME = 2;

    private Paint mPaint;

    private int mRippleColor;
    public void setRippleColor(int rippleColor) {
        mRippleColor = rippleColor;
        if (mPaint != null) {
            mPaint.setColor(rippleColor);
        }
    }

    private float mInitialRadius;
    private int mAnimatorDuration;
    public void setAnimatorDuration(int animatorDuration) {
        mAnimatorDuration = animatorDuration;
    }

    private int mCenterX = -1;
    protected int getCenterX() {
        return mCenterX;
    }
    protected void setCenterX(int centerX) {
        mCenterX = centerX;
        mScale = -1;
    }

    private int mCenterY = -1;
    protected int getCenterY() {
        return mCenterY;
    }
    protected void setCenterY(int centerY) {
        mCenterY = centerY;
        mScale = -1;
    }

    private int mRadius;
    public int getRadius() {
        return mRadius;
    }
    public void setRadius(int radius) {
        mRadius = radius;
    }

    ValueAnimator mValueAnimator;

    Animator.AnimatorListener mAnimatorListener;
    public Animator.AnimatorListener getAnimatorListener() {
        return mAnimatorListener;
    }
    public void setAnimatorListener(Animator.AnimatorListener animatorListener) {
        if (mAnimatorListener != null) {
            mValueAnimator.removeListener(mAnimatorListener);
        }

        mAnimatorListener = animatorListener;
        mValueAnimator.addListener(animatorListener);
    }

    AnimatorListenerAdapter mAnimatorListenerAdapter;
    public AnimatorListenerAdapter getAnimatorListenerAdapter() {
        return mAnimatorListenerAdapter;
    }
    public void setAnimatorListenerAdapter(AnimatorListenerAdapter animatorListenerAdapter) {
        if (mAnimatorListenerAdapter != null) {
            mValueAnimator.removeListener(mAnimatorListenerAdapter);
        }

        mAnimatorListenerAdapter = animatorListenerAdapter;
        mValueAnimator.addListener(animatorListenerAdapter);
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


        mValueAnimator = ValueAnimator.ofInt(1, 1000);
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RippleTransitionAnimationView.this.setVisibility(INVISIBLE);
                    }
                }, 300);
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mCenterX != -1 && mCenterY != -1) {
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
        super.layout(mCenterX - mRadius, mCenterY - mRadius, mCenterX + mRadius, mCenterY + mRadius);
    }

    protected void manualLayout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);
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

    private float calculateScale() {
        if (mScale != -1) {
            return mScale;
        }

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

        mScale = Math.max(scaleTopLeft, scaleTopRight);
        mScale = Math.max(mScale, scaleBottomLeft);
        mScale = Math.max(mScale, scaleBottomRight);

        return mScale;
    }

    private void calculateCenterOfRipple(int right, int bottom) {
        if (mCenterX == -1) {
            mCenterX = (int) (right - mInitialRadius);
        }
        if (mCenterY == -1) {
            mCenterY = (int) (bottom - mInitialRadius);
        }
    }

    public void performAnimation() {
        this.setVisibility(VISIBLE);

        final float scale = calculateScale();
        mValueAnimator.removeAllUpdateListeners();
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (Integer)animation.getAnimatedValue();

                float fraction = currentValue / 1000f;

                RippleTransitionAnimationView.this.setRadius(mEvaluator.evaluate(fraction, (int) (mInitialRadius), (int) (mInitialRadius * scale)));
                RippleTransitionAnimationView.this.requestLayout();
                RippleTransitionAnimationView.this.invalidate();
            }
        });
        mValueAnimator.setInterpolator(new AccelerateInterpolator());

        mValueAnimator.setDuration(mAnimatorDuration).start();
    }

    public void cancelAnimation() {
        mValueAnimator.cancel();
    }
}
