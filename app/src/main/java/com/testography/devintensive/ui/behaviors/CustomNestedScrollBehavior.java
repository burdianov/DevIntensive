package com.testography.devintensive.ui.behaviors;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.testography.devintensive.R;
import com.testography.devintensive.utils.UiHelper;

public class CustomNestedScrollBehavior extends AppBarLayout.ScrollingViewBehavior {

    private final int mMaxAppbarHeight;
    private final int mMinAppbarHeight;
    private final int mMaxUserInfoHeight;

    public CustomNestedScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

        mMinAppbarHeight = UiHelper.getStatusBarHeight() + UiHelper.getActionBarHeight();
        mMaxAppbarHeight = context.getResources().getDimensionPixelSize(R.dimen
                .profile_image_size);
        mMaxUserInfoHeight = context.getResources().getDimensionPixelSize(R.dimen
                .user_info_size);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {

        float friction = UiHelper.currentFriction(mMinAppbarHeight,
                mMaxAppbarHeight, dependency.getBottom());
        int offsetY = UiHelper.lerp(mMaxUserInfoHeight / 2, mMaxUserInfoHeight, friction);

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)
                child.getLayoutParams();
        lp.topMargin = offsetY;
        child.setLayoutParams(lp);

        return super.onDependentViewChanged(parent, child, dependency);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
    }
}
