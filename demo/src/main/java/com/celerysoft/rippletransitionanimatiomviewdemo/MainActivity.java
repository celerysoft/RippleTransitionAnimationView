package com.celerysoft.rippletransitionanimatiomviewdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.celerysoft.rippletransitionanimationview.RippleTransitionAnimationView;

/**
 * Created by admin on 16/5/19.
 */
public class MainActivity extends AppCompatActivity {
    RippleTransitionAnimationView mAnimationView;
    RippleTransitionAnimationView mBottomRightAnimationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mAnimationView = (RippleTransitionAnimationView) findViewById(R.id.view_animation);
        mBottomRightAnimationView = (RippleTransitionAnimationView) findViewById(R.id.view_br_animation);
        View btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomRightAnimationView.setVisibility(View.INVISIBLE);
                mAnimationView.setVisibility(View.VISIBLE);
                mAnimationView.performAnimation();
            }
        });

        View btnReset = findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAnimationView.setVisibility(View.INVISIBLE);
                mBottomRightAnimationView.setVisibility(View.VISIBLE);
                mBottomRightAnimationView.performAnimation();
            }
        });
    }
}
