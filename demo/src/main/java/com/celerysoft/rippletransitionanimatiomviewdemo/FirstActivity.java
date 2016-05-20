package com.celerysoft.rippletransitionanimatiomviewdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.celerysoft.rippletransitionanimationview.RippleTransitionAnimationView;

/**
 * Created by Celery on 16/5/19.
 *
 */
public class FirstActivity extends AppCompatActivity {
    RippleTransitionAnimationView mBottomRightAnimationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_first);

        mBottomRightAnimationView = (RippleTransitionAnimationView) findViewById(R.id.view_br_animation);
        mBottomRightAnimationView.setAnimatorListenerAdapter(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.ripple_alpha_in, R.anim.ripple_alpha_out);
            }
        });

        View btnReset = findViewById(R.id.first_btn);
        if (btnReset != null) {
            btnReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBottomRightAnimationView.performAnimation();
                }
            });
        }
    }
}
