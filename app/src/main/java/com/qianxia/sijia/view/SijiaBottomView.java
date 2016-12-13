package com.qianxia.sijia.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.qianxia.sijia.R;

/**
 * Created by tarena on 2016/10/21.
 */

public class SijiaBottomView extends View {

    private Paint textPaint;
    private Paint drawablePaint;
    private Drawable drawable;
    private int color;
    private int textSize;
    private int drawableSize;
    private int alpha;
    private Bitmap bitmap;
    private String text;

    public SijiaBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //将xml布局文件的属性赋给自定义iew的属性
        initAttrs(context, attrs);
        initPaint();
    }

    private void initPaint() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        drawablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SijiaBottomView);
        drawable = array.getDrawable(R.styleable.SijiaBottomView_bv_drawable);
        color = array.getColor(R.styleable.SijiaBottomView_bv_color, Color.BLACK);
        textSize = array.getDimensionPixelSize(R.styleable.SijiaBottomView_bv_size,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        drawableSize = array.getDimensionPixelSize(R.styleable.SijiaBottomView_bv_drawable_size,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics()));
        bitmap = ((BitmapDrawable) drawable).getBitmap();
        text = array.getString(R.styleable.SijiaBottomView_bv_text);
        bitmap = bitmap.createScaledBitmap(bitmap, drawableSize, drawableSize, true);
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //如果宽度和高度都指定为WrapContent时进行处理
        //图片的宽度为左右边距，再加上图片和文字的宽度取大值


        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST) {
            int leftPadding = getPaddingLeft();
            int rightPadding = getPaddingRight();
            Rect bounds = new Rect();
            textPaint.getTextBounds(text, 0, text.length(), bounds);
            int widthSize = leftPadding + rightPadding + Math.max(bounds.width(), bitmap.getWidth());
            widthMeasureSpec = widthSize;
        }

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            //图片的高度为上下内边距加上图片和文字高度
            int topPadding = getPaddingTop();
            int bottomPadding = getPaddingBottom();
            Rect bounds = new Rect();
            textPaint.getTextBounds(text, 0, text.length(), bounds);
            int heightSize = topPadding + bottomPadding + bitmap.getHeight() + bounds.height();
            heightMeasureSpec = heightSize;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制原图片
        super.onDraw(canvas);
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);

        float left = getWidth() / 2 - bitmap.getWidth() / 2;
        float top = getHeight() / 2 - (bitmap.getHeight() + bounds.height()) / 2;
        canvas.drawBitmap(bitmap, left, top, null);

        float x = getWidth() / 2 - bounds.width() / 2;
        float y = getHeight() / 2 + (bitmap.getHeight() + bounds.height() + 16) / 2;
        textPaint.setColor(Color.GRAY);
        canvas.drawText(text, x, y, textPaint);


        drawColorText(canvas, x, y);
        drawColorBitmap(canvas, left, top);

    }

    /**
     * 绘制彩色文字
     *
     * @param canvas
     * @param x
     * @param y
     */
    private void drawColorText(Canvas canvas, float x, float y) {
        textPaint.setColor(color);
        textPaint.setAlpha(alpha);
        canvas.drawText(text, x, y, textPaint);
    }

    /**
     * 绘制彩色图片，通过与原图片的混合实现图片颜色的变化
     */
    private void drawColorBitmap(Canvas canvas, float left, float top) {
        //绘制一个纯色的图片
        Bitmap colorBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas colorCanvas = new Canvas(colorBitmap);
        colorCanvas.drawBitmap(bitmap, 0, 0, null);

        RectF rectf = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        drawablePaint.setColor(color);
        drawablePaint.setAlpha(alpha);
        drawablePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        colorCanvas.drawRect(rectf, drawablePaint);
        canvas.drawBitmap(colorBitmap, left, top, null);
    }


    /**
     * 设置文本和图片的透明度，已实现颜色的渐变
     *
     * @param alpha
     */
    public void setPaintAlpha(int alpha) {
        this.alpha = alpha;
        invalidate();
    }


}
