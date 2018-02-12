package com.sml.mass.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sml.mass.R;
import com.sml.mass.components.basic.RadiusImageView;


/**
 * @Author: Smeiling
 * @Date: 2018-01-22
 * @Description: RadiusImageView控件示例
 */
public class RadiusImageViewActivity extends AppCompatActivity {

    private LinearLayout extraLayout;
    private RadiusImageView radiusImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radius_image_view);
        extraLayout = findViewById(R.id.extra);

        // 创建RadiusImageView实例
        radiusImageView = new RadiusImageView(this);
        // 设置ImageView类型
        radiusImageView.setShapeType(RadiusImageView.SHAPE_ROUND_RECT);
        // 圆角Radius值
        radiusImageView.setRoundRadius(36);
        // 边框设置
        radiusImageView.setBorderColor(Color.parseColor("#FF4081"));
        radiusImageView.setBorderWidth(12);
        // 设置图片
//        radiusImageView.setImageResource(R.drawable.img_2);
        // 添加点击事件
        radiusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "RadiusImageView Clicked!!!", Toast.LENGTH_SHORT).show();
                // 清除边框
                radiusImageView.clearBorder();
                radiusImageView.setImageResource(R.drawable.kitty);
            }
        });
        // 设置RadiusImageView大小
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(180, 180);
        radiusImageView.setLayoutParams(params);
        extraLayout.addView(radiusImageView);

        // 说明文字
        TextView textView = new TextView(this);
        textView.setText(R.string.radius_image_view_code);
        textView.setTextColor(Color.parseColor("#333333"));
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.leftMargin = 48;
        textView.setLayoutParams(textParams);
        extraLayout.addView(textView);
    }
}
