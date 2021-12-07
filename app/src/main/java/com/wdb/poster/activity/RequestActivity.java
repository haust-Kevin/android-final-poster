package com.wdb.poster.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.widget.QMUIViewPager;
import com.qmuiteam.qmui.widget.tab.QMUIBasicTabSegment;
import com.qmuiteam.qmui.widget.tab.QMUITab;
import com.qmuiteam.qmui.widget.tab.QMUITabBuilder;
import com.qmuiteam.qmui.widget.tab.QMUITabIndicator;
import com.qmuiteam.qmui.widget.tab.QMUITabSegment;
import com.qmuiteam.qmui.widget.tab.QMUITabSegment2;
import com.wdb.poster.R;
import com.wdb.poster.db.api.ApiDatabase;
import com.wdb.poster.db.api.entity.Request;
import com.wdb.poster.handler.ToastHandler;
import com.wdb.poster.ui.components.request.adapter.TabFragmentAdapter;

public class RequestActivity extends QMUIActivity {

    private Request request;
    private ApiDatabase db;
    private int requestId;
    private ToastHandler toastHandler;
    private QMUITabSegment tabSegment;
    private QMUIViewPager viewpager;

    public QMUIViewPager getViewpager() {
        return viewpager;
    }

    public QMUITabSegment getTabSegment() {
        return tabSegment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_request);
        db = ApiDatabase.getInstance(RequestActivity.this);
        requestId = intent.getIntExtra("requestId", -1);
        toastHandler = new ToastHandler(RequestActivity.this);

        tabSegment = findViewById(R.id.tab_segment);
        viewpager = findViewById(R.id.viewpager);

        initViewPager();
        initData();


    }

    private void initViewPager() {
        tabSegment.setMode(QMUIBasicTabSegment.MODE_FIXED);
        viewpager.setAdapter(new TabFragmentAdapter(getSupportFragmentManager(), requestId));
        tabSegment.setupWithViewPager(viewpager, true);
        tabSegment.selectTab(0);
    }


    private void initData() {
        new Thread(() -> {
            request = db.getRequestDao().queryById(requestId);
        }).start();
    }

}