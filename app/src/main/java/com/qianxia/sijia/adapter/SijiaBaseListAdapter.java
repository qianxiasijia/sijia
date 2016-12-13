package com.qianxia.sijia.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.qianxia.sijia.constant.Constant;

import java.util.List;

/**
 * Created by tarena on 2016/9/8.
 */
public abstract class SijiaBaseListAdapter<T> extends BaseAdapter {
    private final Toast toast;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;
    protected Context mContext;

    public SijiaBaseListAdapter(Context context, List<T> datas) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDatas = datas;
        mContext = context;
        toast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);

    }

    public void addAll(List<T> datas, boolean flag) {
        if (datas == null) {
            return;
        }
        if (flag) {
            mDatas.clear();
        }
//        Log.i("TAG：datas",datas.toString());
//        Log.i("TAG:mDatas",mDatas.toString());
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void remove(T t) {
        mDatas.remove(t);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

    public void log(String log) {
        if (Constant.DEBUG) {
            Log.i("DEBUG", "从" + this + "打印出来的调试日记：" + log);
        }
    }

    public void toast(String text) {
        if (!TextUtils.isEmpty(text)) {
            toast.setText(text);
            toast.show();
        }
    }

    public void log(int code, String log) {
        log("错误信息：" + code + ":" + log);
    }

    public void toastAndLog(String text, String log) {
        toast(text);
        log(log);
    }

    public void toastAndLog(String text, int code, String log) {
        toast(text);
        log(code, log);
    }

}
