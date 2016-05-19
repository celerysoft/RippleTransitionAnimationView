package com.celerysoft.rippletransitionanimationview;

import android.animation.AnimatorSet;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by Celery on 16/5/18.
 */
public class RippleTransitionAnimationView extends ViewGroup {
    private static final int SHORT_DURATION_TIME = 0;
    private static final int MEDIUM_DURATION_TIME = 1;
    private static final int LONG_DURATION_TIME = 2;

    private Paint mPaint;

    private int mRippleColor;
    private float mRippleRadius;
    private int mAnimatorDuration;

    private AnimatorSet mAnimatorSet;

    private IntEvaluator mEvaluator = new IntEvaluator();
    private float mScale = -1;
    private RippleView mRippleView;

    public RippleTransitionAnimationView(Context context) {
        super(context);
    }

    public RippleTransitionAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RippleTransitionAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RippleTransitionAnimationView);
        mRippleColor = a.getColor(R.styleable.RippleTransitionAnimationView_animator_ripple_color, getResources().getColor(R.color.default_ripple_color));
        mRippleRadius = a.getDimension(R.styleable.RippleTransitionAnimationView_animator_ripple_radius, getResources().getDimension(R.dimen.default_ripple_radius));
        mAnimatorDuration = a.getInt(R.styleable.RippleTransitionAnimationView_animator_ripple_duration, SHORT_DURATION_TIME);
        a.recycle();

        mRippleRadius = mRippleRadius > 0 ? mRippleRadius : 1;

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

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());

        mRippleView = new RippleView(getContext());
        mRippleView.setVisibility(INVISIBLE);
        LayoutParams layoutParams = new LayoutParams((int) (2 * mRippleRadius), (int) (2 * mRippleRadius));
        addView(mRippleView, layoutParams);
    }

    public void performAnimation() {
        mRippleView.setVisibility(VISIBLE);

        final float scale = calculateScale();

        ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (Integer)animation.getAnimatedValue();

                float fraction = currentValue / 1000f;

                mRippleView.setRadius(mEvaluator.evaluate(fraction, (int) (mRippleRadius), (int) (mRippleRadius * scale)));
                mRippleView.requestLayout();
                mRippleView.invalidate();
            }
        });

        valueAnimator.setDuration(mAnimatorDuration).start();
    }

    public void cancelAnimation() {
        mAnimatorSet.cancel();
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//
//        measureChildren(widthMeasureSpec, heightMeasureSpec);
//
//        int measureWidth;
//        int measureHeight;
//
//        int childWidth = 0;
//        int childHeight = 0;
//
//        int childCount = getChildCount();
//        for (int i = 0; i < childCount; ++i) {
//            View childView = getChildAt(0);
//
//            if (childView instanceof RippleView) {
//                childWidth = childView.getMeasuredWidth();
//                childHeight = childView.getMeasuredHeight();
//            }
//        }
//
//        measureWidth = childWidth;
//        measureHeight = childHeight;
//
//        // MeasureSpec.AT_MOST means WRAP_CONTENT
//        measureWidth = widthMode == MeasureSpec.AT_MOST ? measureWidth : widthSize;
//        measureHeight = heightMode == MeasureSpec.AT_MOST ? measureHeight : heightSize;
//        setMeasuredDimension(measureWidth, measureHeight);
//    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childViewCount = getChildCount();
        for (int i = 0; i < childViewCount; ++i) {
            RippleView childView = (RippleView) getChildAt(i);
            int left = (r - l) / 2 - childView.getRadius();
            int right =  left + 2 * childView.getRadius();
            int top =  (b - t) / 2 - childView.getRadius();
            int bottom = top + (2 * childView.getRadius());
            childView.layout(left, top, right, bottom);
        }
    }

    private float calculateScale() {
        if (mScale != -1) {
            return mScale;
        }

        int[] location = new int[2];
        mRippleView.getLocationInWindow(location);

        int screenWidth = Util.getScreenWidthPixels(getContext());
        int screenHeight = Util.getScreenHeightPixels(getContext());

        int x1 = location[0];
        int x2 = screenWidth - x1 - this.getWidth();
        float scaleX = Math.max(x1, x2) / mRippleRadius;

        int y1 = location[1];
        int y2 = screenHeight - y1 - this.getHeight();
        float scaleY = Math.max(y1, y2) / mRippleRadius;

        mScale = Math.max(scaleX, scaleY);

        return mScale;
    }

    private class RippleView extends View {
        private int mRadius;
        public int getRadius() {
            return mRadius;
        }
        public void setRadius(int radius) {
            mRadius = radius;
        }

        public RippleView(Context context) {
            super(context);

            mRadius = (int) mRippleRadius;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        }


    }
}
