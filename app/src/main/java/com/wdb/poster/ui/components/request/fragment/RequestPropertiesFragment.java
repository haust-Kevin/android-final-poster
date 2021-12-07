package com.wdb.poster.ui.components.request.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.qmuiteam.qmui.layout.QMUIConstraintLayout;
import com.wdb.poster.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestPropertiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestPropertiesFragment extends Fragment {

    private String title;
    private QMUIConstraintLayout propertyLayout;
    private View view;
    private String etData;

    public RequestPropertiesFragment() {
        // Required empty public constructor
    }

    public static RequestPropertiesFragment newInstance(String title, String etData) {
        RequestPropertiesFragment fragment = new RequestPropertiesFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("etData", etData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
            etData = getArguments().getString("etData");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_request_properties, container, false);
            propertyLayout = view.findViewById(R.id.property_layout);
            propertyLayout.setRadius(20);
            ((EditText) view.findViewById(R.id.properties_editbox)).setText(etData);
        }
        return view;
    }


}