package com.qianxia.sijia.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qianxia.sijia.R;
import com.qianxia.sijia.entry.Region;
import com.qianxia.sijia.entry.SubCategoryTable;

import java.util.List;

/**
 * Created by Administrator on 2016/11/24.
 */
public class RegionSpinnerAdapter extends SijiaBaseListAdapter<Region> {
    public RegionSpinnerAdapter(Context context, List<Region> datas) {
        super(context, datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_spinner_textview, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_spinner_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Region region = getItem(position);
        viewHolder.textView.setText(region.getName());

        return convertView;
    }

    class ViewHolder {
        TextView textView;
    }
}
