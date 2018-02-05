package com.sml.mass.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sml.mass.R;
import com.sml.mass.model.ChildItem;
import com.sml.mass.model.GroupItem;
import com.sml.mass.utils.TreeRecyclerViewHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Smeiling
 * @Date: 2018-01-30 16:47
 * @Description:
 */
public class ComponentsFragment extends Fragment {

    private static final int MOVE_UP = -1;
    private static final int MOVE_DOWN = 1;

    private RecyclerView recyclerView;
    private LinearLayout parentLayout;
    private LinearLayout titleLayout;

    /**
     * 系统默认最小滑动距离
     */
    private int touchSlop;

    private float startY;

    private View.OnTouchListener onParentViewTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d("Smeiling", "ACTION_DOWN" + event.getY());
                    startY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d("Smeiling", "ACTION_MOVE" + event.getY());
                    if (startY - event.getY() > 0) {
                        handleTouchEvent(MOVE_UP, Math.abs(startY - event.getY()));
                    } else if (startY - event.getY() < 0) {
                        handleTouchEvent(MOVE_DOWN, Math.abs(startY - event.getY()));
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d("Smeiling", "ACTION_UP" + event.getY());
                    break;
            }
            return true;
        }
    };

    private boolean isTitleVisible = true;

    private void handleTouchEvent(int orientation, float distance) {
        if (orientation == MOVE_UP) {
            if (isTitleVisible) {
                titleLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.hide_to_top));
                titleLayout.setVisibility(View.GONE);
                isTitleVisible = false;
            }
        } else if (orientation == MOVE_DOWN) {
            if (!isTitleVisible) {
                titleLayout.setVisibility(View.VISIBLE);
                titleLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.show_from_top));
                isTitleVisible = true;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        touchSlop = ViewConfiguration.get(getActivity()).getScaledTouchSlop();
    }

    private void initContent() {
        parentLayout = getView().findViewById(R.id.parent_layout);
        titleLayout = getView().findViewById(R.id.title_layout);
        recyclerView = getView().findViewById(R.id.recycler_view);
        recyclerView.setNestedScrollingEnabled(false);
        parentLayout.setOnTouchListener(onParentViewTouchListener);

    }

    private void initHeader() {
        ImageView mainIcon = getView().findViewById(R.id.main_icon);
        mainIcon.setImageResource(R.mipmap.icon_components);
        TextView mainDescription = getView().findViewById(R.id.main_description);
        mainDescription.setText(R.string.components_description);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initHeader();
        initContent();
        loadData();
    }

    private void loadData() {
        List<GroupItem> groupList = new ArrayList<>();
        List<ChildItem> widgetList;
        ChildItem childItem;
        GroupItem groupItem;
        for (int i = 0; i < 10; i++) {
            groupItem = new GroupItem("Basic" + i);
            widgetList = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                childItem = new ChildItem();
                childItem.setWidgetName("FolderTextView" + i + j);
                childItem.setWidgetIcon(R.drawable.kitty);
                childItem.setClassName("com.sml.mass.components.basic.FolderTextView");
                widgetList.add(childItem);
            }
            groupItem.setChildList(widgetList);
            groupList.add(groupItem);
        }
        updateListWithData(groupList);
    }

    private void updateListWithData(List<GroupItem> groupList) {
        TreeRecyclerViewHelper treeRecyclerViewHelper = new TreeRecyclerViewHelper(getActivity(), recyclerView, new TreeRecyclerViewHelper.ItemClickListener() {
            @Override
            public void itemClicked(ChildItem child) {

            }

            @Override
            public void itemClicked(GroupItem group) {

            }
        }, 1);

        treeRecyclerViewHelper.setContents(groupList);
    }
}
