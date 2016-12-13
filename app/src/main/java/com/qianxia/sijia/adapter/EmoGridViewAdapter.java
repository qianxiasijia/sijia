package com.qianxia.sijia.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qianxia.sijia.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/10.
 */
public class EmoGridViewAdapter extends SijiaBaseListAdapter<String> {
    public EmoGridViewAdapter(Context context, List datas) {
        super(context, datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_gridview_postcomment_emo, parent, false);
            vHolder = new ViewHolder(convertView);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }

        String emoStr = getItem(position);
//        Log.i("DEBUG","emoStr:"+emoStr);
        String name = emoStr.substring(1, emoStr.length() - 1);
//        Log.i("DEBUG","name:"+name);
        int resId = mContext.getResources().getIdentifier(name, "drawable", mContext.getPackageName());
//        Log.i("DEBUG","resId:"+resId);
        vHolder.ivEmo.setImageResource(resId);
        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.iv_item_gridview_postcomment_emo)
        ImageView ivEmo;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }
    }
}
