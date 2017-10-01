package com.example.driverdelight.BleConnection;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.driverdelight.R;

import java.util.ArrayList;

/**
 * Created by Edward on 30/09/2017.
 */

class BLEAdapter extends ArrayAdapter<BLEDevice> {
    private ArrayList<BLEDevice> devices;
    private int layoutResource;

    BLEAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<BLEDevice> objects) {
        super(context, resource, objects);
        this.devices = objects;
        this.layoutResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View targetView = convertView;
        if (targetView == null) {
            LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            targetView = li.inflate(layoutResource, null);
        }

        BLEDevice device = devices.get(position);

        TextView tv = targetView.findViewById(R.id.deviceName);
        tv.setText(device.getName() != null ? device.getName() : "Unnamed device");

        tv = targetView.findViewById(R.id.deviceAddr);
        tv.setText(device.getAddress());

        tv = targetView.findViewById(R.id.deviceRSSI);
        String rssi = "RSSI: " + device.getRSSI();
        tv.setText(rssi);

        return targetView;
    }
}
