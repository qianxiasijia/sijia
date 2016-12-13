package com.qianxia.sijia.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.qianxia.sijia.R;

/**
 * Created by Administrator on 2016/8/21.
 */
public class SijiaRadioButton extends RadioButton {
    private Drawable drawableBottom, drawableLeft, drawableTop, drawableRight;
    private int mTopWidth, mTopHeight, mBottomWidth, mBottomHeight, mRightWidth, mRightHeight, mLeftWidth, mLeftHeight;

//    public SijiaRadioButton(Context context) {
//        super(context);
//    }

    public SijiaRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (attrs != null) {
            float scale = context.getResources().getDisplayMetrics().density;
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.sijia);
            int n = a.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = a.getIndex(i);
                switch (attr) {
                    case R.styleable.sijia_sijiaDrawableBottom:
                        drawableBottom = a.getDrawable(attr);
                        break;
                    case R.styleable.sijia_sijiaDrawableTop:
                        drawableTop = a.getDrawable(attr);
                        break;
                    case R.styleable.sijia_sijiaDrawableLeft:
                        drawableLeft = a.getDrawable(attr);
                        break;
                    case R.styleable.sijia_sijiaDrawableRight:
                        drawableRight = a.getDrawable(attr);
                        break;
                    case R.styleable.sijia_sijiaDrawableTopWidth:
                        mTopWidth = (int) (a.getDimension(attr, 20) * scale + 0.5f);
                        break;
                    case R.styleable.sijia_sijiaDrawableTopHeight:
                        mTopHeight = (int) (a.getDimension(attr, 20) * scale + 0.5f);
                        break;
                    case R.styleable.sijia_sijiaDrawableBottomWidth:
                        mBottomWidth = (int) (a.getDimension(attr, 20) * scale + 0.5f);
                        break;
                    case R.styleable.sijia_sijiaDrawableBottomHeight:
                        mBottomHeight = (int) (a.getDimension(attr, 20) * scale + 0.5f);
                        break;
                    case R.styleable.sijia_sijiaDrawableRightWidth:
                        mRightWidth = (int) (a.getDimension(attr, 20) * scale + 0.5f);
                        break;
                    case R.styleable.sijia_sijiaDrawableRightHeight:
                        mRightHeight = (int) (a.getDimension(attr, 20) * scale + 0.5f);
                        break;
                    case R.styleable.sijia_sijiaDrawableLeftWidth:
                        mLeftWidth = (int) (a.getDimension(attr, 20) * scale + 0.5f);
                        break;
                    case R.styleable.sijia_sijiaDrawableLeftHeight:
                        mLeftHeight = (int) (a.getDimension(attr, 20) * scale + 0.5f);
                        break;

                    default:
                        break;
                }
            }
            a.recycle();
            setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
        }

    }

//    public SijiaRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        if (left != null) {
            left.setBounds(0, 0, mLeftWidth <= 0 ? left.getIntrinsicWidth() : mLeftWidth, mLeftHeight <= 0 ? left.getMinimumHeight() : mLeftHeight);
        }
        if (right != null) {
            right.setBounds(0, 0, mRightWidth <= 0 ? right.getIntrinsicWidth() : mRightWidth, mRightHeight <= 0 ? right.getMinimumHeight() : mRightHeight);
        }
        if (top != null) {
            top.setBounds(0, 0, mTopWidth <= 0 ? top.getIntrinsicWidth() : mTopWidth, mTopHeight <= 0 ? top.getMinimumHeight() : mTopHeight);
        }
        if (bottom != null) {
            bottom.setBounds(0, 0, mBottomWidth <= 0 ? bottom.getIntrinsicWidth() : mBottomWidth, mBottomHeight <= 0 ? bottom.getMinimumHeight()
                    : mBottomHeight);
        }
        setCompoundDrawables(left, top, right, bottom);
    }
}