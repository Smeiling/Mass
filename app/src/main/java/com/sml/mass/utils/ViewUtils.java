package com.sml.mass.utils;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.sml.mass.R;

/**
 * @Author: Smeiling
 * @Date: 2018-02-12 11:30
 * @Description:
 */
public class ViewUtils {


    /**
     * 添加水波纹的效果
     *
     * @param view
     */
    public static void addRippleEffect(View view) {
        if (Build.VERSION.SDK_INT >= 23) {
            RippleDrawable ripple = new RippleDrawable(ColorStateList.valueOf(0x80999999),
                    null, new ColorDrawable(Color.WHITE));
            ripple.setRadius(RippleDrawable.RADIUS_AUTO);
            view.setForeground(ripple);
        } else if (Build.VERSION.SDK_INT >= 21) {
            if (view.getBackground() != null && !(view.getBackground() instanceof RippleDrawable)) {
                Drawable background = view.getBackground();
                ColorStateList color = ColorStateList.valueOf(0x80999999);
                background = new RippleDrawable(color, background,
                        new ColorDrawable(Color.WHITE));
                view.setBackground(background);
            } else {
                view.setBackground(ActivityCompat.getDrawable(view.getContext(), R.drawable.ripple_default));
            }
        }
    }


}
