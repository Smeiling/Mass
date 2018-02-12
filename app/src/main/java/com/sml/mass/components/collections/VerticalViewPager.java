package com.sml.mass.components.collections;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sml.mass.R;

/**
 * @Author: Smeiling
 * @Date: 2017-08-03 16:17
 * @Description:
 */

public class VerticalViewPager extends ViewPager {


    private boolean isVertical = false;

    public VerticalViewPager(Context context) {
        super(context);
        init();
    }

    public VerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs, 0);
        init();
    }

    public VerticalViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initAttrs(attrs, defStyle);
        init();
    }

    private void initAttrs(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.VerticalViewPager, defStyle, 0);
        isVertical = a.getBoolean(R.styleable.VerticalViewPager_isVertical, false);
        a.recycle();
    }

    private void init() {
        // The majority of the magic happens here
        setPageTransformer(false, new HorizontalVerticalPageTransformer());
        // The easiest way to get rid of the over scroll drawing that happens on the left and right
        setOverScrollMode(OVER_SCROLL_NEVER);
    }


    public boolean isVertical() {
        return isVertical;
    }

    public void setVertical(boolean vertical) {
        isVertical = vertical;
        init();
    }

    private class HorizontalVerticalPageTransformer implements PageTransformer {

        private static final float MIN_SCALE = 0.75f;

        @Override
        public void transformPage(View page, float position) {
            if (isVertical) {
                int pageHeight = page.getHeight();
                int pageWidth = page.getWidth();

                if (position < -1) { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    page.setAlpha(0);

                } else if (position <= 0) { // [-1,0]


                    // Fade the page out.
                    page.setAlpha(1 + position);

                    // Counteract the default slide transition
                    page.setTranslationX(pageWidth * -position);
//                    page.setTranslationY(0);
                    page.setTranslationY(0);
                    // Scale the page down (between MIN_SCALE and 1)
                    float scaleFactor = MIN_SCALE
                            + (1 - MIN_SCALE) * (1 - Math.abs(position));
                    page.setScaleX(scaleFactor);
                    page.setScaleY(scaleFactor);


                } else if (position <= 1) { // (0,1]

                    // Use the default slide transition when moving to the left page
                    page.setAlpha(1);
                    page.setTranslationX(pageWidth * -position);
                    page.setTranslationY(pageHeight * position);
                    page.setScaleX(1);
                    page.setScaleY(1);


                } else { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    page.setAlpha(0);
                }

            } else {
                int pageWidth = page.getWidth();

                if (position < -1) { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    page.setAlpha(0);

                } else if (position <= 0) { // [-1,0]
                    // Use the default slide transition when moving to the left page
                    page.setAlpha(1);
                    page.setTranslationX(0);
                    page.setScaleX(1);
                    page.setScaleY(1);

                } else if (position <= 1) { // (0,1]
                    // Fade the page out.
                    page.setAlpha(1 - position);

                    // Counteract the default slide transition
                    page.setTranslationX(pageWidth * -position);
                    page.setTranslationY(0);

                    // Scale the page down (between MIN_SCALE and 1)
                    float scaleFactor = MIN_SCALE
                            + (1 - MIN_SCALE) * (1 - Math.abs(position));
                    page.setScaleX(scaleFactor);
                    page.setScaleY(scaleFactor);

                } else { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    page.setAlpha(0);
                }
            }
        }
    }

    /**
     * Swaps the X and Y coordinates of your touch event.
     */
    private MotionEvent swapXY(MotionEvent ev) {
        float width = getWidth();
        float height = getHeight();

        float newX = (ev.getY() / height) * width;
        float newY = (ev.getX() / width) * height;

        ev.setLocation(newX, newY);

        return ev;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isVertical) {
            boolean intercepted = super.onInterceptTouchEvent(swapXY(ev));
            swapXY(ev); // return touch coordinates to original reference frame for any child views
            return intercepted;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isVertical) {
            return super.onTouchEvent(swapXY(ev));
        } else {
            return super.onTouchEvent(ev);
        }
    }


}
