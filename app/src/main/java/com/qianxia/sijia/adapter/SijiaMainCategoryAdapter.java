//package com.qianxia.sijia.adapter;
//
//import android.content.Context;
//import android.util.TypedValue;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.qianxia.sijia.R;
//import com.qianxia.sijia.entry.MainCategory;
//import com.qianxia.sijia.utils.ImageLoaderUtils;
//
///**
// * Created by tarena on 2016/9/9.
// */
//public class SijiaMainCategoryAdapter extends SijiaBaseListAdapter<MainCategory> {
//    public SijiaMainCategoryAdapter(Context context) {
//        super(context);
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View view =null;
//        ViewHolder vHolder=null;
//        if(convertView==null){
//            view = mInflater.inflate(R.header1_fooddetail_refreshlsv.item_maincategory_listview,null);
//            vHolder=new ViewHolder();
//            vHolder.photoImg= (ImageView) view.findViewById(R.id.foodCategoryMainPhotoImg);
//            vHolder.nameTv = (TextView) view.findViewById(R.id.foodCategoryMainBriefTv);
//            view.setTag(vHolder);
//        }else{
//            view=convertView;
//            vHolder= (ViewHolder) view.getTag();
//        }
////        MainCategory categroy = (MainCategory) getItem(position);
////        int photoId=categroy.getPhotoId();
////        ImageLoaderUtils.imageLoadFromResource(mContext,vHolder.photoImg,photoId,40f,40f);
////        vHolder.nameTv.setText(categroy.getBrief());
////        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
////        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
////        vHolder.nameTv.measure(w,h);
////        int tvWidth = vHolder.nameTv.getMeasuredWidth();
////        int imgSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,48,mContext.getResources().getDisplayMetrics());
////        int width=tvWidth+imgSize;
//////        Log.i("TAG",""+tvWidth+"  "+imgSize);
////        view.setLayoutParams(new ViewGroup.LayoutParams(width,imgSize));
//        return view;
//    }
//
//    class ViewHolder{
//        private ImageView photoImg;
//        private TextView nameTv;
//    }
//}
