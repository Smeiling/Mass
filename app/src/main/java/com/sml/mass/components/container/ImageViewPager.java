package com.sml.mass.components.container;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * @Author: Smeiling
 * @Date: 2018-02-11 15:30
 * @Description:
 */
public class ImageViewPager extends ViewPager {
    public ImageViewPager(@NonNull Context context) {
        super(context);
    }

    public ImageViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}
