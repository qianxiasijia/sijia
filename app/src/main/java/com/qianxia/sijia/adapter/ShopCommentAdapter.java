package com.qianxia.sijia.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.LruCache;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qianxia.sijia.R;
import com.qianxia.sijia.constant.Constant;
import com.qianxia.sijia.entry.FoodComment;
import com.qianxia.sijia.entry.FoodReply;
import com.qianxia.sijia.entry.Shop;
import com.qianxia.sijia.entry.ShopComment;
import com.qianxia.sijia.entry.ShopReply;
import com.qianxia.sijia.entry.SijiaUser;
import com.qianxia.sijia.fragment.DeleteDialogFrament;
import com.qianxia.sijia.listener.OnBitmapLoadedListener;
import com.qianxia.sijia.listener.OnDeleteCommentListener;
import com.qianxia.sijia.listener.OnDialogClickListener;
import com.qianxia.sijia.listener.OnFoodReplayLoadedListener;
import com.qianxia.sijia.listener.OnReplayListener;
import com.qianxia.sijia.listener.OnShopReplayLoadedListener;
import com.qianxia.sijia.manager.CommentManager;
import com.qianxia.sijia.manager.ReplayManager;
import com.qianxia.sijia.ui.FoodDetailActivity;
import com.qianxia.sijia.ui.ShopDetailActivity;
import com.qianxia.sijia.ui.ShowBigImageActivity;
import com.qianxia.sijia.util.DBUtil;
import com.qianxia.sijia.util.EmoUtil;
import com.qianxia.sijia.util.SiJiaImageLoader;
import com.qianxia.sijia.util.TimeUtil;
import com.qianxia.sijia.view.CircleImageView;
import com.qianxia.sijia.view.SijiaRepalyView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.im.BmobUserManager;

/**
 * Created by Administrator on 2016/11/14.
 */
public class ShopCommentAdapter extends SijiaBaseListAdapter<ShopComment> {
    private final LruCache<String, Bitmap> memoryCache;
    private OnReplayListener<ShopComment> listener;
    private SijiaUser currentUser;
    private DBUtil dbUtil;
    private static final int EMPTY_TYPE = 0;
    private static final int NOMARL_TYPE = 1;
    private OnDeleteCommentListener deleteCommentListener;

