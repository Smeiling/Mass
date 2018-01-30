package com.sml.mass.activity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sml.mass.R;
import com.sml.mass.adapter.RecyclerViewAdapter;
import com.sml.mass.model.WidgetItem;

import java.util.ArrayList;
import java.util.List;

public class GradientRecyclerViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<WidgetItem> itemList;
    private OnListFragmentInteractionListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gradient_recycler_view);

        loadData();

        mListener = new OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(WidgetItem widgetItem) {

            }
        };

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerViewAdapter(itemList, mListener));
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        doTopGradualEffect();
    }

    private Paint mPaint;
    private LinearGradient linearGradient;
    private int layerId;

    private void doTopGradualEffect() {
        if (recyclerView == null) {
            return;
        }

        mPaint = new Paint();
        final Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        mPaint.setXfermode(xfermode);
        linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, 200.0f, new int[]{Color.TRANSPARENT, Color.WHITE}, null, Shader.TileMode.CLAMP);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            // 在RecyclerView整体上做效果
            @Override
            public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
                super.onDrawOver(canvas, parent, state);

                mPaint.setXfermode(xfermode);
                mPaint.setShader(linearGradient);
                canvas.drawRect(0.0f, 0.0f, parent.getRight(), 200.0f, mPaint);
                mPaint.setXfermode(null);
                canvas.restoreToCount(layerId);
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
                layerId = c.saveLayer(0.0f, 0.0f, (float) parent.getWidth(), (float) parent.getHeight(), mPaint, Canvas.ALL_SAVE_FLAG);
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
            }
        });
    }

    private void loadData() {
        itemList = new ArrayList<>();
        WidgetItem widgetItem;
        for (int i = 0; i < 25; i++) {
            widgetItem = new WidgetItem();
            widgetItem.setWidgetName("smlsmlsml " + i);
            widgetItem.setWidgetIcon(R.drawable.kitty);
            itemList.add(widgetItem);
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(WidgetItem widgetItem);
    }
}
