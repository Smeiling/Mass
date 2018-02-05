package com.sml.mass.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.sml.mass.R;
import com.sml.mass.adapter.RecyclerViewAdapter;
import com.sml.mass.components.list.GradientRecyclerView;
import com.sml.mass.model.ChildItem;

import java.util.ArrayList;
import java.util.List;

public class GradientRecyclerViewActivity extends AppCompatActivity {

    private GradientRecyclerView recyclerView;
    private List<ChildItem> itemList = new ArrayList<>();

    private RecyclerViewAdapter adapter;
    private ImageView btnSend;
    private EditText etMsg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gradient_recycler_view);

        etMsg = findViewById(R.id.et_msg);
        btnSend = findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etMsg != null && !TextUtils.isEmpty(etMsg.getText().toString())) {
                    addData(etMsg.getText().toString());
                    etMsg.setText("");
                }
            }
        });

        adapter = new RecyclerViewAdapter(itemList);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView.setAdapter(adapter);
    }

    private void addData(String msg) {
        ChildItem childItem = new ChildItem();
        childItem.setWidgetName(msg);
        childItem.setWidgetIcon(R.drawable.kitty);
        itemList.add(childItem);
        adapter.updateItems(itemList);
        recyclerView.scrollToPosition(itemList.size() - 1);
        if (itemList.size() == 7) {
            recyclerView.addGradientEffect();
            recyclerView.invalidate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.scrollToPosition(itemList.size() - 1);

    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(ChildItem childItem);
    }


}
