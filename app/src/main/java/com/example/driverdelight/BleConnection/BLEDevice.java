package com.example.driverdelight.BleConnection;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Edward on 30/09/2017.
 */

class BLEDevice {
    private BluetoothDevice device;
    private int rssi;

    BLEDevice(BluetoothDevice device) {
        this.device = device;
    }

    String getName() {
        return device.getName();
    }

    String getAddress() {
        return device.getAddress();
    }

    int getRSSI() {
        return rssi;
    }

    void setRSSI(int rssi) {
        this.rssi = rssi;
    }
}
