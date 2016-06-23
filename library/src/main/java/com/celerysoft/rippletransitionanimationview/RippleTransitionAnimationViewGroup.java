package com.celerysoft.rippletransitionanimationview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Celery on 16/5/18.
 *
 */
public class RippleTransitionAnimationViewGroup extends ViewGroup {
    private RippleTransitionAnimationView mRippleView;

    private int mCenterX = -1;
    private int mCenterY = -1;

    private int mInitialRadius = -1;

    private ViewGroup mRootView;
    private ViewGroup mParentView;

    public RippleTransitionAnimationViewGroup(Context context) {
        super(context);
    }

    public RippleTransitionAnimationViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RippleTransitionAnimationViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mRippleView = new RippleTransitionAnimationView(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mParentView = (ViewGroup) getRootView().findViewById(R.id.ripple_animation_parent);
        mRootView = (ViewGroup) ((ViewGroup) (getRootView().findViewById(android.R.id.content))).getChildAt(0);
        if (mParentView != null) {
            mParentView.addView(mRippleView);
        } else {
            mRootView.addView(mRippleView);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int measureWidth = 0;
        int measureHeight = 0;

        int childWidth;
        int childHeight;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; ++i) {
            View childView = getChildAt(i);

            childWidth = childView.getMeasuredWidth();
            childHeight = childView.getMeasuredHeight();

            measureWidth = measureWidth > childWidth ? measureWidth : childWidth;
            measureHeight = measureHeight > childHeight ? measureHeight : childHeight;
        }

        // MeasureSpec.AT_MOST means WRAP_CONTENT
        measureWidth = widthMode == MeasureSpec.AT_MOST ? measureWidth : widthSize;
        measureHeight = heightMode == MeasureSpec.AT_MOST ? measureHeight : heightSize;
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        calculateCenterOfRippleView();
        calculateInitialRadiusOfRippleView();

        int childViewCount = getChildCount();

        int centerX = (r - l) / 2;
        int centerY = (b - t) / 2;

        for (int i = 0; i < childViewCount; ++i) {
            View childView = getChildAt(i);

            int width = childView.getMeasuredWidth();
            int height = childView.getMeasuredHeight();

            int left = centerX - width / 2;
            int right = centerX + width / 2;
            int top = centerY - height / 2;
            int bottom = centerY + height / 2;

            childView.layout(left, top, right, bottom);
        }
    }

    private void calculateCenterOfRippleView() {
        if (mCenterX != -1 && mCenterY != -1) {
            return;
        }

        int[] location = new int[2];
        this.getLocationInWindow(location);

        if (mParentView != null) {
            /****************************************************************
             |--------------------------------------------------------------|
             |Status Bar                                                    |
             |--------------------------------------------------------------|
             |Root View                                                     |
             |      --------------------------------------------------------|
             |      |Parent View                                            |
             |      |                                                       |
             |      |                                                       |
             |      |          ------------------------------------         |
             |      |          |RippleTransitionAnimationViewGroup|         |
             |      |          ------------------------------------         |
             |      |                                                       |
             |      |                                                       |
             |--------------------------------------------------------------|
             ****************************************************************/
            int[] rootViewLocation = new int[2];
            mRootView.getLocationInWindow(rootViewLocation);

            int[] parentViewLocation = new int[2];
            mParentView.getLocationInWindow(parentViewLocation);

            if (mCenterX == -1) {
                mCenterX = location[0] + getMeasuredWidth() / 2 - parentViewLocation[0];
            }
            if (mCenterY == -1) {
                mCenterY = location[1] - parentViewLocation[1] + rootViewLocation[1];
            }

        } else {

            if (mCenterX == -1) {
                mCenterX = location[0] + getMeasuredWidth() / 2;
            }
            if (mCenterY == -1) {
                mCenterY = location[1];
            }

        }

        mRippleView.setCenterX(mCenterX);
        mRippleView.setCenterY(mCenterY);
    }

    private void calculateInitialRadiusOfRippleView() {
        if (mInitialRadius != -1) {
            return;
        }

        mInitialRadius = Math.min(getMeasuredWidth(), getMeasuredHeight());

        mRippleView.setRadius(mInitialRadius);
    }

    public void performAnimation() {
        mRippleView.performAnimation();
    }

    public void cancelAnimation() {
        mRippleView.cancelAnimation();
    }

    public void setRippleColor(int rippleColor) {
        mRippleView.setRippleColor(rippleColor);
    }

    public void setAnimatorDuration(int animatorDuration) {
        mRippleView.setAnimatorDuration(animatorDuration);
    }

    public void setAnimatorListener(Animator.AnimatorListener animatorListener) {
        mRippleView.setAnimatorListener(animatorListener);
    }

   public void setAnimatorListenerAdapter(AnimatorListenerAdapter animatorListenerAdapter) {
        mRippleView.setAnimatorListenerAdapter(animatorListenerAdapter);
    }
}
