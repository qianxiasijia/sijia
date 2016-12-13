package com.qianxia.sijia.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qianxia.sijia.R;
import com.qianxia.sijia.entry.City;
import com.qianxia.sijia.entry.CityNameBean;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by tarena on 2016/10/17.
 */
public class CitySearchAdapter extends SijiaBaseListAdapter<CityNameBean> {
    public CitySearchAdapter(Context context, List datas) {
        super(context, datas);
    }

    public int getSetctionFromPosition(int position) {
        return (int) getItem(position).getLetter();
    }

    public int getPositionFromSection(int section) {
        for (int i = 0; i < mDatas.size(); i++) {
            if (section == getSetctionFromPosition(i)) {
                return i;
            }
        }

        return -10;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_listview_citysearch_dialogfragment, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_cityname_item_lsitview_citysearch);
            viewHolder.tvSection = (TextView) convertView.findViewById(R.id.tv_section_item_lsitview_citysearch);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CityNameBean city = getItem(position);
        if (position == getPositionFromSection(getSetctionFromPosition(position))) {
            viewHolder.tvSection.setVisibility(View.VISIBLE);
            viewHolder.tvSection.setText(String.valueOf(city.getLetter()));
        } else {
            viewHolder.tvSection.setVisibility(View.GONE);
        }
        viewHolder.tvName.setText(city.getCityName());


        return convertView;
    }

    class ViewHolder {
        TextView tvName, tvSection;
    }
}