    public ShopCommentAdapter(Context context, List datas, OnReplayListener<ShopComment> listener) {
        super(context, datas);
        dbUtil = new DBUtil(context);
        this.listener = listener;
        currentUser = BmobUserManager.getInstance(context).getCurrentUser(SijiaUser.class);
        memoryCache = new LruCache<String, Bitmap>(Constant.MEMORYCACHE_MAXSIZE) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        ShopComment comment = getItem(position);
        if (position == 0 && comment.getTime() < 0) {
            return EMPTY_TYPE;
        } else {
            return NOMARL_TYPE;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void setDeleteCommentListener(OnDeleteCommentListener listener) {
        deleteCommentListener = listener;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ShopComment comment = getItem(position);
        final ViewHolder viewHolder;

        if (convertView == null) {
            if (position == 0 && comment.getTime() < 0) {
                convertView = mInflater.inflate(R.layout.item_refresh_emptyview, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.tvEmpty = (TextView) convertView.findViewById(R.id.tv_refreshlistview_empty);
                convertView.setTag(viewHolder);
            } else {
                convertView = mInflater.inflate(R.layout.item_foodetail_refreshlistview_comment, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.ivAvatar = (CircleImageView) convertView.findViewById(R.id.iv_fooddetail_item_avatar);
                viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_fooddetail_item_username);
                viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_fooddetail_item_time);
                viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_fooddetail_item_content);
                viewHolder.imgsContainer = (RelativeLayout) convertView.findViewById(R.id.rl_fooddetail_item_imgscontainer);
                viewHolder.tvDelete = (TextView) convertView.findViewById(R.id.tv_fooddetail_item_delete);
                viewHolder.tvReplay = (TextView) convertView.findViewById(R.id.tv_fooddetail_item_replay);
                viewHolder.replayContainer = (LinearLayout) convertView.findViewById(R.id.ll_fooddetail_item_replaycontainer);
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position != 0 || comment.getTime() > 0) {
            String avatarStr = comment.getAuthor().getAvatar();
            if (TextUtils.isEmpty(avatarStr)) {
                viewHolder.ivAvatar.setImageResource(R.drawable.icon_my_photo);
            } else {
                SiJiaImageLoader.loadImage(avatarStr, viewHolder.ivAvatar, dbUtil, memoryCache);
            }
            viewHolder.tvUserName.setText(comment.getAuthor().getUsername());
//        Log.i("DEBUG","username:"+comment.getAuthor().toString());
            viewHolder.tvContent.setText(EmoUtil.getSpannableString(comment.getContent()));
            viewHolder.tvTime.setText(TimeUtil.getTime(comment.getTime()));

            viewHolder.imgsContainer.removeAllViews();
            String imgUrls = comment.getPics();
            if (!TextUtils.isEmpty(imgUrls)) {
//            Log.i("DEBUG",imgUrls.length()+"");
                viewHolder.imgsContainer.setVisibility(View.VISIBLE);
                showCommentPics(imgUrls, viewHolder.imgsContainer);
            }

            viewHolder.replayContainer.removeAllViews();


            if (currentUser != null && currentUser.getUsername().equals(comment.getAuthor().getUsername())) {
                viewHolder.tvDelete.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tvDelete.setVisibility(View.INVISIBLE);
            }

            showReplays(comment, viewHolder.replayContainer);

            viewHolder.tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCommentListener.onDeleteComment(position);
                }
            });
            viewHolder.tvReplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUser == null) {
                        toast("需要先登录哦~");
                        return;
                    }
                    String title = "";
                    SijiaUser user = BmobUserManager.getInstance(mContext).getCurrentUser(SijiaUser.class);
                    log(user.toString() + ":" + comment.getAuthor().toString());
                    if (user != null && user.getUsername().equals(comment.getAuthor().getUsername())) {
                        title = user.getUsername();
                    } else {
                        title = user.getUsername() + "&" + comment.getAuthor().getUsername();
                    }
                    listener.onReplay(comment, title);
                }
            });
        }


        return convertView;
    }

    private void showReplays(ShopComment comment, final LinearLayout replayContainer) {

        ReplayManager.getShopReplays(mContext, comment, new OnShopReplayLoadedListener() {
            @Override
            public void onShopReplayLoaded(List<ShopReply> list) {
                if (list.size() > 0) {
                    for (ShopReply reply : list) {
                        SijiaRepalyView repalyView = new SijiaRepalyView(mContext, reply);
                        replayContainer.addView(repalyView);
                    }
                }
            }
        });
    }

    private void showCommentPics(String imgUrls, RelativeLayout imgsContainer) {
//        Log.i("DEBUG","imgUrls:"+imgUrls);
        final String[] urls = imgUrls.split("&");
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, mContext.getResources().getDisplayMetrics());
        int span = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72, mContext.getResources().getDisplayMetrics());
        int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        int size = (screenWidth - span - margin * 2) / 3;
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, mContext.getResources().getDisplayMetrics());
        for (int i = 0; i < urls.length; i++) {
            final ImageView iv = new ImageView(mContext);
            iv.setId(i + 1);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
            if (i % 3 != 0) {
                params.addRule(RelativeLayout.RIGHT_OF, i);
                params.leftMargin = margin;
            }
            if (i >= 3) {
                params.addRule(RelativeLayout.BELOW, i - 2);
                params.topMargin = margin;
            }
            iv.setLayoutParams(params);
            iv.setBackgroundResource(R.drawable.input_bg);
            iv.setPadding(padding, padding, padding, padding);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            SiJiaImageLoader.loadImage(urls[i], iv, dbUtil, memoryCache);

            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> list = new ArrayList<String>();
                    for (String url : urls) {
                        list.add(url);
                    }
                    Intent intent = new Intent(mContext, ShowBigImageActivity.class);
                    intent.putStringArrayListExtra("imgPaths", list);
                    intent.putExtra("from", "comment");
                    mContext.startActivity(intent);
                }
            });

            imgsContainer.addView(iv);

        }

    }


    class ViewHolder {
        CircleImageView ivAvatar;
        TextView tvUserName;
        TextView tvTime;
        TextView tvContent;
        RelativeLayout imgsContainer;
        TextView tvDelete;
        TextView tvReplay;
        LinearLayout replayContainer;
        TextView tvEmpty;
    }
}
