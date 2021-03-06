package com.qianxia.sijia.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qianxia.sijia.R;
import com.qianxia.sijia.constant.Constant;
import com.qianxia.sijia.entry.Collect;
import com.qianxia.sijia.listener.OnBitmapLoadedListener;
import com.qianxia.sijia.util.DBUtil;
import com.qianxia.sijia.util.SiJiaImageLoader;
import com.qianxia.sijia.util.TimeUtil;
import com.qianxia.sijia.view.CircleImageView;

import java.util.List;

import butterknife.Bind;

/**
 * Created by Administrator on 2016/11/23.
 */
public class CollectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Collect> mDatas;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private OnItemClickListener mListener;
    private DBUtil dbUtil;
    private LruCache<String, Bitmap> memoryCache;

    public CollectAdapter(Context context, List<Collect> datas) {
        mContext = context;
        mDatas = datas;
        dbUtil = new DBUtil(context);
        memoryCache = new LruCache<String, Bitmap>(Constant.MEMORYCACHE_MAXSIZE) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_collect_recyclerview, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_collect_recyclerview_footer, parent, false);
            return new FooterViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterViewHolder) {
            ((FooterViewHolder) holder).tvLoading.setText("正在加载更多...");
            return;
        } else if (holder instanceof ItemViewHolder) {
            Collect collect = mDatas.get(position);
            ((ItemViewHolder) holder).tvDate.setText("收藏于" + TimeUtil.getTime(collect.getTime()));
            ((ItemViewHolder) holder).tvName.setText(collect.getName());
            ((ItemViewHolder) holder).tvContent.setText(collect.getContent());
            final String imgUrl = collect.getImgUrl();
            SiJiaImageLoader.loadImage(imgUrl, ((ItemViewHolder) holder).ivPic, dbUtil, memoryCache);

            if (mListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getLayoutPosition();
                        mListener.onItemClick(holder.itemView, pos);
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int pos = holder.getLayoutPosition();
                        mListener.onItemLongClick(holder.itemView, pos);
                        return false;
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (mDatas.get(position) == null) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }

    }

    public void addAll(List<Collect> list, boolean flag) {
        if (list == null) {
            return;
        }
        if (flag) {
            mDatas.clear();
        }
        mDatas.addAll(list);
        notifyDataSetChanged();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivPic;
        TextView tvDate;
        TextView tvName;
        TextView tvContent;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ivPic = (CircleImageView) itemView.findViewById(R.id.iv_collect_pic);
            tvDate = (TextView) itemView.findViewById(R.id.tv_collect_date);
            tvName = (TextView) itemView.findViewById(R.id.tv_collect_name);
            tvContent = (TextView) itemView.findViewById(R.id.tv_collect_content);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        TextView tvLoading;

        public FooterViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.prb_collect_footerloading);
            tvLoading = (TextView) itemView.findViewById(R.id.tv_collect_footerloading);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListner(OnItemClickListener listner) {
        mListener = listner;
    }
}
