package com.qianxia.sijia.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qianxia.sijia.R;
import com.qianxia.sijia.constant.Constant;
import com.qianxia.sijia.entry.Food;
import com.qianxia.sijia.entry.SijiaUser;
import com.qianxia.sijia.listener.OnBitmapLoadedListener;
import com.qianxia.sijia.util.DBUtil;
import com.qianxia.sijia.util.SiJiaImageLoader;
import com.qianxia.sijia.view.CircleImageView;
import com.qianxia.sijia.view.SijiaTextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2016/9/17.
 */
public class FoodRankListAdapter extends RecyclerView.Adapter<FoodRankListAdapter.ViewHolder> {
    private final LruCache<String, Bitmap> memoryCache;
    private List<Food> mDatas;
    private Context mContext;
    private DBUtil dbUtil;
    private OnItemClickListener mListener;

    public FoodRankListAdapter(Context context, List<Food> datas) {
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

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        final ViewHolder viewHolder;
//        if(convertView==null){
//            convertView = mInflater.inflate(R.layout.item_food_rank_listview,parent,false);
//            viewHolder = new ViewHolder(convertView);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        Food food = getItem(position);
//        viewHolder.tvAddress.setText(food.getShop().getAddress());
//        int commentNum =0;
//        if(food.getCommentNum()!=null){
//            commentNum = food.getCommentNum();
//        }
//        viewHolder.tvCommentNum.setText(String.valueOf(commentNum));
//        viewHolder.ratingBar.setRating(food.getRating());
//        viewHolder.tvShopName.setText(food.getShop().getName());
//        viewHolder.tvFoodName.setText(food.getName());
//        viewHolder.tvFoodBrief.setText(food.getDescription());
//        viewHolder.tvLikeNum.setText(String.valueOf(food.getLikerNum()));
//        viewHolder.tvPrice.setText(String.valueOf(food.getPrice()));
//        List<BmobFile> list = food.getPics();
//        if(list!=null&&list.size()>0){
//            String imgUrl=list.get(0).getUrl();
//            if(dbUtil.isBitmapExist(imgUrl)){
//                dbUtil.getBitmap(imgUrl, new OnBitmapLoadedListener() {
//                    @Override
//                    public void onBitmapLoaded(Bitmap bitmap) {
//                        viewHolder.ivFoodImg.setImageBitmap(bitmap);
//                    }
//                });
//            } else {
//                ImageLoader.getInstance().displayImage(imgUrl, viewHolder.ivFoodImg, new ImageLoadingListener() {
//                    @Override
//                    public void onLoadingStarted(String s, View view) {
//
//                    }
//
//                    @Override
//                    public void onLoadingFailed(String s, View view, FailReason failReason) {
//
//                    }
//
//                    @Override
//                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
//                        dbUtil.saveBitmap(s,bitmap);
//                    }
//
//                    @Override
//                    public void onLoadingCancelled(String s, View view) {
//
//                    }
//                });
//            }
//        }else{
//            viewHolder.ivFoodImg.setImageResource(R.drawable.food_sample);
//        }
//
//
//        return convertView;
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_food_rank_listview, parent, false);
        return new ViewHolder(view);
    }

    public void addAll(List<Food> list, boolean flag) {
        if (list == null) {
            return;
        }
        if (flag) {
            mDatas.clear();
        }
        mDatas.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Food food = mDatas.get(position);
        if (mListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(holder.itemView, holder.getLayoutPosition());
                }
            });
        }

//        holder.tvAddress.setText(food.getShop().getAddress());
        int commentNum = 0;
        if (food.getCommentNum() != null) {
            commentNum = food.getCommentNum();
        }
        holder.tvCommentNum.setText(String.valueOf(commentNum));
//        holder.ratingBar.setRating(food.getRating());
        holder.tvShopName.setText(food.getShop().getName());
//        holder.tvFoodName.setText(food.getName());
        holder.tvFoodBrief.setText(food.getDescription());
        holder.tvLikeNum.setText(String.valueOf(food.getLikerNum()));
//        holder.tvPrice.setText(String.valueOf(food.getPrice()));
        SijiaUser user = food.getAuthor();
        String username = "Test";
        String avatar = null;
        if (user != null) {
            username = user.getUsername();
            avatar = user.getAvatar();
        }
        holder.tvUserName.setText("by  " + username);
        if (TextUtils.isEmpty(avatar)) {
            holder.ivAtavar.setImageResource(R.drawable.food_sample);
        } else {
            SiJiaImageLoader.loadImage(avatar, holder.ivAtavar, dbUtil, memoryCache);
        }
        List<BmobFile> list = food.getPics();
        if (list != null && list.size() > 0) {
            String imgUrl = list.get(0).getUrl();
            SiJiaImageLoader.loadImage(imgUrl, holder.ivFoodImg, dbUtil, memoryCache);
        } else {
            holder.ivFoodImg.setImageResource(R.drawable.food_sample);
        }

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        //        @Bind(R.id.tv_foodrank_listview_item_foodname)
//        TextView tvFoodName;
//        @Bind(R.id.rb_foodrank_listview_item)
//        RatingBar ratingBar;
        @Bind(R.id.tv_foodrank_listview_item_shopname)
        TextView tvShopName;
        @Bind(R.id.iv_foodrank_listview_item_foodimg)
        ImageView ivFoodImg;
        @Bind(R.id.tv_foodrank_listview_item_foodbrief)
        TextView tvFoodBrief;
        //        @Bind(R.id.tv_foodrank_listview_item_address)
//        TextView tvAddress;
        @Bind(R.id.tv_foodrank_listview_item_commentnum)
        SijiaTextView tvCommentNum;
        @Bind(R.id.tv_foodrank_listview_item_likenum)
        SijiaTextView tvLikeNum;
        //        @Bind(R.id.tv_foodrank_listview_item_price)
//        TextView tvPrice;
        @Bind(R.id.iv_foodrank_listview_item_ivavatar)
        CircleImageView ivAtavar;
        @Bind(R.id.tv_foodrank_listview_item_username)
        TextView tvUserName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

//        public ViewHolder(View convertView){
//            ButterKnife.bind(this,convertView);
//        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
