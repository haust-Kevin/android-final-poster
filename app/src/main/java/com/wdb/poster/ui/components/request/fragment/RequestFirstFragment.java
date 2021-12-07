package com.wdb.poster.ui.components.request.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.qmuiteam.qmui.layout.QMUIButton;
import com.qmuiteam.qmui.layout.QMUILinearLayout;
import com.qmuiteam.qmui.widget.QMUIViewPager;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.tab.QMUIBasicTabSegment;
import com.qmuiteam.qmui.widget.tab.QMUITabSegment;
import com.wdb.poster.R;
import com.wdb.poster.activity.RequestActivity;
import com.wdb.poster.constraint.RequestMethod;
import com.wdb.poster.db.api.ApiDatabase;
import com.wdb.poster.db.api.entity.Connection;
import com.wdb.poster.db.api.entity.Request;
import com.wdb.poster.entity.RequestWrapper;
import com.wdb.poster.entity.ResponseWrapper;
import com.wdb.poster.handler.ToastHandler;
import com.wdb.poster.http.client.HttpClientFactory;
import com.wdb.poster.http.client.IHttpClient;
import com.wdb.poster.ui.components.request.adapter.TabFragmentAdapter;
import com.wdb.poster.ui.components.request.fragment.adapter.FirstTabFragmentAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestFirstFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestFirstFragment extends Fragment {
    private static final String TAG = "RequestFirstFragment";

    public static ResponseWrapper responseWrapper;

    static {
        responseWrapper = new ResponseWrapper();
    }

    private Handler handler;
    private View thisView;
    private Context context;
    private QMUILinearLayout linearLayout;
    private ToastHandler toastHandler;
    private QMUITabSegment tabSegment;
    private QMUIViewPager viewpager;
    private int requestId;
    private ApiDatabase db;
    private Spinner spMethod;
    private Request request;
    private QMUITipDialog waitResponseDialog;
    private Connection connection;
    private EditText etUrl;
    private Button btSend;
    private EditText etReqHeaders;
    private EditText etReqParams;
    private EditText etReqBody;
    private FirstTabFragmentAdapter firstTabFragmentAdapter;
    private Thread thread;
    private final Object lock = new Object();

    public RequestFirstFragment() {
        // Required empty public constructor
    }


    public static RequestFirstFragment newInstance(int requestId) {
        RequestFirstFragment fragment = new RequestFirstFragment();
        Bundle args = new Bundle();
        args.putInt("requestId", requestId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            requestId = getArguments().getInt("requestId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request_first, container, false);
        thisView = view;
        ((QMUILinearLayout) view.findViewById(R.id.layout_input)).setRadius(10);
        ((QMUIButton) view.findViewById(R.id.btn_send)).setRadius(10);

        context = container.getContext();
        handler = new Handler(context.getMainLooper());
        toastHandler = new ToastHandler(context);
        db = ApiDatabase.getInstance(context);
        tabSegment = view.findViewById(R.id.request_edit_tab_segment);
        viewpager = view.findViewById(R.id.request_edit_viewpager);
        spMethod = view.findViewById(R.id.sp_method);
        etUrl = view.findViewById(R.id.et_request_req_url);
        btSend = view.findViewById(R.id.btn_send);
        loadDataAndInitViewPager();
        btSend.setOnClickListener(v -> sendRequest());
        return view;
    }

    private void loadDataAndInitViewPager() {
        new Thread(() -> {
            request = db.getRequestDao().queryById(requestId);
            connection = db.getConnectionDao().queryById(request.getConnId());
            handler.post(() -> {
                ((TextView) thisView.findViewById(R.id.request_conn_name)).setText(connection.getConnName());
                ((TextView) thisView.findViewById(R.id.request_req_name)).setText(request.getReqName());
                ((TextView) thisView.findViewById(R.id.request_conn_default_website)).setText(connection.getDefaultWebsite());
                // TODO refactor REQUEST_METHOD
                spMethod.setSelection(request.getReqMethod().equals("POST") ? 0 : 1);
                etUrl.setText(request.getReqPath());
                initViewPager();
            });
        }).start();
    }

    private void initViewPager() {
        tabSegment.setMode(QMUIBasicTabSegment.MODE_FIXED);
        viewpager.setAdapter(new FirstTabFragmentAdapter(getParentFragmentManager(), request));
        tabSegment.setupWithViewPager(viewpager, true);
        tabSegment.selectTab(2);
        firstTabFragmentAdapter = (FirstTabFragmentAdapter) viewpager.getAdapter();
    }

    private void saveRequest() {
        updateRequestInFragment();
        Log.e(TAG, "saveRequest: " + request);
        new Thread(() -> db.getRequestDao().update(request)).start();
    }

    private void updateRequestInFragment() {

        request.setReqPath(etUrl.getText().toString());
        request.setReqMethod(spMethod.getSelectedItem().toString());

        View view;
        view = firstTabFragmentAdapter.getItem(0).getView();
        if (view != null)
            request.setReqHeaders(((EditText) view.findViewById(R.id.properties_editbox)).getText().toString().trim());
        view = firstTabFragmentAdapter.getItem(1).getView();
        if (view != null)
            request.setReqParams(((EditText) view.findViewById(R.id.properties_editbox)).getText().toString().trim());
        view = firstTabFragmentAdapter.getItem(2).getView();
        if (view != null)
            request.setReqBody(((EditText) view.findViewById(R.id.properties_editbox)).getText().toString().trim());
    }


    private void sendRequest() {
        saveRequest();
        RequestWrapper requestWrapper = generateRequestWrapper();
        System.out.println(requestWrapper);
        if (requestWrapper == null) {
            return;
        }
        if (thread != null && thread.isAlive()) thread.stop();

        IHttpClient client = HttpClientFactory.create(connection);
        executeRequest(client, requestWrapper, new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                handler.post(() -> {
                    waitResponseDialog.dismiss();
                    if (call.isCanceled()) return;
                    QMUITipDialog requestFailure = new QMUITipDialog.Builder(context)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                            .setTipWord(e.getMessage())
                            .create();
                    requestFailure.show();
                    handler.postDelayed(requestFailure::dismiss, 4000);
                    toastHandler.toast("failure");
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {

                handler.post(() -> {
                    waitResponseDialog.dismiss();
                    toastHandler.toast("success");
                    updateResponseWrapper(response);
                    QMUIViewPager viewpager = ((RequestActivity) getActivity()).getViewpager();
                    QMUITabSegment tabSegment = ((RequestActivity) getActivity()).getTabSegment();
                    RequestSecondFragment secondFragment = (RequestSecondFragment) ((TabFragmentAdapter) viewpager.getAdapter()).getItem(1);
                    secondFragment.updateView();
                    tabSegment.selectTab(1);
                });
            }
        });


        waitResponseDialog = new QMUITipDialog.Builder(context).setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING).setTipWord("wait response").create();
        waitResponseDialog.show();
        waitResponseDialog.setOnCancelListener(dialog -> {
            dialog.dismiss();
            client.cancelAllRequest();
            toastHandler.toast("request cancelled");
        });

    }

    private void updateResponseWrapper(Response response) {
        try {
            responseWrapper.body = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(response.protocol().toString())
                .append(" ")
                .append(response.code())
                .append(" ")
                .append(response.message()).append("\n")
                .append(response.headers().toString())
                .append("\n")
                .append(responseWrapper.body);
        responseWrapper.responseRaw = responseBuilder.toString();


        try {
            JSONObject jsonObject = new JSONObject(responseWrapper.body);
            responseWrapper.body = jsonObject.toString(2);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        okhttp3.Request request = response.networkResponse().request();
        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append(request.method())
                .append(" ")
                .append(request.url().url().getFile())
                .append(" ")
                .append(response.protocol().toString())
                .append("\n")
                .append(request.headers().toString());

        Buffer buffer = new Buffer();
        RequestBody requestBody = request.body();
        try {
            if (requestBody != null)
                requestBody.writeTo(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (requestBody != null)
            requestBuilder.append("\n").append(buffer.toString());
        responseWrapper.requestRaw = requestBuilder.toString();
        responseWrapper.code = response.code();
        responseWrapper.message = response.message();
    }

    private RequestWrapper generateRequestWrapper() {
        updateRequestInFragment();
        System.out.println(request);
        RequestWrapper wrapper = new RequestWrapper();
        wrapper.method = request.getReqMethod().equals("GET") ? RequestMethod.GET : RequestMethod.POST;

        String headers = request.getReqHeaders();
        if (headers != null) {
            for (String h : headers.split("\n")) {
                h = h.trim();
                if (h.equals("")) continue;
                String[] kv = h.split(":");
                if (kv.length != 2) {
                    toastHandler.toast("Please check headers");
                    return null;
                }
                wrapper.headers.put(kv[0].trim(), kv[1].trim());
            }
        }

        String params = request.getReqParams();
        Map<String, String> paramsMap = new HashMap<>();
        if (params != null) {
            for (String p : params.split("\n|&")) {
                p = p.trim();
                if (p.equals("")) continue;
                String[] kv = p.split("=");
                if (kv.length != 2) {
                    toastHandler.toast("Please check params");
                    return null;
                }
                paramsMap.put(kv[0].trim(), kv[1].trim());
            }
        }

        StringBuilder path = new StringBuilder(request.getReqPath());
        if (!"".equals(connection.getDefaultWebsite()) && !path.toString().contains("://")) {
            path.insert(0, connection.getDefaultWebsite());
            if (path.length() >= 4 && !path.substring(0, 4).equals("http")) {
                path.insert(0, "http://");
            }
        }
        if (!path.toString().contains("://")) {
            toastHandler.toast("Please check path");
            return null;
        }
        // 完善 restful url 的参数
        System.out.println(path);
        while (path.indexOf(":", path.indexOf("/", 8)) != -1) {
            int idx = path.indexOf("/", 8);
            if (idx == -1) break;
            int idx1 = path.indexOf(":", idx);
            int idx2 = path.indexOf("/", idx1);
            if (idx2 == -1) idx2 = path.length();
            String pre = path.substring(0, idx1);
            String suf = path.substring(idx2);
            String paramName = path.substring(idx1 + 1, idx2);

            if (!paramsMap.containsKey(paramName)) {
                toastHandler.toast("Please check params in restful path");
                return null;
            }
            path = new StringBuilder(pre + paramsMap.remove(paramName) + suf);
        }

        if (!paramsMap.isEmpty()) {
            path.append("?");
            for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                path.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            path.deleteCharAt(path.length() - 1);
        }
        wrapper.url = path.toString();

        do {
            if (wrapper.method != RequestMethod.POST) break;
            String reqBody = request.getReqBody();
            if (reqBody == null) break;
            String json = reqBody.trim();
            if ("".equals(json)) break;
            try {
                wrapper.body = new JSONObject(json).toString();
            } catch (JSONException e) {
                toastHandler.toast("Please check json body");
                return null;
            }
        } while (false);

        return wrapper;
    }

    @Override
    public void onStop() {
        super.onStop();
        saveRequest();
    }

    private void executeRequest(IHttpClient client, RequestWrapper requestWrapper, Callback callback) {
        System.out.println(requestWrapper);
        if (requestWrapper.method == RequestMethod.GET) {
            System.out.println("GET  " + requestWrapper.url);
            client.get(requestWrapper.url, requestWrapper.headers, callback);
        } else {
            System.out.println("POST " + requestWrapper.url);
            client.post(requestWrapper.url, requestWrapper.body, requestWrapper.headers, callback);
        }
    }
}