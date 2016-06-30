package com.celerysoft.ripple;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.celerysoft.ripple.view.RippleInView;
import com.celerysoft.ripple.view.RippleOutView;
import com.celerysoft.ripple.view.RippleView;
import com.celerysoft.rippletransitionanimationview.R;

/**
 * Created by Celery on 16/5/18.
 *
 */
public class Wrapper extends ViewGroup {
    /* Animation Type */
    public static final int FILL_IN = 0;
    public static final int WIPE_OUT = 1;

    private RippleView mRippleView;

    private int mCenterX = -1;
    private int mCenterY = -1;

    protected float mInitialRadius = -1;
    //protected int mRippleBackgroundColor;
    //protected int mRippleColor;
    //protected int mAnimatorDuration;
    protected boolean mAutoHide = true;

    private int mRippleType;

    private ViewGroup mRootView;
    private ViewGroup mParentView;

    public Wrapper(Context context) {
        super(context);
    }

    public Wrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Wrapper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Wrapper);
//        mRippleBackgroundColor = a.getColor(R.styleable.Wrapper_animator_ripple_background, getResources().getColor(android.R.color.darker_gray));
//        mRippleColor = a.getColor(R.styleable.Wrapper_animator_ripple_color, getResources().getColor(R.color.default_ripple_color));
//        mAnimatorDuration = a.getInt(R.styleable.Wrapper_animator_ripple_duration, Const.SHORT_DURATION_TIME);
        mInitialRadius = a.getDimension(R.styleable.Wrapper_animator_ripple_radius, getResources().getDimension(R.dimen.default_ripple_radius));
        mRippleType = a.getInt(R.styleable.Wrapper_animator_ripple_type, FILL_IN);
        mAutoHide = a.getBoolean(R.styleable.Wrapper_animator_ripple_auto_hide, true);
        a.recycle();

        if (mRippleType == FILL_IN) {
            mRippleView = new RippleInView(context, attrs);
        } else if (mRippleType == WIPE_OUT) {
            mRippleView = new RippleOutView(context, attrs);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (!isInEditMode()) {
            setupRippleView();
        }
    }

    private void setupRippleView() {
        mParentView = (ViewGroup) getRootView().findViewById(R.id.ripple_animation_parent);
        mRootView = (ViewGroup) ((ViewGroup) (getRootView().findViewById(android.R.id.content))).getChildAt(0);
        if (mParentView != null) {
            if (mParentView instanceof RelativeLayout) {
                if (mRippleView.getParent() == null) {
                    mParentView.addView(mRippleView, 0, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                }
            } else {
                mParentView.addView(mRippleView);
            }

            if (mRippleType == WIPE_OUT) {
                mParentView.bringChildToFront(this);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    mParentView.requestLayout();
                    mParentView.invalidate();
                }
//                mParentView.requestLayout();
//                mParentView.invalidate();
            }

            mParentView.post(new Runnable() {
                @Override
                public void run() {
                    mRippleView.setForcedWidth(mParentView.getWidth());
                    mRippleView.setForcedHeight(mParentView.getHeight());
                }
            });
        } else {
//            if (mRootView instanceof RelativeLayout) {
//                if (mRootView.getParent() == null) {
//                    mRootView.addView(mRippleView, 0, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//                }
//            } else {
//                mRootView.addView(mRippleView);
//            }
            mRootView.addView(mRippleView);

            if (mRippleType == WIPE_OUT) {
                mRootView.bringChildToFront(this);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    mRootView.requestLayout();
                    mRootView.invalidate();
                }
//                mRootView.requestLayout();
//                mRootView.invalidate();
            }

            mRootView.post(new Runnable() {
                @Override
                public void run() {
                    mRippleView.setForcedWidth(mRootView.getWidth());
                    mRippleView.setForcedHeight(mRootView.getHeight());
                }
            });
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
        if (getVisibility() == VISIBLE) {
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

        mInitialRadius = Math.min(getMeasuredWidth() / 2, getMeasuredHeight() / 2);

        mRippleView.setRadius((int) mInitialRadius);
    }

    private AnimatorListenerAdapter mAnimatorListenerAdapter;
    private void prePerformAnimation() {
        if (mAnimatorListenerAdapter == null) {
            mAnimatorListenerAdapter = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);

                    beforeAnimationStart();
                }
            };
            mRippleView.addAnimatorListenerAdapter(mAnimatorListenerAdapter);
        }
    }

    private void beforeAnimationStart() {
        ViewGroup v;
        if (mParentView != null) {
            v = mParentView;
        } else {
            v = mRootView;
        }
        if (v != null) {
            v.bringChildToFront(mRippleView);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                v.requestLayout();
                v.invalidate();
            }
        }

        if (mAutoHide) {
            setVisibility(GONE);
        }
    }

    public void performAnimation() {
        post(new Runnable() {
            @Override
            public void run() {
                prePerformAnimation();
                mRippleView.performAnimation();
            }
        });
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

    public void addAnimatorListener(Animator.AnimatorListener animatorListener) {
        mRippleView.addAnimatorListener(animatorListener);
    }

    public void removeAnimatorListener(Animator.AnimatorListener animatorListener) {
        mRippleView.removeAnimatorListener(animatorListener);
    }

    public void addAnimatorListenerAdapter(AnimatorListenerAdapter animatorListenerAdapter) {
       mRippleView.addAnimatorListenerAdapter(animatorListenerAdapter);
    }

    public void removeAnimatorListenerAdapter(AnimatorListenerAdapter animatorListenerAdapter) {
        mRippleView.removeAnimatorListenerAdapter(animatorListenerAdapter);
    }
}
