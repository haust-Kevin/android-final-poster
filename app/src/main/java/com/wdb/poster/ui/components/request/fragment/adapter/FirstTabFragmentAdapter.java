package com.wdb.poster.ui.components.request.fragment.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.wdb.poster.db.api.entity.Request;
import com.wdb.poster.ui.components.request.fragment.RequestPropertiesFragment;

import java.util.ArrayList;
import java.util.List;

public class FirstTabFragmentAdapter extends FragmentStatePagerAdapter {
    private List<String> tabs;
    private List<Fragment> fragments;

    public FirstTabFragmentAdapter(@NonNull FragmentManager fm, Request request) {
        super(fm);
        tabs = new ArrayList<>();
        fragments = new ArrayList<>();
        tabs.add("headers");
        tabs.add("params");
        tabs.add("body");
        fragments.add(RequestPropertiesFragment.newInstance("headers", request.getReqHeaders()));
        fragments.add(RequestPropertiesFragment.newInstance("params", request.getReqParams()));
        fragments.add(RequestPropertiesFragment.newInstance("body", request.getReqBody()));
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position);
    }
}
