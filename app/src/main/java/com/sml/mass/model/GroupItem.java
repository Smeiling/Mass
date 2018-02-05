package com.sml.mass.model;

import java.util.List;

/**
 * @Author: Smeiling
 * @Date: 2018-01-30 17:19
 * @Description:
 */
public class GroupItem {
    public boolean isExpanded;

    private String groupName;
    private int groupIcon;
    private List<ChildItem> childList;

    public GroupItem(String groupName) {
        this.groupName = groupName;
        this.isExpanded = false;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getGroupIcon() {
        return groupIcon;
    }

    public void setGroupIcon(int groupIcon) {
        this.groupIcon = groupIcon;
    }

    public List<ChildItem> getChildList() {
        return childList;
    }

    public void setChildList(List<ChildItem> childList) {

        this.childList = childList;
    }
}
