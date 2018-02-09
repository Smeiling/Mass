package com.sml.mass.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.sml.mass.R;
import com.sml.mass.components.basic.ZoomableImageView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZoomableImageViewActivity extends AppCompatActivity {

    private static final String TAG = ZoomableImageViewActivity.class.getSimpleName();

    private ZoomableImageView zoomableImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoomable_image_view);

        zoomableImageView = findViewById(R.id.image);

        setActions(zoomableImageView);
    }


    private void setActions(ZoomableImageView zoomableImageView) {


    }
}
