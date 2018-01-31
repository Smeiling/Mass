package com.sml.mass.components.list;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * @Author: Smeiling
 * @Date: 2018-01-31 18:00
 * @Description: 解决Scroll嵌套时只显示一行的问题
 */
public class MeasuredExpandableListView extends ExpandableListView {
    public MeasuredExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
