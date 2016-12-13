package com.qianxia.sijia.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qianxia.sijia.R;
import com.qianxia.sijia.constant.Constant;
import com.qianxia.sijia.entry.SubCategoryTable;
import com.qianxia.sijia.listener.OnBitmapLoadedListener;
import com.qianxia.sijia.util.DBUtil;
import com.qianxia.sijia.util.SiJiaImageLoader;
import com.qianxia.sijia.view.CircleImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tarena on 2016/9/13.
 */
public class SijiaSubCategoryAdapter extends SijiaBaseListAdapter<SubCategoryTable> {

    private final LruCache<String, Bitmap> memoryCache;
    private DBUtil dbUtil;

    public SijiaSubCategoryAdapter(Context context, List<SubCategoryTable> datas) {
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
        final ViewHolder vHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_food_popupwindow, null);
            vHolder = new ViewHolder(convertView);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }
        SubCategoryTable subCategoryTable = getItem(position);
        vHolder.tvName.setText(subCategoryTable.getName());
        String imgUrl = subCategoryTable.getImgUrl();

        Bitmap bitmap = memoryCache.get(imgUrl);
        if (bitmap == null) {
            dbUtil.getBitmap(imgUrl, new OnBitmapLoadedListener() {
                @Override
                public void onBitmapLoaded(String imgUrl, Bitmap bitmap) {
                    memoryCache.put(imgUrl, bitmap);
                    vHolder.ivPic.setImageBitmap(bitmap);
                }
            });
        } else {
            vHolder.ivPic.setImageBitmap(bitmap);
        }

//        SiJiaImageLoader.loadImage(imgUrl,vHolder.ivPic,dbUtil,memoryCache);
//        SubCategory categroy = (SubCategory) getItem(position);
//        int photoId=categroy.getPhotoId();
//        ImageLoaderUtils.imageLoadFromResource(mContext,vHolder.photoImg,photoId,40f,40f);
//        vHolder.nameTv.setText(categroy.getBrief());
//        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//        vHolder.nameTv.measure(w,h);
//        int tvWidth = vHolder.nameTv.getMeasuredWidth();
//        int imgSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,48,mContext.getResources().getDisplayMetrics());
//        int width=tvWidth+imgSize;
////        Log.i("TAG",""+tvWidth+"  "+imgSize);
//        view.setLayoutParams(new ViewGroup.LayoutParams(width,imgSize));
        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.iv_foodfragment_item_subcategorypic)
        CircleImageView ivPic;
        @Bind(R.id.tv_foodfragment_item_subcategoryname)
        TextView tvName;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }
    }
}
