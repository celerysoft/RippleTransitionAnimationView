package com.celerysoft.rippletransitionanimatiomviewdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.celerysoft.rippletransitionanimationview.RippleTransitionAnimationViewGroup;

/**
 * Created by Celery on 16/5/26.
 *
 */
public class ThirdActivity extends AppCompatActivity {
    RippleTransitionAnimationViewGroup mAnimationViewGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(R.anim.ripple_alpha_in, R.anim.ripple_alpha_out);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_third);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Don't overflow parent");
        }


        mAnimationViewGroup = (RippleTransitionAnimationViewGroup) findViewById(R.id.view_group_animation);
        mAnimationViewGroup.setAnimatorListenerAdapter(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent(ThirdActivity.this, FirstActivity.class);
                startActivity(intent);
                ThirdActivity.this.finish();
                overridePendingTransition(R.anim.ripple_alpha_in, R.anim.ripple_alpha_out);
            }
        });

        View btnReset = findViewById(R.id.third_btn);
        if (btnReset != null) {
            btnReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAnimationViewGroup.performAnimation();
                }
            });
        }
    }
}
