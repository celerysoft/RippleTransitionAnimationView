package com.celerysoft.rippletransitionanimatiomviewdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
    Wrapper mFabRippleWrapper;

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
        mFabRippleWrapper = (Wrapper) findViewById(R.id.fab_ripple_wrapper);
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
}
