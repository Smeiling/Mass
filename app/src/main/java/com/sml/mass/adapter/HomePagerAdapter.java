package com.sml.mass.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @Author: Smeiling
 * @Date: 2018-01-30 16:34
 * @Description:
 */
public class HomePagerAdapter extends FragmentPagerAdapter {

    private List<String> fragmentClasses;

    public HomePagerAdapter(FragmentManager fm, List<String> classes) {
        super(fm);
        this.fragmentClasses = classes;
    }

    @Override
    public Fragment getItem(int position) {
        Object fragment = null;
        if (fragmentClasses != null) {
            Class<?> clazz;
            try {
                clazz = Class.forName(fragmentClasses.get(position));
                fragment = clazz.newInstance();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return (Fragment) fragment;
    }

    @Override
    public int getCount() {
        if (fragmentClasses != null) {
            return fragmentClasses.size();
        } else {
            return 0;
        }
    }
}
