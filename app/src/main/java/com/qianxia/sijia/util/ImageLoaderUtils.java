package com.qianxia.sijia.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2016/9/11.
 */
public class ImageLoaderUtils {
    private static final String NOT_PAHT = "NOTPATH";

    private static void imageLoad(Context context, ImageView img, String imgPath, int ResourceId, float reqWidth, float reqHeight) {
        if (!cancelTask(img, imgPath, ResourceId)) {
            return;
        }
//        reqWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,reqWidth,context.getResources().getDisplayMetrics());
//        reqHeight =TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,reqHeight,context.getResources().getDisplayMetrics());
        ImgLoadTask task = new ImgLoadTask(context, img);
        ThreadDrawable tDrawable = new ThreadDrawable(task);
        img.setImageDrawable(tDrawable);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imgPath, ResourceId, reqWidth, reqHeight);
    }

    private static boolean cancelTask(ImageView img, String imgPath, int ResourceId) {
        ImgLoadTask task = getTask(img);
        if (task != null) {
            String path = task.imgPath;
            int resource = task.ResourceId;
            if (path != null && path.equals(NOT_PAHT) && (resource == ResourceId)) {
                return false;
            } else if (path != null && path.equals(imgPath)) {
                return false;
            } else {
                task.cancel(true);
                return true;
            }
        }
        return true;
    }

    private static ImgLoadTask getTask(ImageView img) {
        if (img == null) {
            return null;
        }
        Drawable drawable = img.getDrawable();
        if (drawable instanceof ThreadDrawable) {
            return ((ThreadDrawable) drawable).getTask();
        }
        return null;
    }

    static class ThreadDrawable extends BitmapDrawable {
        private WeakReference<ImgLoadTask> wTask;

        public ThreadDrawable(ImgLoadTask task) {
            wTask = new WeakReference<ImgLoadTask>(task);
        }

        public ImgLoadTask getTask() {
            return wTask.get();
        }
    }

    static class ImgLoadTask extends AsyncTask<Object, Void, Bitmap> {
        private WeakReference<ImageView> wImg;
        private WeakReference<Context> wContext;
        private String imgPath;
        private int ResourceId;

        public ImgLoadTask(Context context, ImageView img) {
            wImg = new WeakReference<ImageView>(img);
            wContext = new WeakReference<Context>(context);
        }

        @Override
        protected Bitmap doInBackground(Object... params) {
            imgPath = (String) params[0];
            ResourceId = (int) params[1];
            float reqWidth = (float) params[2];
            float reqHeight = (float) params[3];
            Context mContext = wContext.get();
            Bitmap bitmap = null;
            if (NOT_PAHT.equals(imgPath) && (mContext != null)) {
                bitmap = BitmapCompressUtils.decodeImageFromResource(mContext, ResourceId, reqWidth, reqHeight);
            } else if (mContext != null) {
                bitmap = BitmapCompressUtils.decodeImageFromPath(mContext, imgPath, reqWidth, reqHeight);
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
//            Log.i("TAG",""+bitmap.getByteCount());
            if (isCancelled()) {
                bitmap = null;
            }
            if (bitmap != null && wImg != null) {
                final ImageView imageView = wImg.get();
                ImgLoadTask task = getTask(imageView);
                if (this == task && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    public static void imageLoadFromPath(Context context, ImageView img, String imgPath, float reqWidth, float reqHeight) {
        imageLoad(context, img, imgPath, -1, reqWidth, reqHeight);
    }

    public static void imageLoadFromResource(Context context, ImageView img, int ResourceId, float reqWidth, float reqHeight) {
        imageLoad(context, img, NOT_PAHT, ResourceId, reqWidth, reqHeight);
    }
}
