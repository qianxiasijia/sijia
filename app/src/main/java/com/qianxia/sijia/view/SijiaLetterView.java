package com.qianxia.sijia.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.qianxia.sijia.R;

public class SijiaLetterView extends View {

    private String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
    private Paint paint;

    private int color;

    private OnTouchLetterListener listener;

    private TextView tvLetter;


    public SijiaLetterView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SijiaLetterView);
        color = array.getColor(R.styleable.SijiaLetterView_letter_color, Color.BLACK);
        array.recycle();
        initialPaint();

    }

    private void initialPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()));
        paint.setColor(color);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST) {
            int leftPadding = getPaddingLeft();
            int rightPadding = getPaddingRight();

            int textWidth = 0;
            for (int i = 0; i < letters.length; i++) {
                Rect bounds = new Rect();
                paint.getTextBounds(letters[i], 0, letters[i].length(), bounds);
                if (bounds.width() > textWidth) {
                    textWidth = bounds.width();
                }
            }
            int width = leftPadding + rightPadding + textWidth;

            setMeasuredDimension(width, MeasureSpec.getSize(heightMeasureSpec));
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        int width = getWidth();
        int height = getHeight() / letters.length;
        for (int i = 0; i < letters.length; i++) {
            Rect bounds = new Rect();
            paint.getTextBounds(letters[i], 0, letters[i].length(), bounds);
            int w = bounds.width();
            int h = bounds.height();

            int x = width / 2 - w / 2;
            int y = height / 2 + h / 2 + height * i;
            canvas.drawText(letters[i], x, y, paint);
        }
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                setBackground(getResources().getDrawable(R.drawable.shape_hotcity_n));
                float y = event.getY();
                int idx = (int) (y * letters.length / getHeight());
                if (idx >= 0 && idx < letters.length) {
                    String letter = letters[idx];
                    if (listener != null) {
                        listener.onTouchLetter(letter);
                        if (tvLetter != null) {
                            tvLetter.setVisibility(View.VISIBLE);
                            tvLetter.setText(letter);
                        }

                    }
                }
                break;

            default:
                setBackgroundColor(Color.TRANSPARENT);
                if (listener != null) {
                    listener.onReleaseLetter();
                }
                if (tvLetter != null) {
                    tvLetter.setVisibility(View.INVISIBLE);
                    tvLetter.setText("");
                }
                break;
        }
        return true;
    }

    public void setTextView(TextView tv) {
        tvLetter = tv;
    }

    public void setOnTouchLetterListener(OnTouchLetterListener listener) {
        this.listener = listener;
    }

    public interface OnTouchLetterListener {

        void onTouchLetter(String letter);

        void onReleaseLetter();
    }

}
