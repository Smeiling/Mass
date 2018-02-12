package com.sml.mass.fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sml.mass.R;
import com.sml.mass.model.ChildItem;
import com.sml.mass.model.GroupItem;
import com.sml.mass.utils.TreeRecyclerViewHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import kotlin.collections.Grouping;

/**
 * @Author: Smeiling
 * @Date: 2018-01-30 16:47
 * @Description:
 */
public class ComponentsFragment extends Fragment {


    private RecyclerView recyclerView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initContent() {
        recyclerView = getView().findViewById(R.id.recycler_view);
        recyclerView.setNestedScrollingEnabled(false);
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

        try {
            InputStream inputStream = getResources().getAssets().open("item_list.json");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            List<GroupItem> groupList = gson.fromJson(inputStreamReader, new TypeToken<List<GroupItem>>() {
            }.getType());
            updateListWithData(groupList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void updateListWithData(List<GroupItem> groupList) {
        TreeRecyclerViewHelper treeRecyclerViewHelper = new TreeRecyclerViewHelper(getActivity(), recyclerView, new TreeRecyclerViewHelper.ItemClickListener() {
            @Override
            public void itemClicked(ChildItem child) {
                try {
                    if (!TextUtils.isEmpty(child.getClassName())) {
                        startActivity(new Intent(getActivity(), Class.forName(child.getClassName())));
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void itemClicked(GroupItem group) {

            }
        }, 1);

        treeRecyclerViewHelper.setContents(groupList);
    }
}
