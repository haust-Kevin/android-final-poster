package com.wdb.poster.ui.components.request.fragment.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.wdb.poster.entity.ResponseWrapper;
import com.wdb.poster.ui.components.request.fragment.ResponsePropertiesFragment;

import java.util.ArrayList;
import java.util.List;

public class SecondTabFragmentAdapter extends FragmentStatePagerAdapter {
    private List<String> tabs;
    private List<Fragment> fragments;

    public SecondTabFragmentAdapter(@NonNull FragmentManager fm, ResponseWrapper responseWrapper) {
        super(fm);
        tabs = new ArrayList<>();
        fragments = new ArrayList<>();
        tabs.add("request raw");
        tabs.add("response raw");
        tabs.add("body");
        fragments.add(ResponsePropertiesFragment.newInstance("request raw", responseWrapper.requestRaw));
        fragments.add(ResponsePropertiesFragment.newInstance("response raw", responseWrapper.responseRaw));
        fragments.add(ResponsePropertiesFragment.newInstance("body", responseWrapper.body));
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
