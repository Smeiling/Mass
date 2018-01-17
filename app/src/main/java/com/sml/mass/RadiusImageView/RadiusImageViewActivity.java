package com.sml.mass.RadiusImageView;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sml.mass.R;

public class RadiusImageViewActivity extends AppCompatActivity {

    private RadiusImageView custom;
    private Button btn;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radius_image_view);
        custom = findViewById(R.id.custom);
        btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (count) {
                    case 0:
                        custom.setBorderWidth(6);
                        custom.setBorderColor(Color.parseColor("#FF0000"));
                        btn.setText("setBorderWidth(6)\nsetBorderColor(Color.parseColor(\"#FF0000\"))");
                        break;
                    case 1:
                        custom.setShapeType(RadiusImageView.SHAPE_OVAL);
                        btn.setText("setShapeType(RadiusImageView.SHAPE_OVAL)");
                        break;
                    case 2:
                        custom.setShapeType(RadiusImageView.SHAPE_ROUND_RECT);
                        btn.setText("setShapeType(RadiusImageView.SHAPE_ROUND_RECT)");
                        break;
                    case 3:
                        custom.setLeftTopRadius(12);
                        btn.setText("setLeftTopRadius(12)");
                        break;
                    case 4:
                        custom.setLeftBottomRadius(24);
                        btn.setText("setLeftBottomRadius(24)");
                        break;
                    case 5:
                        custom.setRightBottomRadius(36);
                        btn.setText("setRightBottomRadius(36)");
                        break;
                    case 6:
                        custom.setRightTopRadius(48);
                        btn.setText("setRightTopRadius(48)");
                        break;
                    case 7:
                        custom.setRoundRadius(12);
                        btn.setText("setRoundRadius(12)");
                        break;
                    case 8:
                        custom.setShapeType(RadiusImageView.SHAPE_RECTANGLE);
                        btn.setText("setShapeType(RadiusImageView.SHAPE_RECTANGLE)");
                        break;
                    default:
                        break;
                }
                if (count <= 7) {
                    count++;
                } else {
                    count = 0;
                }
            }
        });
    }
}
