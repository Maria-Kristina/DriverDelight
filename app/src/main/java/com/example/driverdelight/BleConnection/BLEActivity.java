package com.example.driverdelight.BleConnection;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getFragmentManager().beginTransaction().add(R.id.fragmentHolder, new BLEFragment()).commit();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE not supported", Toast.LENGTH_SHORT).show();
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((BLEFragment) getFragmentManager().findFragmentById(R.id.fragmentHolder)).start();
                }
            }, 100);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.empty_action_bar, menu);
        return true;
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
