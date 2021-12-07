package com.wdb.poster.ui.components.connections.tree;


import com.qmuiteam.qmui.widget.section.QMUISection;
import com.wdb.poster.db.api.entity.Request;

public class ItemModel implements QMUISection.Model<ItemModel> {

    private final Request request;

    public ItemModel(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

    @Override
    public ItemModel cloneForDiff() {
        return new ItemModel(request);
    }

    @Override
    public boolean isSameItem(ItemModel other) {
        return request == other.request;
    }

    @Override
    public boolean isSameContent(ItemModel other) {
        return request.getReqId() == other.getRequest().getReqId()
                && request.getReqName().equals(other.getRequest().getReqName())
                && request.getReqMethod().equals(other.getRequest().getReqMethod());
    }
}
