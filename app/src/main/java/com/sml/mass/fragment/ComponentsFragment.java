package com.sml.mass.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sml.mass.R;
import com.sml.mass.adapter.WidgetsAdapter;
import com.sml.mass.model.GroupItem;
import com.sml.mass.model.WidgetItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Smeiling
 * @Date: 2018-01-30 16:47
 * @Description:
 */
public class ComponentsFragment extends Fragment {

    private ExpandableListView listView;
    private WidgetsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, null);
    }

    private void initContent() {
        listView = getView().findViewById(R.id.content_list);
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
        List<WidgetItem> widgetList;
        WidgetItem widgetItem;
        GroupItem groupItem;
        for (int i = 0; i < 3; i++) {
            groupItem = new GroupItem();
            groupItem.setGroupName("Basic");
            groupItem.setGroupIcon(R.drawable.kitty);
            widgetList = new ArrayList<>();
            for (int j = 0; j < 2; j++) {
                widgetItem = new WidgetItem();
                widgetItem.setWidgetName("FolderTextView");
                widgetItem.setWidgetIcon(R.drawable.kitty);
                widgetItem.setClassName("com.sml.mass.components.basic.FolderTextView");
                widgetList.add(widgetItem);
            }
            groupItem.setChildList(widgetList);
            groupList.add(groupItem);
        }
        updateListWithData(groupList);
    }

    private void updateListWithData(List<GroupItem> groupList) {
        adapter = new WidgetsAdapter(groupList);
        listView.setAdapter(adapter);
    }
}
