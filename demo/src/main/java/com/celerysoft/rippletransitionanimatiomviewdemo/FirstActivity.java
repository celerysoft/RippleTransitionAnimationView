package com.celerysoft.rippletransitionanimatiomviewdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.celerysoft.ripple.Wrapper;
import com.celerysoft.ripple.view.RippleInView;

/**
 * Created by Celery on 16/5/19.
 *
 */
public class FirstActivity extends AppCompatActivity {
    ProgressBar mProgressBar;
    Wrapper mRippleWrapper;
    FloatingActionButton mFloatingActionButton;
    RippleInView mFabRippleWrapper;

    private long mLastPressBackTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(R.anim.ripple_alpha_in, R.anim.ripple_alpha_out);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_first);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("1. First Activity");
        }

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mRippleWrapper = (Wrapper) findViewById(R.id.ripple_wrapper);
        mRippleWrapper.addAnimatorListenerAdapter(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

            }
        });

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.first_btn);
        if (mFloatingActionButton != null) {
            mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFloatingActionButton.hide();
                    mFloatingActionButton.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mFabRippleWrapper.performAnimation();
                        }
                    }, 100);
                }
            });
        }
        mFabRippleWrapper = (RippleInView) findViewById(R.id.fab_ripple_animator);
        mFabRippleWrapper.addAnimatorListenerAdapter(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
                startActivity(intent);
                FirstActivity.this.finish();
                overridePendingTransition(R.anim.ripple_alpha_in, R.anim.ripple_alpha_out);
            }
        });

        simulateLoadData();
//        mRippleWrapper.performAnimation();
    }

    private void simulateLoadData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // after loading data
                mRippleWrapper.performAnimation();
            }
        }, 3000);
    }

    @Override
    public void onBackPressed() {
        if (readyToExitApp()) {
            exitApp();
        } else {
            preExitApp();
        }
    }

    public void preExitApp() {
        showExitAppSnackBar();
        mLastPressBackTime = System.currentTimeMillis();
    }

    public void exitApp() {
        this.finish();
    }

    public boolean readyToExitApp() {
        return System.currentTimeMillis() - mLastPressBackTime <= 2000;
    }

    public void showExitAppSnackBar() {
        String text = "Tap back again to exit the demo";
        Snackbar.make(mFloatingActionButton, text, Snackbar.LENGTH_SHORT).show();
    }
}
