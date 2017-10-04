package com.example.driverdelight;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.android.BtleService;
import com.mbientlab.metawear.module.Led;

import bolts.Continuation;
import bolts.Task;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        ServiceConnection, AddressDialogFragment.OnDialogConfirmListener {
    private static final String DEFAULT_MAC_ADDRESS = "Ff:e3:70:08:b9:0d".toUpperCase();
    private static final String PREFERENCE_KEY = "AddressData";
    private static final String ADDRESS_KEY = "addressKey";

    private static final int REQUEST_SCAN_FOR_DEVICE = 1;
    private BtleService.LocalBinder serviceBinder;
    private MetaWearBoard board = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        getApplicationContext().bindService(new Intent(this, BtleService.class), this, BIND_AUTO_CREATE);

        ImageButton phoneButton = (ImageButton)findViewById(R.id.phoneButton);
        ImageButton spotifyButton = (ImageButton)findViewById(R.id.spotifyButton);
        phoneButton.setOnClickListener(this);
        spotifyButton.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        // TO-DO: Implement toolbar action

        switch (item.getItemId()) {
            case R.id.action_scan:
                SharedPreferences data = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
                String address = data.getString(ADDRESS_KEY, DEFAULT_MAC_ADDRESS);

                makeToast("Connecting");
                retrieveMetaWearDevice(address);
                break;
            case R.id.action_address:
                AddressDialogFragment dialog = new AddressDialogFragment();
                dialog.show(getSupportFragmentManager(), "manual_address");
                //Toast.makeText(this, "Manually enter address", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_last:
                retrieveMetaWearDevice(DEFAULT_MAC_ADDRESS);
                break;
            case R.id.action_disconnect:
                if (board != null && board.isConnected()) {
                    disconnectDevice();
                }
                break;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        disconnectDevice();
        getApplicationContext().unbindService(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();

        switch (view.getId()) {

            case R.id.phoneButton:
                intent.setClass(getBaseContext(), PhoneActivity.class);
                break;


            case R.id.spotifyButton:
                try {
                    intent.setComponent(new ComponentName("com.spotify.music", "com.spotify.music.MainActivity"));
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                } catch ( ActivityNotFoundException e ) {
                    Log.d("ONCLICK", e.toString());
                }
                break;

        }
        startActivity(intent);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        serviceBinder = (BtleService.LocalBinder) iBinder;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SCAN_FOR_DEVICE) {
            if (resultCode == RESULT_OK) {
                String address = data.getExtras().getString("device_address");
                retrieveMetaWearDevice(address);
            } else if (resultCode == RESULT_CANCELED) makeToast("Cancelled");
        }
    }

    private void retrieveMetaWearDevice(String address) {
        SharedPreferences data = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor dataOutput = data.edit();
        dataOutput.putString(ADDRESS_KEY, address).apply();

        final BluetoothDevice remoteDevice = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE))
                .getAdapter().getRemoteDevice(address);
        board = serviceBinder.getMetaWearBoard(remoteDevice);

        board.connectAsync().continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                if (task.isFaulted()) {
                    makeToast("Failed to connect");
                } else {
                    deviceConnected();
                }
                return null;
            }
        });
    }

    private void deviceConnected() {

        board.onUnexpectedDisconnect(new MetaWearBoard.UnexpectedDisconnectHandler() {
            @Override
            public void disconnected(int status) {
                attemptToReconnect(3);
            }
        });

        Led led;
        if ((led = board.getModule(Led.class)) != null) {
            led.editPattern(Led.Color.GREEN, Led.PatternPreset.PULSE)
                    .repeatCount((byte) 5)
                    .commit();
            led.play();
        }
    }

    private void attemptToReconnect(int tries) {
        Log.d("MainActivity", "Reconnecting " + tries);
        if (tries-- == 0) {
            makeToast("Unable to reconnect");
            return;
        }
        final int finalTries = tries;
        board.connectAsync().continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                if (task.isFaulted()) {
                    attemptToReconnect(finalTries);
                } else makeToast("Reconnected");
                return null;
            }
        });
    }

    private void disconnectDevice() {
        Led led = board.getModule(Led.class);
        if (led != null) {
            led.editPattern(Led.Color.RED, Led.PatternPreset.BLINK).repeatCount((byte) 5).commit();
            led.play();
        }
        board.disconnectAsync().continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                makeToast("Device disconnected");
                return null;
            }
        });
    }

    @Override
    public void onDialogConfirm(String address) {
        makeToast("Connecting");
        retrieveMetaWearDevice(address);
    }

    private void makeToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
