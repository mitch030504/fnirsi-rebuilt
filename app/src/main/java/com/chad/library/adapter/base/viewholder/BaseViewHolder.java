package com.chad.library.adapter.base.viewholder;

import android.util.SparseArray;
import android.view.View;
import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;

public class BaseViewHolder extends RecyclerView.ViewHolder {
    private final SparseArray<View> views = new SparseArray<>();

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getView(@IdRes int viewId) {
        View view = this.views.get(viewId);
        if (view == null) {
            view = this.itemView.findViewById(viewId);
            this.views.put(viewId, view);
        }
        return (T) view;
    }

    public BaseViewHolder setText(@IdRes int viewId, CharSequence value) {
        View view = getView(viewId);
        if (view instanceof android.widget.TextView) {
            ((android.widget.TextView) view).setText(value);
        }
        return this;
    }
}
