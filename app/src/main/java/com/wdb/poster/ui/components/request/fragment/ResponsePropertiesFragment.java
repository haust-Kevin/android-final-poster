package com.wdb.poster.ui.components.request.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.qmuiteam.qmui.alpha.QMUIAlphaTextView;
import com.qmuiteam.qmui.layout.QMUIConstraintLayout;
import com.wdb.poster.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResponsePropertiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResponsePropertiesFragment extends Fragment {


    private String title;
    private String data;
    private QMUIConstraintLayout propertyLayout;
    private View view;

    public ResponsePropertiesFragment() {
        // Required empty public constructor
    }


    public static ResponsePropertiesFragment newInstance(String title, String data) {
        ResponsePropertiesFragment fragment = new ResponsePropertiesFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("data", data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
            data = getArguments().getString("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_response_properties, container, false);
            propertyLayout = view.findViewById(R.id.property_layout);
            propertyLayout.setRadius(20);
            ((QMUIAlphaTextView) view.findViewById(R.id.tv_response_properties_show)).setText(data);
        }
        return view;
    }
}