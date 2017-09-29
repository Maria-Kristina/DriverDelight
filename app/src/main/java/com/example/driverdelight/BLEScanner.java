package com.example.driverdelight;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by Edward on 29/09/2017.
 */

public class BLEScanner {
    private HashMap<String, BluetoothDevice> devices;

    private MainActivity activity;
    private BluetoothAdapter adapter;

    private boolean isScanning;
    private Handler handler;

    private long scanPeriod;
    private int signalStrength;

    public BLEScanner(MainActivity activity, long scanPeriod, int signalStrength) {
        this.activity = activity;
        this.scanPeriod = scanPeriod;
        this.signalStrength = signalStrength;

        this.isScanning = false;
        this.handler = new Handler();

        final BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        adapter = bluetoothManager.getAdapter();
    }

    public boolean isScanning() {
        return isScanning;
    }

    public void start() {
        if (!bluetoothIsEnabled()) {
            enableBluetooth();
            stop();
        } else {
            scanLeDevice();
        }
    }

    private void scanLeDevice() {
        if (!isScanning) {
            Log.d("DeviceScan", "BLE scanning started");
            Toast.makeText(activity, "Scanning for device", Toast.LENGTH_SHORT).show();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("DeviceScan", "BLE scanning stopped");
                    Toast.makeText(activity, "Scan ended", Toast.LENGTH_SHORT).show();
                    isScanning = false;
                    adapter.stopLeScan(bleScanCallback);
                }
            }, scanPeriod);
            isScanning = true;
            adapter.startLeScan(bleScanCallback);
        }
    }

    public void stop() {
        if (isScanning) {
            adapter.startLeScan(bleScanCallback);
            isScanning = false;
        }
    }

    private BluetoothAdapter.LeScanCallback bleScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            final int rssi = i;
            if (rssi > signalStrength) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        addDevice(bluetoothDevice, rssi);
                    }
                });
            }
        }
    };

    private void addDevice(BluetoothDevice bluetoothDevice, int rssi) {
        String addr = bluetoothDevice.getAddress();

        if (!devices.containsKey(addr)) {
            Log.d("DeviceScan", "Device found: " + bluetoothDevice.getName());
            devices.put(addr, bluetoothDevice);
        }
    }

    // Check if bluetooth is enabled
    public boolean bluetoothIsEnabled() {
        return adapter != null && adapter.isEnabled();
    }

    // Enable bluetooth if not
    public void enableBluetooth() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, 1);
    }
}
