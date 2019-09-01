package com.mkorp.newsboard.ui;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomPager extends ViewPager {

    private boolean enabled;

    public CustomPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (this.enabled)
            return super.onTouchEvent(ev);
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.enabled)
            return super.onInterceptTouchEvent(ev);
        return false;
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
