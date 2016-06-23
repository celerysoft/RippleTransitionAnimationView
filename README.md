# Ripple Transition Animation

## What is it?

Ripple Transition Animation is a transition animation for Android. You can use it as Activity( or Fragment) transition animation, make your App more material-design.

## Features

 * Beatify and smooth

 * Easily to add to you project

## Technical Information

* Required minimum API level: 14(Android 4.0)

* Supports all the screens sizes and density.

## Usage

### Step 1

#### Gradle

```
dependencies {
    compile 'com.celerysoft:rippletransitionanimationview:0.4.0'
}
```

### Step 2

Add the RippleTransitionAnimationViewGroup to your layout

if there is a FloatingActionButton in your layout like below

```
<android.support.design.widget.FloatingActionButton
    android:id="@+id/first_btn"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="bottom|end"
    android:layout_margin="16dp"
    android:src="@android:drawable/ic_media_play"/>
```

then change your layout like this

```
<com.celerysoft.rippletransitionanimationview.RippleTransitionAnimationViewGroup
    android:id="@+id/view_group_animation"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="bottom|end"
    android:layout_margin="16dp"
    app:animator_ripple_duration="animator_long"
    app:animator_ripple_color="@color/colorPrimary">
    <android.support.design.widget.FloatingActionButton
        android:id="@+id/first_btn"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|end"
        android:layout_margin="16dp"
        android:src="@android:drawable/ic_media_play"/>
</com.celerysoft.rippletransitionanimationview.RippleTransitionAnimationViewGroup>
```

### Step 3

Add some listener in the Activity( or Fragment) to make it work

```
@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_first);

    final RippleTransitionAnimationViewGroup animationViewGroup = (RippleTransitionAnimationViewGroup) findViewById(R.id.view_group_animation);
    animationViewGroup.setAnimatorListenerAdapter(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
            startActivity(intent);
            FirstActivity.this.finish();
            // It is important to override pending transition
            overridePendingTransition(R.anim.ripple_alpha_in, R.anim.ripple_alpha_out);
        }
    });

    final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.first_btn);
    if (fab != null) {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.hide();
                fab.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animationViewGroup.performAnimation();
                    }
                }, 150);
            }
        });

    }
}
```

### You can also see the demo to get started
[demo](https://github.com/celerysoft/RippleTransitionAnimationView/tree/master/demo).

## Screenshots

To be added.

## License

[MIT](./LICENSE)