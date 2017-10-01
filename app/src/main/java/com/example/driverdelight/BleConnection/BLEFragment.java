package com.example.driverdelight.BleConnection;

import android.app.ListFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.driverdelight.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Edward on 01/10/2017.
 */

public class BLEFragment extends ListFragment {
    private static final long SCAN_PERIOD = 10000;
    private static final int SIGNAL_STRENGTH = -100;

    private BLEAdapter bleAdapter;
    private ArrayList<BLEDevice> deviceArrayList;
    private HashMap<String, BLEDevice> deviceHashMap;

    private boolean isScanning;
    private Handler handler;

    private OnBleItemClickListener listener;
    private BluetoothAdapter.LeScanCallback bleScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            final int rssi = i;
            if (rssi > SIGNAL_STRENGTH) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        addDevice(bluetoothDevice, rssi);
                    }
                });
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listener = (OnBleItemClickListener) getActivity();

        deviceArrayList = new ArrayList<>();
        deviceHashMap = new HashMap<>();
        handler = new Handler();
        isScanning = false;

        bleAdapter = new BLEAdapter(getActivity(), R.layout.bt_list_item_layout, deviceArrayList);
        setListAdapter(bleAdapter);
    }

    public void start() {
        final BluetoothAdapter bluetoothAdapter = ((BluetoothManager) getActivity()
                .getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent btIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            getActivity().startActivityForResult(btIntent, 1);
        } else if (!isScanning) {
            makeToast("Scanning");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isScanning)
                    isScanning = false;
                    bluetoothAdapter.stopLeScan(bleScanCallback);
                }
            }, SCAN_PERIOD);
            isScanning = true;
            bluetoothAdapter.startLeScan(bleScanCallback);
        } else makeToast("Scan already in progress");
    }

    private void addDevice(BluetoothDevice bluetoothDevice, int rssi) {
        String addr = bluetoothDevice.getAddress();
        if (!deviceHashMap.containsKey(addr)) {
            BLEDevice newDevice = new BLEDevice(bluetoothDevice);
            newDevice.setRSSI(rssi);
            deviceHashMap.put(addr, newDevice);
            deviceArrayList.add(newDevice);
        } else deviceHashMap.get(addr).setRSSI(rssi);
        bleAdapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        isScanning = false;
        listener.onBleItemClick(deviceArrayList.get(position).getAddress());
    }

    private void makeToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    interface OnBleItemClickListener {
        void onBleItemClick(String addr);
    }
}
