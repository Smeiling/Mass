package com.sml.mass.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.sml.mass.R;
import com.sml.mass.components.basic.ZoomableImageView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZoomableImageViewActivity extends AppCompatActivity {
    public static final String TAG = ZoomableImageViewActivity.class.getSimpleName();

    private Switch zoomableSwitch;
    private ZoomableImageView zoomableImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoomable_image_view);
        setTitle("ZoomableImageView");

        zoomableImage = (ZoomableImageView) findViewById(R.id.zoomable_image);

        zoomableSwitch = (Switch) findViewById(R.id.zoomable_switch);
        zoomableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d(TAG, "zoomable = true");
                    zoomableImage.setZoomable(true);
                } else {
                    Log.d(TAG, "zoomable = false");
                    zoomableImage.setZoomable(false);
                }
            }
        });
    }
}
