package com.wdb.poster.ui.components.connections.tree;

import androidx.room.util.StringUtil;

import com.qmuiteam.qmui.widget.section.QMUISection;
import com.wdb.poster.db.api.entity.Connection;

public class HeaderModel implements QMUISection.Model<HeaderModel> {


    private final Connection conn;

    public HeaderModel(Connection conn) {
        this.conn = conn;
    }

    public Connection getConn() {
        return conn;
    }

    @Override
    public HeaderModel cloneForDiff() {
        return new HeaderModel(conn);
    }

    @Override
    public boolean isSameItem(HeaderModel other) {
        return conn == other.conn;
    }

    @Override
    public boolean isSameContent(HeaderModel other) {
        return conn.getConnId() == other.getConn().getConnId()
                && conn.getConnName().equals(other.getConn().getConnName())
                && conn.getDefaultWebsite().equals(other.getConn().getDefaultWebsite());
    }
}
