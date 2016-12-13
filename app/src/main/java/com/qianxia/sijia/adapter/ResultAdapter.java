package com.qianxia.sijia.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qianxia.sijia.R;
import com.qianxia.sijia.entry.Collect;
import com.qianxia.sijia.entry.SearchResult;
import com.qianxia.sijia.util.PinYinUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2016/11/24.
 */
public class ResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SearchResult> mDatas;
    private Context mContext;
    private OnItemSelectListener mListener;

    public ResultAdapter(Context context, List<SearchResult> datas) {
        mDatas = datas;
        mContext = context;
    }

    public void addAll(List<SearchResult> list, boolean flag) {
        if (list == null) {
            return;
        }
        if (flag) {
            mDatas.clear();
//            notifyItemRangeRemoved(0,mDatas.size());
        }
        Collections.sort(list, new Comparator<SearchResult>() {
            @Override
            public int compare(SearchResult lhs, SearchResult rhs) {
                String lhsPY = PinYinUtil.getPinYin(lhs.getName());
                String rhsPY = PinYinUtil.getPinYin(rhs.getName());
                if (lhsPY.charAt(0) == rhsPY.charAt(0)) {
                    return lhs.getName().compareTo(rhs.getName());
                } else {
                    return lhsPY.toUpperCase().compareTo(rhsPY.toUpperCase());
                }
            }
        });
        mDatas.addAll(list);
        notifyDataSetChanged();
    }

    public SearchResult getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_result, parent, false);
        ResultViewHolder viewHolder = new ResultViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((ResultViewHolder) holder).tvResult.setText(mDatas.get(position).getName());
        if (mListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mListener.onItemSelect(holder.itemView, pos);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mListener.onItemLongClick(holder.itemView, pos);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView tvResult;

        public ResultViewHolder(View itemView) {
            super(itemView);
            tvResult = (TextView) itemView.findViewById(R.id.tv_search_result);
        }
    }

    public void setOnItemSelectListener(OnItemSelectListener listener) {
        mListener = listener;
    }

    public interface OnItemSelectListener {
        void onItemSelect(View view, int position);

        void onItemLongClick(View view, int position);
    }
}
