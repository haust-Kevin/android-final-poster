package com.wdb.poster.ui.components.connections.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qmuiteam.qmui.recyclerView.QMUIRVItemSwipeAction;
import com.qmuiteam.qmui.recyclerView.QMUISwipeAction;
import com.qmuiteam.qmui.widget.section.QMUIDefaultStickySectionAdapter;
import com.qmuiteam.qmui.widget.section.QMUISection;
import com.wdb.poster.R;
import com.wdb.poster.db.api.entity.Connection;
import com.wdb.poster.db.api.entity.Request;
import com.wdb.poster.ui.components.connections.tree.HeaderModel;
import com.wdb.poster.ui.components.connections.tree.ItemModel;

import java.util.List;
import java.util.Map;

public class ConnectionSectionAdapter extends QMUIDefaultStickySectionAdapter<HeaderModel, ItemModel> {
    final static String TAG = "ConnectionSectionAdapter";

    @NonNull
    @Override
    protected ViewHolder onCreateSectionHeaderViewHolder(@NonNull ViewGroup viewGroup) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.components_connections_tree_header, viewGroup, false);
        return new ViewHolder(view);
    }

    @NonNull
    @Override
    protected ViewHolder onCreateSectionItemViewHolder(@NonNull ViewGroup viewGroup) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.components_connections_tree_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindSectionHeader(ViewHolder holder, int position, QMUISection<HeaderModel, ItemModel> section) {
        View view = holder.itemView;
        Connection conn = section.getHeader().getConn();

        TextView tvConnName = view.findViewById(R.id.tv_conn_name);
        TextView tvDefaultWebsite = view.findViewById(R.id.tv_default_website);
        ImageView tvColor = view.findViewById(R.id.iv_color);

        tvConnName.setText(conn.getConnName());
        tvDefaultWebsite.setText(conn.getDefaultWebsite());
        int color_rsId = conn.isSingleSession() ? R.color.connection_left_span_single : R.color.connection_left_span;
        tvColor.setBackgroundColor(view.getResources().getColor(color_rsId));
    }

    @Override
    protected void onBindSectionItem(ViewHolder holder, int position, QMUISection<HeaderModel, ItemModel> section, int itemIndex) {
        View view = holder.itemView;
        Request request = section.getItemAt(itemIndex).getRequest();

        TextView tvMethod = view.findViewById(R.id.tv_method);
        TextView tvReqName = view.findViewById(R.id.tv_request_name);

        tvMethod.setText(request.getReqMethod());
        tvReqName.setText(request.getReqName());
        int color_rsId = "GET".equals(request.getReqMethod()) ? R.color.request_method_get : R.color.request_method_post;
        tvMethod.setTextColor(view.getResources().getColor(color_rsId));
    }
}
