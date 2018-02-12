package com.sml.mass.activity;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sml.mass.R;
import com.sml.mass.components.basic.FolderTextView;
import com.sml.mass.utils.ViewUtils;

public class FolderTextViewActivity extends AppCompatActivity {

    private TextView span;
    private FolderTextView folderTextView;
    private Button addImageSpan;

    private SpannableString spannableString;
    private TagSpan imageSpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_text_view);

        span = findViewById(R.id.span);
        folderTextView = findViewById(R.id.folder);
        addImageSpan = findViewById(R.id.add_image_span);

        spannableString = new SpannableString("TAG" + span.getText());
        Drawable drawable = getResources().getDrawable(R.mipmap.label);
        drawable.setBounds(0, 0, 168, 48);
        imageSpan = new TagSpan(drawable, DynamicDrawableSpan.ALIGN_BOTTOM);
        spannableString.setSpan(imageSpan, 0, 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        span.setText(spannableString);

        addImageSpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.addRippleEffect(v);
                if (imageSpan != null && folderTextView != null) {
                    folderTextView.setSpan(imageSpan, 0, 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
        });
    }

    /**
     * 使用Spannable在TextView中添加图片标签时
     * 解决TextView设置LineSpacing时图标与文字底部不对齐问题
     */
    public class TagSpan extends ImageSpan {

        public TagSpan(Drawable d) {
            super(d);
        }

        public TagSpan(Drawable d, int verticalAlignment) {
            super(d, verticalAlignment);
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            // image to draw
            Drawable b = getDrawable();

            // font metrics of text to be replaced
            Paint.FontMetricsInt fm = paint.getFontMetricsInt();
            int transY = (y + fm.descent + y + fm.ascent) / 2 - b.getBounds().bottom / 2;

            canvas.save();
            canvas.translate(x, transY);
            b.draw(canvas);
            canvas.restore();
        }
    }

}
