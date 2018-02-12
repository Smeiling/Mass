package com.sml.mass.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sml.mass.R;
import com.sml.mass.activity.GradientRecyclerViewActivity;
import com.sml.mass.model.ChildItem;

import java.util.List;

/**
 * @Author: Smeiling
 * @Date: 2018-01-30 17:59
 * @Description:
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<ChildItem> mValues;
    private GradientRecyclerViewActivity.OnListFragmentInteractionListener mListener;

    public RecyclerViewAdapter(List<ChildItem> items) {
        mValues = items;
    }

    public void updateItems(List<ChildItem> newList) {
        this.mValues = newList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transparent_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTitle.setText(mValues.get(position).getWidgetName());
        holder.mIcon.setImageResource(
                holder.mIcon.getContext().getResources().getIdentifier(mValues.get(position).getWidgetIcon(),
                        "drawable",
                        holder.mIcon.getContext().getPackageName()));
        //holder.mIcon.setImageResource(mValues.get(position).getWidgetIcon());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitle;
        public final ImageView mIcon;
        public ChildItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitle = view.findViewById(R.id.title);
            mIcon = view.findViewById(R.id.icon);
        }

    }
}
