package com.wdb.poster.ui.components.request.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.qmuiteam.qmui.layout.QMUIButton;
import com.qmuiteam.qmui.widget.QMUIViewPager;
import com.qmuiteam.qmui.widget.tab.QMUITabSegment;
import com.wdb.poster.R;
import com.wdb.poster.db.api.ApiDatabase;
import com.wdb.poster.entity.ResponseWrapper;
import com.wdb.poster.handler.ToastHandler;
import com.wdb.poster.ui.components.request.fragment.adapter.SecondTabFragmentAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestSecondFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestSecondFragment extends Fragment {

    private String requestRaw;
    private String responseRaw;
    private String body;
    private Context context;
    private Handler handler;
    private ToastHandler toastHandler;
    private ApiDatabase db;
    private QMUITabSegment tabSegment;
    private QMUIViewPager viewpager;
    private SecondTabFragmentAdapter secondTabFragmentAdapter;


    private RequestSecondFragment() {
        // Required empty public constructor
    }


    public static RequestSecondFragment newInstance() {
        RequestSecondFragment fragment = new RequestSecondFragment();
        ResponseWrapper response = RequestFirstFragment.responseWrapper;
        Bundle args = new Bundle();
        args.putString("requestRaw", response.requestRaw);
        args.putString("responseRaw", response.responseRaw);
        args.putString("body", response.body);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            requestRaw = getArguments().getString("requestRaw");
            body = getArguments().getString("body");
            responseRaw = getArguments().getString("responseRaw");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request_second, container, false);

        context = container.getContext();
        handler = new Handler(context.getMainLooper());
        toastHandler = new ToastHandler(context);
        db = ApiDatabase.getInstance(context);
        tabSegment = view.findViewById(R.id.response_tab);
        viewpager = view.findViewById(R.id.response_viewpager);
        initViewpager();
        return view;
    }

    private void initViewpager() {
        viewpager.setAdapter(new SecondTabFragmentAdapter(getParentFragmentManager(), RequestFirstFragment.responseWrapper));
        tabSegment.setupWithViewPager(viewpager, true);
        tabSegment.selectTab(2);
        System.out.println(RequestFirstFragment.responseWrapper);
        secondTabFragmentAdapter = (SecondTabFragmentAdapter) viewpager.getAdapter();
    }

    public void updateView() {
        viewpager.setAdapter(new SecondTabFragmentAdapter(getParentFragmentManager(), RequestFirstFragment.responseWrapper));
        tabSegment.selectTab(2);
        int colorId = 0;
        switch (RequestFirstFragment.responseWrapper.code / 100) {
            case 1:
                colorId = R.color.net_status_10x;
                break;
            case 2:
                colorId = R.color.net_status_20x;
                break;
            case 3:
                colorId = R.color.net_status_30x;
                break;
            case 4:
                colorId = R.color.net_status_40x;
                break;
            case 5:
                colorId = R.color.net_status_50x;
                break;
        }
        QMUIButton button = getActivity().findViewById(R.id.btn_resp_status);
        button.setBackgroundColor(getResources().getColor(colorId));
        button.setRadius(10);
        button.setText(RequestFirstFragment.responseWrapper.code + " " + RequestFirstFragment.responseWrapper.message);
    }

}