package com.example.driverdelight;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends BaseAdapter {
    private Context mContext;
    private List<Contact> phoneList;
    private LayoutInflater mInflater;
    private TextView nameView;

    public CustomAdapter(Context context, List<Contact> list) {
        this.mContext = context;
        this.phoneList = list;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // Counts the length of the given list
    @Override
    public int getCount() {
        return phoneList.size();
    }

    //get a particular item from the list
    @Override
    public Contact getItem(int position) {
        return phoneList.get(position);
    }

    //Get the ID of a particular item of the list
    @Override
    public long getItemId(int position) {
        return position;
    }

    //Defines a view for a single row
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View rowView = mInflater.inflate(R.layout.list_item_layout, viewGroup, false);
        nameView = rowView.findViewById(R.id.nameView);
        Contact contact = getItem(position);
        nameView.setText(contact.getName());

        return rowView;
    }
}
