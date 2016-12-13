package com.qianxia.sijia.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qianxia.sijia.R;
import com.qianxia.sijia.constant.Constant;
import com.qianxia.sijia.entry.Food;
import com.qianxia.sijia.listener.OnBitmapLoadedListener;
import com.qianxia.sijia.util.DBUtil;
import com.qianxia.sijia.util.SiJiaImageLoader;
import com.qianxia.sijia.util.TimeUtil;
import com.qianxia.sijia.view.SijiaTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2016/11/18.
 */
public class ShareFoodsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LruCache<String, Bitmap> memoryCache;
    private DBUtil dbUtil;
    private Context mContext;
    private List<Food> mDatas;
    private OnItemClickListener mOnItemClickListener;
    private View headerView;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_HEADER = 2;

    public ShareFoodsAdapter(Context context, List<Food> datas) {
        mContext = context;
        mDatas = datas;
        dbUtil = new DBUtil(mContext);
        memoryCache = new LruCache<String, Bitmap>(Constant.MEMORYCACHE_MAXSIZE) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
        notifyItemInserted(0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (headerView != null && viewType == TYPE_HEADER) {
            return new HeaderViewHolder(headerView);
        }
        if (viewType == TYPE_FOOTER) {
            return new FooterViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_collect_recyclerview_footer, parent, false));

        } else if (viewType == Constant.CARDVIEW_TYPE_WITH_DATE) {
            return new HasDateViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_sharefragment_recycleview_withdate,
                    parent, false));
        } else {
            return new NoDateViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_sharefragment_recycleview_withoutdate,
                    parent, false));
        }

    }

    public Food getItem(int position) {
        return mDatas.get(position);
    }

    public int getRealPosition(RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getLayoutPosition();
        return headerView == null ? position : position - 1;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            return;
        }

        if (holder instanceof FooterViewHolder) {
            ((FooterViewHolder) holder).tvLoading.setText("正在加载更多...");
            return;
        }

        Food food = mDatas.get(position - 1);
        if (food == null) {
            return;
        }
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getRealPosition(holder);
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            });
        }
        NoDateViewHolder vHolder = (NoDateViewHolder) holder;
        vHolder.tvAddress.setText(food.getShop().getAddress());
        int commentNum = 0;
        if (food.getCommentNum() != null) {
            commentNum = food.getCommentNum();
        }
        vHolder.tvCommentNum.setText(String.valueOf(commentNum));
        int likerNum = 0;
        if (food.getLikerNum() != null) {
            likerNum = food.getLikerNum();
        }
        vHolder.tvLikerNum.setText(String.valueOf(likerNum));
        vHolder.tvFoodName.setText(food.getName());
        vHolder.tvShopName.setText(food.getShop().getName());
        String imgUrl = food.getPics().get(0).getUrl();
        SiJiaImageLoader.loadImage(imgUrl, vHolder.ivFoodPic, dbUtil, memoryCache);
        if (vHolder instanceof HasDateViewHolder) {
            try {
//                Log.i("SIJIADEBUG",food.getCreatedAt());
                long date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(food.getCreatedAt()).getTime();
//                Log.i("SIJIADEBUG",date+"");
                ((HasDateViewHolder) vHolder).tvDate.setText(TimeUtil.getTime(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public int getItemCount() {
        return headerView == null ? mDatas.size() : mDatas.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (headerView == null) {
            if (mDatas.get(position) == null) {
                return TYPE_FOOTER;
            }
            if (position == 0) {
                return Constant.CARDVIEW_TYPE_WITH_DATE;
            }
            String dateStr = mDatas.get(position).getCreatedAt().split(" ")[0];
            String prevDateStr = mDatas.get(position - 1).getCreatedAt().split(" ")[0];
            return dateStr.equals(prevDateStr) ? Constant.CARDVIEW_TYPE_WITHOUT_DATE : Constant.CARDVIEW_TYPE_WITH_DATE;
        } else {
            if (position == 0) {
                return TYPE_HEADER;
            }
            if (mDatas.get(position - 1) == null) {
                return TYPE_FOOTER;
            }
            if (position == 1) {
                return Constant.CARDVIEW_TYPE_WITH_DATE;
            }
            String dateStr = mDatas.get(position - 1).getCreatedAt().split(" ")[0];
            String prevDateStr = mDatas.get(position - 2).getCreatedAt().split(" ")[0];
            return dateStr.equals(prevDateStr) ? Constant.CARDVIEW_TYPE_WITHOUT_DATE : Constant.CARDVIEW_TYPE_WITH_DATE;
        }
    }

    public void addAll(List<Food> list, boolean flag) {
        if (flag) {
            mDatas.clear();
        }
        mDatas.addAll(list);
        notifyDataSetChanged();
    }


    public class NoDateViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoodPic;
        TextView tvFoodName, tvShopName, tvAddress;
        SijiaTextView tvCommentNum, tvLikerNum;

        public NoDateViewHolder(View itemView) {
            super(itemView);
            ivFoodPic = (ImageView) itemView.findViewById(R.id.iv_sharefragment_recyclerview_foodpic);
            tvFoodName = (TextView) itemView.findViewById(R.id.tv_sharefragment_recyclerview_foodname);
            tvShopName = (TextView) itemView.findViewById(R.id.tv_sharefragment_recyclerview_shopname);
            tvCommentNum = (SijiaTextView) itemView.findViewById(R.id.tv_sharefragment_recyclerview_commentnum);
            tvLikerNum = (SijiaTextView) itemView.findViewById(R.id.tv_sharefragment_recyclerview_likernum);
            tvAddress = (TextView) itemView.findViewById(R.id.tv_sharefragment_recyclerview_address);
        }
    }

    public class HasDateViewHolder extends NoDateViewHolder {
        TextView tvDate;

        public HasDateViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.tv_sharefragment_recycleview_date);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        TextView tvLoading;

        public FooterViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.prb_collect_footerloading);
            tvLoading = (TextView) itemView.findViewById(R.id.tv_collect_footerloading);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void setOnItemClickLitener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}
