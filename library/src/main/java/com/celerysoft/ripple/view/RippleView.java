package com.celerysoft.ripple.view;

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

import com.celerysoft.ripple.Animatable;
import com.celerysoft.ripple.R;
import com.celerysoft.ripple.util.Const;

/**
 * Created by Celery on 16/6/24.
 *
 */
public abstract class RippleView extends View implements Animatable {
    protected Paint mPaint;

    protected int mRippleBackgroundColor;
    public void setRippleBackgroundColor(int rippleBackgroundColor) {
        mRippleBackgroundColor = rippleBackgroundColor;
    }

    protected int mRippleColor;
    public void setRippleColor(int rippleColor) {
        mRippleColor = rippleColor;
        if (mPaint != null) {
            mPaint.setColor(rippleColor);
        }
    }

    protected float mInitialRadius;
    protected int mAnimatorDuration;
    public void setAnimatorDuration(int animatorDuration) {
        mAnimatorDuration = animatorDuration;
    }

    protected int mForcedWidth = -1;
    public int getForcedWidth() {
        return mForcedWidth;
    }
    public void setForcedWidth(int forcedWidth) {
        if (forcedWidth <= 0) {
            return;
        }
        mForcedWidth = forcedWidth;
    }

    protected int mForcedHeight = -1;
    public int getForcedHeight() {
        return mForcedHeight;
    }
    public void setForcedHeight(int forcedHeight) {
        if (forcedHeight <= 0) {
            return;
        }
        mForcedHeight = forcedHeight;
    }

    protected int mCenterX = -1;
    public int getCenterX() {
        return mCenterX;
    }
    public void setCenterX(int centerX) {
        mCenterX = centerX;
        mScale = -1;
    }

    protected int mCenterY = -1;
    public int getCenterY() {
        return mCenterY;
    }
    public void setCenterY(int centerY) {
        mCenterY = centerY;
        mScale = -1;
    }

    protected int mRadius;
    public int getRadius() {
        return mRadius;
    }
    public void setRadius(int radius) {
        mRadius = radius;
    }

    ValueAnimator mValueAnimator;

    private IntEvaluator mEvaluator = new IntEvaluator();
    private float mScale = -1;

    public RippleView(Context context) {
        super(context);
    }

    public RippleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public RippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    protected void initView(Context context, AttributeSet attrs) {
        setVisibility(INVISIBLE);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Wrapper);
        mRippleBackgroundColor = a.getColor(R.styleable.Wrapper_animator_ripple_background, getResources().getColor(android.R.color.darker_gray));
        mRippleColor = a.getColor(R.styleable.Wrapper_animator_ripple_color, getResources().getColor(R.color.default_ripple_color));
        mInitialRadius = a.getDimension(R.styleable.Wrapper_animator_ripple_radius, getResources().getDimension(R.dimen.default_ripple_radius));
        mAnimatorDuration = a.getInt(R.styleable.Wrapper_animator_ripple_duration, Const.SHORT_DURATION_TIME);
        a.recycle();

        mInitialRadius = mInitialRadius > 0 ? mInitialRadius : 1;
        mRadius = (int) mInitialRadius;

        switch (mAnimatorDuration) {
            case Const.SHORT_DURATION_TIME:
                mAnimatorDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
                break;
            case Const.MEDIUM_DURATION_TIME:
                mAnimatorDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
                break;
            case Const.LONG_DURATION_TIME:
                mAnimatorDuration = getResources().getInteger(android.R.integer.config_longAnimTime);
                break;
            default:
                break;
        }

        mPaint = initPaint();

        mValueAnimator = ValueAnimator.ofInt(1, 1000);
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RippleView.this.setVisibility(GONE);
                    }
                }, 300);
            }
        });
    }

    protected abstract Paint initPaint();

    protected abstract float calculateScale();

    /**
     * called before perform animation
     */
    protected void prePerformAnimation() {

    }

    @Override
    public void performAnimation() {
        prePerformAnimation();

        this.setVisibility(VISIBLE);

        if (mScale == -1) {
            mScale = calculateScale();
        }

        mValueAnimator.removeAllUpdateListeners();
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (Integer)animation.getAnimatedValue();

                float fraction = currentValue / 1000f;

                RippleView.this.setRadius(mEvaluator.evaluate(fraction, (int) (mInitialRadius), (int) (mInitialRadius * mScale)));
                RippleView.this.requestLayout();
                RippleView.this.invalidate();
            }
        });
        mValueAnimator.setInterpolator(new AccelerateInterpolator());

        mValueAnimator.setDuration(mAnimatorDuration).start();
    }

    public void cancelAnimation() {
        mValueAnimator.cancel();
    }

    @Override
    protected abstract void onDraw(Canvas canvas);

    public void addAnimatorListener(Animator.AnimatorListener animatorListener) {
        if (!mValueAnimator.getListeners().contains(animatorListener)) {
            mValueAnimator.addListener(animatorListener);
        }
    }
    public void removeAnimatorListener(Animator.AnimatorListener animatorListener) {
        mValueAnimator.removeListener(animatorListener);
    }

    public void addAnimatorListenerAdapter(AnimatorListenerAdapter animatorListenerAdapter) {
        if (!mValueAnimator.getListeners().contains(animatorListenerAdapter)) {
            mValueAnimator.addListener(animatorListenerAdapter);
        }
    }
    public void removeAnimatorListenerAdapter(AnimatorListenerAdapter animatorListenerAdapter) {
        mValueAnimator.removeListener(animatorListenerAdapter);
    }
}
