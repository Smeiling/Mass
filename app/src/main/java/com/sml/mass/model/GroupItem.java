package com.sml.mass.model;

import java.util.List;

/**
 * @Author: Smeiling
 * @Date: 2018-01-30 17:19
 * @Description:
 */
public class GroupItem {
    private String groupName;
    private int groupIcon;
    private List<WidgetItem> childList;

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

    public List<WidgetItem> getChildList() {
        return childList;
    }

    public void setChildList(List<WidgetItem> childList) {
        this.childList = childList;
    }
}
