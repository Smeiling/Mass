package com.sml.mass.utils;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sml.mass.adapter.TreeRecyclerViewAdapter;
import com.sml.mass.model.GroupItem;
import com.sml.mass.model.ChildItem;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * @Author: Smeiling
 * @Date: 2018-02-01 16:02
 * @Description:
 */
public class TreeRecyclerViewHelper {

    //data list
    private LinkedHashMap<GroupItem, List<ChildItem>> mGroupItemDataMap = new LinkedHashMap<>();
    private List<Object> mDataArrayList = new ArrayList<>();

    //GroupItem map
    //TODO : look for a way to avoid this
    private HashMap<String, GroupItem> mGroupItemMap = new HashMap<>();

    private GroupItem lastGroup;

    //adapter
    private TreeRecyclerViewAdapter treeRecyclerViewAdapter;
    private RecyclerView recyclerView;

    private OnExpandStateChangeListener onExpandStateChangeListener = new OnExpandStateChangeListener() {
        @Override
        public void onExpandStateChange(int position, GroupItem group, boolean isExpanded) {
            group.isExpanded = isExpanded;
            if (isExpanded) {
                expandData(group, mGroupItemDataMap.get(group).size());
            } else {
                collapseData(group, mGroupItemDataMap.get(group).size());
            }
        }
    };


    /**
     * 构造函数
     *
     * @param context
     * @param recyclerView
     * @param itemClickListener
     * @param gridSpanCount
     */
    public TreeRecyclerViewHelper(Context context, RecyclerView recyclerView, ItemClickListener itemClickListener,
                                  int gridSpanCount) {

        //setting the recycler view
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, gridSpanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        treeRecyclerViewAdapter = new TreeRecyclerViewAdapter(mDataArrayList,
                gridLayoutManager, itemClickListener, onExpandStateChangeListener);

        this.recyclerView = recyclerView;
        recyclerView.setAdapter(treeRecyclerViewAdapter);

        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setAddDuration(500);
        recyclerView.setItemAnimator(defaultItemAnimator);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }


    /**
     * Update content
     */
    public void notifyDataSetChanged() {
        //TODO : handle this condition such that these functions won't be called if the recycler view is on scroll
        generateDataList();
        treeRecyclerViewAdapter.notifyDataSetChanged();
    }

    public void expandData(GroupItem group, int childCount) {
        generateDataList();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        recyclerView.setLayoutParams(params);

        treeRecyclerViewAdapter.notifyItemRangeInserted(mDataArrayList.indexOf(group) + 1, childCount);
    }

    public void collapseData(GroupItem group, int childCount) {
        generateDataList();
        // Fix recyclerView height
        if (group == lastGroup) {
            int height = recyclerView.getMeasuredHeight();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            recyclerView.setLayoutParams(params);
        }
        treeRecyclerViewAdapter.notifyItemRangeRemoved(mDataArrayList.indexOf(group) + 1, childCount);

    }

    public void addHeaderItem() {
        treeRecyclerViewAdapter.setHeaderExists(true);
        mGroupItemDataMap.put(new GroupItem(""), null);
    }

    /**
     * Add group
     *
     * @param groupName groupName
     * @param items     groupItems
     */
    public void addGroupItem(String groupName, List<ChildItem> items) {
        GroupItem newGroupItem;
        // Name->Group
        mGroupItemMap.put(groupName, (newGroupItem = new GroupItem(groupName)));
        // Group->childItems
        mGroupItemDataMap.put(newGroupItem, items);
    }

    public void addItem(String groupName, ChildItem item) {
        mGroupItemDataMap.get(mGroupItemMap.get(groupName)).add(item);
    }

    public void removeItem(String groupName, ChildItem item) {
        mGroupItemDataMap.get(mGroupItemMap.get(groupName)).remove(item);
    }

    public void removeGroupItem(String groupName) {
        mGroupItemDataMap.remove(mGroupItemMap.get(groupName));
        mGroupItemMap.remove(groupName);
    }

    /**
     * Generate real data list
     */
    private void generateDataList() {
        mDataArrayList.clear();
        for (Map.Entry<GroupItem, List<ChildItem>> entry : mGroupItemDataMap.entrySet()) {

            GroupItem key;
            if (TextUtils.isEmpty(entry.getKey().getGroupName())) {
                // Header add at first
                mDataArrayList.add(0, key = entry.getKey());
            } else {
                // Add to tail
                mDataArrayList.add((key = entry.getKey()));
            }
            if (key.isExpanded) { //如果是可展开，则添加子Item到内容，否则不添加
                mDataArrayList.addAll(entry.getValue());
            }
        }
    }

    public void setContents(List<GroupItem> groupList) {
        // 添加头部
        addHeaderItem();
        for (GroupItem group : groupList) {
            addGroupItem(group.getGroupName(), group.getChildList());
        }
        lastGroup = groupList.get(groupList.size() - 1);
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void itemClicked(ChildItem child);

        void itemClicked(GroupItem group);
    }

    public interface OnExpandStateChangeListener {
        void onExpandStateChange(int position, GroupItem groupItem, boolean isExpanded);
    }
}
