package com.sml.mass.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.sml.mass.R;
import com.sml.mass.components.collections.VerticalViewPager;

public class VerticalViewPagerActivity extends AppCompatActivity {

    private VerticalViewPager verticalViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_view_pager);

        verticalViewPager = findViewById(R.id.vertical_view_pager);
        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter();
        verticalViewPager.setAdapter(imagePagerAdapter);
    }

    private class ImagePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 10;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            FrameLayout frameLayout = new FrameLayout(container.getContext());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            frameLayout.setLayoutParams(params);

            ImageView imageView = new ImageView(container.getContext());
            imageView.setImageResource(R.drawable.image_001);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            frameLayout.addView(imageView);
            container.addView(frameLayout);
            return frameLayout;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//            super.destroyItem(container, position, object);
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

}
