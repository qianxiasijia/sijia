package com.qianxia.sijia.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qianxia.sijia.R;
import com.qianxia.sijia.constant.Constant;
import com.qianxia.sijia.entry.MainCategoryTable;
import com.qianxia.sijia.listener.OnBitmapLoadedListener;
import com.qianxia.sijia.util.DBUtil;
import com.qianxia.sijia.util.SiJiaImageLoader;
import com.qianxia.sijia.view.CircleImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/1.
 */
public class MainCategoryAdapter extends SijiaBaseListAdapter<MainCategoryTable> {
    private final LruCache<String, Bitmap> memoryCache;
    private DBUtil dbUtil;

    public MainCategoryAdapter(Context context, List<MainCategoryTable> datas) {
        super(context, datas);
        dbUtil = new DBUtil(context);
        memoryCache = new LruCache<String, Bitmap>(Constant.MEMORYCACHE_MAXSIZE) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_foodfragment_listview, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MainCategoryTable mainCategoryTable = getItem(position);
        viewHolder.tvName.setText(mainCategoryTable.getName());
        String imgUrl = mainCategoryTable.getImgUrl();
        Bitmap bitmap = memoryCache.get(imgUrl);
        if (bitmap == null) {
            dbUtil.getBitmap(imgUrl, new OnBitmapLoadedListener() {
                @Override
                public void onBitmapLoaded(String imgUrl, Bitmap bitmap) {
                    memoryCache.put(imgUrl, bitmap);
                    viewHolder.ivPhoto.setImageBitmap(bitmap);
                }
            });
        } else {
            viewHolder.ivPhoto.setImageBitmap(bitmap);
        }


        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        viewHolder.tvName.measure(w, h);
        int tvWidth = viewHolder.tvName.getMeasuredWidth();
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, mContext.getResources().getDisplayMetrics());
        int imgSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, mContext.getResources().getDisplayMetrics());
        convertView.setLayoutParams(new AbsListView.LayoutParams(tvWidth + imgSize + padding, imgSize + padding));

        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.iv_foodfragment_item_categoryphoto)
        CircleImageView ivPhoto;
        @Bind(R.id.tv_foodfragment_item_categoryname)
        TextView tvName;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }
    }
}
