package com.example.driverdelight;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by User1 on 22.9.2017.
 */

public class CustomAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> phoneList;
    private LayoutInflater mInflater;

    private TextView nameView;


    public CustomAdapter(Context context, List<String> list) {
        this.mContext = context;
        this.phoneList = list;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return phoneList.size();
    }

    @Override
    public Object getItem(int position) {
        return phoneList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View rowView = mInflater.inflate(R.layout.list_item_layout, viewGroup, false);
        nameView = rowView.findViewById(R.id.nameView);
        Object o = getItem(position);
        nameView.setText(o.toString());

        return rowView;
    }
}
