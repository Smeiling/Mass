package com.sml.mass.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sml.mass.R;
import com.sml.mass.model.WidgetItem;

import java.util.List;

/**
 * Created by Smeiling on 2018/1/19.
 */

public class WidgetsAdapter extends BaseAdapter {

    private Context context;
    private List<WidgetItem> items;
    private LayoutInflater inflater;

    public WidgetsAdapter(Context context, List<WidgetItem> items) {
        this.context = context;
        this.items = items;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (items != null) {
            return items.size();
        }
        return 0;
    }

    @Override
    public WidgetItem getItem(int position) {
        if (items != null) {
            return items.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_layout, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(getItem(position).getWidgetName());
        viewHolder.icon.setImageResource(getItem(position).getWidgetIcon());

        return convertView;
    }

    private static class ViewHolder {
        TextView title;
        ImageView icon;

        public ViewHolder(View view) {
            title = view.findViewById(R.id.title);
            icon = view.findViewById(R.id.icon);
        }
    }
}
