package com.wdb.poster.ui.components.request.adapter;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.wdb.poster.activity.MainActivity;
import com.wdb.poster.ui.components.request.fragment.RequestFirstFragment;
import com.wdb.poster.ui.components.request.fragment.RequestSecondFragment;
import com.wdb.poster.ui.components.request.fragment.ResponsePropertiesFragment;

import java.util.ArrayList;
import java.util.List;

public class TabFragmentAdapter extends FragmentStatePagerAdapter {

    private  List<String> tabs;
    private RequestFirstFragment requestFragment;
    private RequestSecondFragment responseFragment;

    public TabFragmentAdapter(@NonNull FragmentManager fm, int requestId) {
        super(fm);
        tabs = new ArrayList<>();
        tabs.add("Request");
        tabs.add("Response");
        requestFragment = RequestFirstFragment.newInstance(requestId);
        responseFragment = RequestSecondFragment.newInstance();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return requestFragment;
        }else{
            return responseFragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position);
    }
}
