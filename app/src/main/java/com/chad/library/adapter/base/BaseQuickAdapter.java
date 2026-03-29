package com.chad.library.adapter.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class BaseQuickAdapter<T, VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {
    private static final int TYPE_HEADER = Integer.MIN_VALUE;

    private final List<T> data = new ArrayList<>();
    private final Set<Integer> childClickViewIds = new LinkedHashSet<>();
    private final int layoutResId;
    private View headerView;
    private OnItemClickListener onItemClickListener;
    private OnItemChildClickListener onItemChildClickListener;

    protected BaseQuickAdapter(@LayoutRes int layoutResId) {
        this.layoutResId = layoutResId;
    }

    protected abstract void convert(VH holder, T item);

    public List<T> getData() {
        return this.data;
    }

    public int addHeaderView(View view) {
        this.headerView = view;
        notifyDataSetChanged();
        return 0;
    }

    public void addChildClickViewIds(int... viewIds) {
        for (int viewId : viewIds) {
            this.childClickViewIds.add(viewId);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnItemChildClickListener(OnItemChildClickListener listener) {
        this.onItemChildClickListener = listener;
    }

    public void setNewData(List<T> newData) {
        this.data.clear();
        if (newData != null) {
            this.data.addAll(newData);
        }
        notifyDataSetChanged();
    }

    public void addData(T item) {
        this.data.add(item);
        notifyItemInserted((hasHeader() ? 1 : 0) + this.data.size() - 1);
    }

    @Override
    public int getItemCount() {
        return this.data.size() + (this.headerView == null ? 0 : 1);
    }

    @Override
    public int getItemViewType(int position) {
        return hasHeader() && position == 0 ? TYPE_HEADER : 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return (VH) new BaseViewHolder(this.headerView);
        }
        View itemView = LayoutInflater.from(parent.getContext()).inflate(this.layoutResId, parent, false);
        return (VH) new BaseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            return;
        }
        int dataPosition = toDataPosition(position);
        T item = this.data.get(dataPosition);
        convert(holder, item);
        bindClicks(holder, dataPosition);
    }

    private void bindClicks(VH holder, final int dataPosition) {
        holder.itemView.setOnClickListener(null);
        if (this.onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BaseQuickAdapter.this.onItemClickListener.onItemClick(BaseQuickAdapter.this, view, dataPosition);
                }
            });
        }
        for (final int viewId : this.childClickViewIds) {
            View child = holder.getView(viewId);
            if (child != null) {
                child.setOnClickListener(null);
                if (this.onItemChildClickListener != null) {
                    child.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            BaseQuickAdapter.this.onItemChildClickListener.onItemChildClick(BaseQuickAdapter.this, view, dataPosition);
                        }
                    });
                }
            }
        }
    }

    private boolean hasHeader() {
        return this.headerView != null;
    }

    private int toDataPosition(int adapterPosition) {
        return hasHeader() ? adapterPosition - 1 : adapterPosition;
    }
}
