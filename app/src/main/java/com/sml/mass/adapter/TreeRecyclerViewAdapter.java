package com.sml.mass.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sml.mass.R;
import com.sml.mass.model.ChildItem;
import com.sml.mass.model.GroupItem;
import com.sml.mass.utils.TreeRecyclerViewHelper;
import com.sml.mass.utils.ViewUtils;

import java.util.List;

/**
 * @Author: Smeiling
 * @Date: 2018-02-01 15:53
 * @Description:
 */
public class TreeRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * 头部
     */
    private boolean headerExists = false;

    //data array
    private List<Object> mDataArrayList;

    //listeners
    private final TreeRecyclerViewHelper.ItemClickListener mItemClickListener;
    private final TreeRecyclerViewHelper.OnExpandStateChangeListener onExpandStateChangeListener;

    //view type
    private static final int VIEW_TYPE_GROUP = R.layout.list_item_layout;
    private static final int VIEW_TYPE_ITEM = R.layout.child_item_layout; //TODO : change this
    private static final int VIEW_TYPE_HEADER = R.layout.fragment_header;

    public TreeRecyclerViewAdapter(List<Object> dataArrayList,
                                   final GridLayoutManager gridLayoutManager,
                                   TreeRecyclerViewHelper.ItemClickListener itemClickListener,
                                   TreeRecyclerViewHelper.OnExpandStateChangeListener GroupItemStateChangeListener) {
        // Item点击监听
        mItemClickListener = itemClickListener;
        // 展开监听
        onExpandStateChangeListener = GroupItemStateChangeListener;
        // 数据
        mDataArrayList = dataArrayList;

        // Item跨度，如果是GroupTitle就满行显示
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return isGroupItem(position) ? gridLayoutManager.getSpanCount() : 1;
            }
        });
    }

    /**
     * 是否有头部
     *
     * @param headerExists
     */
    public void setHeaderExists(boolean headerExists) {
        this.headerExists = headerExists;
    }

    /**
     * 是否为GroupTitle
     *
     * @param position
     * @return
     */
    private boolean isGroupItem(int position) {
        return mDataArrayList.get(position) instanceof GroupItem;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_ITEM) {
            return new ChildViewHolder(inflater.inflate(viewType, null));
        } else if (viewType == VIEW_TYPE_GROUP) {
            return new GroupViewHolder(inflater.inflate(viewType, parent, false));
        } else {
            return new HeaderViewHolder(inflater.inflate(viewType, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_ITEM:
                ChildViewHolder childViewHolder = (ChildViewHolder) holder;
                final ChildItem childItem = (ChildItem) mDataArrayList.get(position);
                childViewHolder.childName.setText(childItem.getWidgetName());
                childViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewUtils.addRippleEffect(v);
                        mItemClickListener.itemClicked(childItem);
                    }
                });
                if (!TextUtils.isEmpty(childItem.getWidgetIcon())) {
                    childViewHolder.childIcon.setImageResource(childViewHolder.childIcon.getContext().getResources().getIdentifier(
                            childItem.getWidgetIcon(),
                            "mipmap",
                            childViewHolder.childIcon.getContext().getPackageName()
                    ));
                } else {
                    childViewHolder.childIcon.setImageResource(R.mipmap.icon_default);
                }
                break;
            case VIEW_TYPE_GROUP:
                final GroupViewHolder groupViewHolder = (GroupViewHolder) holder;
                final GroupItem groupItem = (GroupItem) mDataArrayList.get(position);
                groupViewHolder.groupName.setText(groupItem.getGroupName());
                if (!TextUtils.isEmpty(groupItem.getGroupIcon())) {
                    groupViewHolder.groupIcon.setImageResource(groupViewHolder.groupIcon.getContext().getResources().getIdentifier(
                            groupItem.getGroupIcon(),
                            "mipmap",
                            groupViewHolder.groupIcon.getContext().getPackageName()
                    ));
                } else {
                    groupViewHolder.groupIcon.setImageResource(R.mipmap.icon_default);
                }
                //展开/收起点击监听
                groupViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onExpandStateChangeListener.onExpandStateChange(position, groupItem, !groupItem.isExpanded);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            if (groupItem.isExpanded) {
                                groupViewHolder.groupIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor("#E6E6E6")));
                                groupViewHolder.groupName.setTextColor(Color.parseColor("#E6E6E6"));
                            } else {
                                groupViewHolder.groupIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor("#2C2C2C")));
                                groupViewHolder.groupName.setTextColor(Color.parseColor("#2C2C2C"));
                            }
                        }
                    }
                });
                break;
            case VIEW_TYPE_HEADER:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.icon.setImageResource(R.mipmap.icon_components);
                headerViewHolder.description.setText(R.string.components_description);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mDataArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (headerExists && position == 0) {
            return VIEW_TYPE_HEADER;
        }
        if (isGroupItem(position))
            return VIEW_TYPE_GROUP;
        else return VIEW_TYPE_ITEM;
    }

    protected static class HeaderViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView description;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.main_icon);
            description = itemView.findViewById(R.id.main_description);
        }
    }

    protected static class GroupViewHolder extends RecyclerView.ViewHolder {

        //common
        View view;
        ImageView groupIcon;

        //for GroupItem
        TextView groupName;

        public GroupViewHolder(View view) {
            super(view);
            this.view = view;
            groupIcon = view.findViewById(R.id.icon);
            groupName = view.findViewById(R.id.title);
        }
    }

    protected static class ChildViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        ImageView childIcon;
        TextView childName;

        public ChildViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.childIcon = itemView.findViewById(R.id.icon);
            this.childName = itemView.findViewById(R.id.title);
        }
    }
}
