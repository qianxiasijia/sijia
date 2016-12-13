package com.qianxia.sijia.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.TypedValue;

/**
 * Created by Administrator on 2016/9/11.
 */
public class BitmapCompressUtils {
    private static Bitmap decodeImage(Context context, String imgPath, int ResourceId, float reqWidth, float reqHeight) {
        //将输入的dp单位转换为px单位值；
//        reqWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,reqWidth,context.getResources().getDisplayMetrics())/4;
//        reqHeight =TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,reqHeight,context.getResources().getDisplayMetrics())/4;
//        Log.i("TAG","reqWidth="+reqWidth+" "+"reqHeight="+reqHeight);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //判断是从外部路径还是从res资源处读取图片；
        if (imgPath != null) {
            BitmapFactory.decodeFile(imgPath, options);
        } else {
            BitmapFactory.decodeResource(context.getResources(), ResourceId, options);
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateDecodeRatio(options, reqWidth, reqHeight);
        Bitmap bitmap = null;
        if (imgPath != null) {
            bitmap = BitmapFactory.decodeFile(imgPath, options);
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), ResourceId, options);
        }
//        Log.i("TAG:bitmap"," "+bitmap.getWidth()+" "+bitmap.getHeight()+" "+bitmap.getByteCount());
        return bitmap;
    }

    private static int calculateDecodeRatio(BitmapFactory.Options options, float reqWidth, float reqHeight) {
        int rate = 1;
        int mWidth = options.outWidth;
        int mHeight = options.outHeight;
//        Log.i("TAG","mWidth="+mWidth+" mHeight"+mHeight);
        if (mWidth >= reqWidth || mHeight >= reqHeight) {
            if (mWidth <= mHeight) {
                rate = Math.round((float) mHeight / (float) reqHeight);
            } else {
                rate = Math.round((float) mWidth / reqWidth);
            }
        }
//        Log.i("DecodeImageUtil", "rate="+rate);
        return rate;
    }

    public static Bitmap decodeImageFromPath(Context context, String imgPath, float reqWidth, float reqHeight) {
        Bitmap bitmap = decodeImage(context, imgPath, -1, reqWidth, reqHeight);
        return bitmap;
    }

    public static Bitmap decodeImageFromResource(Context context, int ResourceId, float reqWidth, float reqHeight) {
        Bitmap bitmap = decodeImage(context, null, ResourceId, reqWidth, reqHeight);
        return bitmap;
    }

}
