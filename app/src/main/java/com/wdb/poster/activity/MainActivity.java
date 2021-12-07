package com.wdb.poster.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogView;
import com.qmuiteam.qmui.widget.section.QMUISection;
import com.qmuiteam.qmui.widget.section.QMUIStickySectionAdapter;
import com.qmuiteam.qmui.widget.section.QMUIStickySectionLayout;
import com.wdb.poster.R;
import com.wdb.poster.db.api.ApiDatabase;
import com.wdb.poster.db.api.dao.ConnectionDao;
import com.wdb.poster.db.api.dao.RequestDao;
import com.wdb.poster.db.api.entity.Connection;
import com.wdb.poster.db.api.entity.Request;
import com.wdb.poster.ui.components.connections.adapter.ConnectionSectionAdapter;
import com.wdb.poster.ui.components.connections.tree.HeaderModel;
import com.wdb.poster.ui.components.connections.tree.ItemModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends QMUIActivity {
    private final static String TAG = "MainActivity";
    private final static int INVALID_CONNECTION_ID = -1;
    private static final int INVALID_REQUEST_ID = -1;

    private QMUITopBarLayout topBar;
    private QMUIStickySectionLayout mSectionLayout;
    private RecyclerView.LayoutManager mLayoutManager;
    protected static QMUIStickySectionAdapter<HeaderModel, ItemModel, QMUIStickySectionAdapter.ViewHolder> mAdapter;

    List<Connection> conns;
    private Map<Integer, List<Request>> reqsMap;
    private ApiDatabase db;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QMUIStatusBarHelper.translucent(this);
        topBar = findViewById(R.id.topbar);
        mSectionLayout = findViewById(R.id.conn_list_layout);
        findViewById(R.id.btn_add_conn).setOnClickListener(v -> {
            showConnectionEditDialog(INVALID_CONNECTION_ID);
        });
        db = ApiDatabase.getInstance(this);

        initAdatpter();
        initAdapterLogicalOperation();

        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 1) {
                    Toast.makeText(MainActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void initAdatpter() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mSectionLayout.setLayoutManager(mLayoutManager);
        mAdapter = new ConnectionSectionAdapter();
        fillAdapterData();
        mSectionLayout.setAdapter(mAdapter, false);
    }

    @SuppressLint("StaticFieldLeak")
    private void fillAdapterData() {
        new AsyncTask<Void, Void, List<QMUISection<HeaderModel, ItemModel>>>() {

            @Override
            protected List<QMUISection<HeaderModel, ItemModel>> doInBackground(Void... voids) {
                return getData();
            }

            @Override
            protected void onPostExecute(List<QMUISection<HeaderModel, ItemModel>> data) {
                mAdapter.setData(data);
            }

        }.execute();
    }

    private List<QMUISection<HeaderModel, ItemModel>> getData() {
        SharedPreferences sharedPreferences = getSharedPreferences("IS_FIRST_USE", MODE_PRIVATE);
        boolean isFirstUse = sharedPreferences.getBoolean("is_first_use", true);
        if (isFirstUse) {
            setOriginDatabase();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_first_use", false);
            editor.apply();
        }

        List<QMUISection<HeaderModel, ItemModel>> ret = new ArrayList<>();

        conns = db.getConnectionDao().queryAll();
        reqsMap = new HashMap<>();

        for (Connection conn : conns) {
            HeaderModel headerModel = new HeaderModel(conn);

            List<Request> data = db.getRequestDao().queryByConnId(conn.getConnId());
            reqsMap.put(conn.getConnId(), data);

            ArrayList<ItemModel> itemModels = new ArrayList<>();
            for (Request req : data) {
                itemModels.add(new ItemModel(req));
            }
            ret.add(new QMUISection<>(headerModel, itemModels));
        }
        return ret;
    }

    private void initAdapterLogicalOperation() {
        mAdapter.setCallback(new QMUIStickySectionAdapter.Callback<HeaderModel, ItemModel>() {
            @Override
            public void loadMore(QMUISection<HeaderModel, ItemModel> section, boolean loadMoreBefore) {
                Log.e(TAG, "loadMore: ");
            }

            @Override
            public void onItemClick(QMUIStickySectionAdapter.ViewHolder holder, int position) {
                if (holder.getItemViewType() == mAdapter.getItemViewType(0)) {
                    mAdapter.toggleFold(position, true);
                } else {
                    Intent intent = new Intent(MainActivity.this, RequestActivity.class);
                    intent.putExtra("requestId", mAdapter.getSectionItem(position).getRequest().getReqId());
                    startActivity(intent);
                }
            }


            @Override
            public boolean onItemLongClick(QMUIStickySectionAdapter.ViewHolder holder, int position) {

                QMUISection<HeaderModel, ItemModel> section = mAdapter.getSection(position);
                QMUIBottomSheet.BottomListSheetBuilder sheetBuilder = new QMUIBottomSheet.BottomListSheetBuilder(MainActivity.this);

                int connId = section.getHeader().getConn().getConnId();
                // 如果是 header
                if (holder.getItemViewType() == mAdapter.getItemViewType(0)) {
                    sheetBuilder.addItem("编辑");
                    sheetBuilder.addItem("新增请求");
                    sheetBuilder.addItem("删除");
                    sheetBuilder.setTitle(section.getHeader().getConn().getConnName());
                    sheetBuilder.setOnSheetItemClickListener((dialog, itemView, position1, tag) -> {
                        dialog.dismiss();
                        switch (tag) {
                            case "编辑":
                                showConnectionEditDialog(connId);
                                break;
                            case "删除":
                                new Thread(() -> {
                                    Message msg = new Message();
                                    msg.what = 1;
                                    try {
                                        db.getConnectionDao().deleteById(connId);
                                        msg.obj = "success";
                                        handler.sendMessage(msg);
                                        fillAdapterData();
                                    } catch (Exception ignored) {
                                        msg.obj = "failure";
                                        handler.sendMessage(msg);
                                    }
                                }).start();
                                break;
                            case "新增请求":
                                showSimpleEditRequestDialog(connId, INVALID_REQUEST_ID);
                                break;
                        }

                    });
                } else {
                    sheetBuilder.addItem("编辑");
                    sheetBuilder.addItem("删除");
                    int reqId = mAdapter.getSectionItem(position).getRequest().getReqId();
                    String reqName = mAdapter.getSectionItem(position).getRequest().getReqName();
                    sheetBuilder.setTitle(reqName);
                    sheetBuilder.setOnSheetItemClickListener((dialog, itemView, position12, tag) -> {
                        dialog.dismiss();
                        switch (tag) {
                            case "删除":
                                new Thread(() -> {
                                    Message msg = new Message();
                                    msg.what = 1;
                                    try {
                                        db.getRequestDao().deleteById(reqId);
                                        msg.obj = "success";
                                        handler.sendMessage(msg);
                                        fillAdapterData();
                                    } catch (Exception ignored) {
                                        msg.obj = "failure";
                                        handler.sendMessage(msg);
                                    }
                                }).start();
                                break;
                            case "编辑":
                                showSimpleEditRequestDialog(connId, reqId);
                                break;
                        }
                    });
                }
//                sheetBuilder.setAddCancelBtn(true);
                sheetBuilder.setGravityCenter(true);
                QMUIBottomSheet bottomSheet = sheetBuilder.build();
                bottomSheet.show();
                return true;
            }
        });
        // TODO
    }

    @SuppressLint("StaticFieldLeak")
    private void showSimpleEditRequestDialog(int connId, int reqId) {
        new AsyncTask<Void, Void, Request>() {
            @Override
            protected void onPostExecute(Request request) {
                new QMUIDialog.CustomDialogBuilder(MainActivity.this) {
                    @Nullable
                    @Override
                    protected View onCreateContent(QMUIDialog dialog, QMUIDialogView parent, Context context) {
                        View view = View.inflate(MainActivity.this, R.layout.dialog_request_edit, null);
                        if (request != null) {
                            if (request.getReqMethod().equals("POST"))
                                ((RadioButton) view.findViewById(R.id.rb_req_post)).setChecked(true);
                            else
                                ((RadioButton) view.findViewById(R.id.rb_req_get)).setChecked(true);
                            ((EditText) view.findViewById(R.id.et_req_name)).setText(request.getReqName());
                        }

                        return view;
                    }
                }
                        .setTitle("请求 / " + (request == null ? "新增" : "编辑"))
                        .setCanceledOnTouchOutside(false)
                        .addAction("取消", (dialog, index) -> dialog.dismiss())
                        .addAction("确认", (dialog, index) -> {
                            EditText etReqName = dialog.findViewById(R.id.et_req_name);
                            RadioGroup rgMethod = dialog.findViewById(R.id.rg_req_method);
                            RadioButton rbMethod = dialog.findViewById(rgMethod.getCheckedRadioButtonId());
                            Request newReq = request == null ? new Request() : request;
                            newReq.setReqName(etReqName.getText().toString());
                            newReq.setReqMethod(rbMethod.getText().toString());

                            if (newReq.getReqName().equals("") || newReq.getReqMethod().equals("")) {
                                Toast.makeText(MainActivity.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            dialog.dismiss();
                            new Thread(() -> {
                                Message msg = new Message();
                                msg.what = 1;
                                try {
                                    if (request == null) {
                                        newReq.setConnId(connId);
                                        db.getRequestDao().insert(newReq);
                                        msg.obj = "success";
                                    } else {
                                        db.getRequestDao().update(newReq);
                                        msg.obj = "success";
                                    }
                                } catch (Exception ignored) {
                                    msg.obj = "failure";
                                }
                                fillAdapterData();
                                handler.sendMessage(msg);
                            }).start();
                        }).show();
                super.onPostExecute(request);
            }

            @Override
            protected Request doInBackground(Void... voids) {
                return db.getRequestDao().queryById(reqId);
            }
        }.execute();
    }


    @Override
    protected void doOnBackPressed() {

    }

    /**
     * 显示一个connection编辑框
     *
     * @param connId if connId != INVALID_CONNECTION_ID then modify, else create new
     */
    @SuppressLint("StaticFieldLeak")
    private void showConnectionEditDialog(int connId) {
        new AsyncTask<Void, Void, Connection>() {
            @Override
            protected Connection doInBackground(Void... voids) {
                return db.getConnectionDao().queryById(connId);
            }

            @Override
            protected void onPostExecute(Connection conn) {
                new QMUIDialog.CustomDialogBuilder(MainActivity.this) {
                    @Nullable
                    @Override
                    protected View onCreateContent(QMUIDialog dialog, QMUIDialogView parent, Context context) {
                        View view = View.inflate(MainActivity.this, R.layout.dialog_connection_edit, null);
                        if (conn != null) {
                            ((EditText) view.findViewById(R.id.et_conn_name)).setText(conn.getConnName());
                            ((EditText) view.findViewById(R.id.et_conn_default_website)).setText(conn.getDefaultWebsite());
                            ((Switch) view.findViewById(R.id.sw_conn_single_session)).setChecked(conn.isSingleSession());
                        }
                        return view;
                    }
                }
                        .setCanceledOnTouchOutside(false)
                        .setTitle("请求集 / " + (conn == null ? "新增" : "编辑"))
                        .addAction("取消", (dialog, index) -> dialog.dismiss())
                        .addAction("确认", (dialog, index) -> {
                            Connection newConn = conn == null ? new Connection() : conn;
                            EditText etConnName = dialog.findViewById(R.id.et_conn_name);
                            EditText etDefaultWebsite = dialog.findViewById(R.id.et_conn_default_website);
                            Switch swSingleSession = dialog.findViewById(R.id.sw_conn_single_session);

                            newConn.setConnName(etConnName.getText().toString().trim());
                            newConn.setDefaultWebsite(etDefaultWebsite.getText().toString().trim());
                            newConn.setSingleSession(swSingleSession.isChecked());
                            if (newConn.getConnName().equals("")) {
                                Toast.makeText(MainActivity.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            dialog.dismiss();
                            new Thread(() -> {
                                Message msg = new Message();
                                msg.what = 1;
                                try {
                                    if (conn != null) {
                                        db.getConnectionDao().update(newConn);
                                        msg.obj = "success";
                                    } else {
                                        db.getConnectionDao().insert(newConn);
                                        msg.obj = "success";
                                    }
                                } catch (Exception e) {
                                    msg.obj = "failure";
                                }
                                fillAdapterData();
                                handler.sendMessage(msg);
                            }).start();
                        }).show();
            }
        }.execute();
    }

    private void setOriginDatabase() {

        Connection top;
        ConnectionDao connectionDao = db.getConnectionDao();
        RequestDao requestDao = db.getRequestDao();

        requestDao.deleteAll();
        connectionDao.deleteAll();

        Connection conn1 = new Connection();
        conn1.setConnName("木小果API");
        conn1.setDefaultWebsite("api.muxiaoguo.cn");
        conn1.setSingleSession(true);
        connectionDao.insert(conn1);
        top = connectionDao.top();

        Request req11 = new Request();
        req11.setConnId(top.getConnId());
        req11.setReqMethod("GET");
        req11.setReqName("/api/QqInfo");
        req11.setReqPath("/api/QqInfo");
        req11.setReqParams("qq=3364917990");

        Request req12 = new Request();
        req12.setConnId(top.getConnId());
        req12.setReqMethod("GET");
        req12.setReqName("/api/Gushici");
        req12.setReqPath("/api/Gushici");

        Request req13 = new Request();
        req13.setConnId(top.getConnId());
        req13.setReqMethod("POST");
        req13.setReqPath("/api/tianqi");
        req13.setReqName("/api/tianqi");
        req13.setReqParams("city=洛阳&type=1");

        Request req14 = new Request();
        req14.setConnId(top.getConnId());
        req14.setReqMethod("GET");
        req14.setReqPath("/api/163reping");
        req14.setReqName("/api/163reping");

        requestDao.insert(req11, req12, req13, req14);

        Connection conn2 = new Connection();
        conn2.setConnName("星语API");
        conn2.setDefaultWebsite("api.wiiuii.cn");
        conn2.setSingleSession(false);
        connectionDao.insert(conn2);
        top = connectionDao.top();

        Request req21 = new Request();
        req21.setConnId(top.getConnId());
        req21.setReqMethod("GET");
        req21.setReqName("/api/lishi");
        req21.setReqPath("/api/lishi");

        Request req22 = new Request();
        req22.setConnId(top.getConnId());
        req22.setReqMethod("GET");
        req22.setReqName("/api/yiyan");
        req22.setReqPath("/api/yiyan");

        Request req23 = new Request();
        req23.setConnId(top.getConnId());
        req23.setReqMethod("GET");
        req23.setReqName("/api/qqzx");
        req23.setReqPath("/api/qqzx");
        req23.setReqParams("qq=3364917990");

        requestDao.insert(req21, req22, req23);

        Connection conn3 = new Connection();
        conn3.setConnName("Ten·API");
        conn3.setDefaultWebsite("tenapi.cn");
        conn3.setSingleSession(false);
        connectionDao.insert(conn3);
        top = connectionDao.top();

        Request req31 = new Request();
        req31.setConnId(top.getConnId());
        req31.setReqMethod("GET");
        req31.setReqName("/wyyinfo/");
        req31.setReqPath("/wyyinfo/");
        req31.setReqParams("id=400162138");

        Request req32 = new Request();
        req32.setConnId(top.getConnId());
        req32.setReqMethod("GET");
        req32.setReqName("/comment/");
        req32.setReqPath("/comment/");

        Request req33 = new Request();
        req33.setConnId(top.getConnId());
        req33.setReqMethod("GET");
        req33.setReqName("/bilibili/");
        req33.setReqPath("/bilibili/");
        req33.setReqParams("uid=1");

        Request req34 = new Request();
        req34.setConnId(top.getConnId());
        req34.setReqMethod("GET");
        req34.setReqName("/resou/");
        req34.setReqPath("/resou/");

        Request req35 = new Request();
        req35.setConnId(top.getConnId());
        req35.setReqMethod("GET");
        req35.setReqName("/tel/");
        req35.setReqPath("/tel/");
        req35.setReqParams("tel=15515306103");

        requestDao.insert(req31, req32, req33, req34, req35);
    }


    @Override
    protected void onResume() {
        super.onResume();
        fillAdapterData();
    }
}