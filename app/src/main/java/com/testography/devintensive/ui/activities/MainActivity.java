package com.testography.devintensive.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.testography.devintensive.R;
import com.testography.devintensive.utils.ConstantManager;

public class MainActivity extends BaseActivity implements View
        .OnClickListener {

    public static final String TAG = ConstantManager.TAG_PREFIX + "Main Activity";

    private ImageView mCallImg;
    private CoordinatorLayout mCoordinatorLayout;
    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        mCallImg = (ImageView) findViewById(R.id.call_img);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_layout);

        mCallImg.setOnClickListener(this);

        if (savedInstanceState == null) {
            // running the activity for the first time
            showSnackbar("running the activity for the first time");
        } else {
            // running the activity for the first time
            showSnackbar("activity has already been created");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.call_img:
                showProgress();
                runWithDelay();
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void showSnackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void runWithDelay() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgress();
            }
        }, 3000);
    }
}
