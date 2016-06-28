package com.celerysoft.rippletransitionanimatiomviewdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.celerysoft.ripple.Wrapper;

/**
 * Created by Celery on 16/5/20.
 *
 */
public class SecondActivity extends AppCompatActivity {
    Wrapper mAnimationViewGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(R.anim.ripple_alpha_in, R.anim.ripple_alpha_out);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_second);


        mAnimationViewGroup = (Wrapper) findViewById(R.id.view_group_animation);
        mAnimationViewGroup.addAnimatorListenerAdapter(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
                startActivity(intent);
                SecondActivity.this.finish();
                overridePendingTransition(R.anim.ripple_alpha_in, R.anim.ripple_alpha_out);
            }
        });

        View btnReset = findViewById(R.id.second_btn);
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
