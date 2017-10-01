package com.example.driverdelight.BleConnection;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.driverdelight.R;

/**
 * Created by Edward on 01/10/2017.
 */

public class BLEActivity extends AppCompatActivity implements BLEFragment.OnBleItemClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);


        getFragmentManager().beginTransaction().add(R.id.fragmentHolder, new BLEFragment()).commit();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE not supported", Toast.LENGTH_SHORT).show();
        } else {
            ((BLEFragment) getFragmentManager().findFragmentById(R.id.fragmentHolder)).start();
        }
    }

    @Override
    public void onBleItemClick(String addr) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("device_address", addr);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
