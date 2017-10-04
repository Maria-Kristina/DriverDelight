package com.example.driverdelight;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.android.BtleService;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.data.Acceleration;
import com.mbientlab.metawear.module.Accelerometer;
import com.mbientlab.metawear.module.Led;

import bolts.Continuation;
import bolts.Task;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        ServiceConnection, AddressDialogFragment.OnDialogConfirmListener, SensorEventListener {

    // Default device mac address
    private static final String DEFAULT_MAC_ADDRESS = "Ff:e3:70:08:b9:0d".toUpperCase();

    // SharedPreference keys
    private static final String PREFERENCE_KEY = "AddressData";
    private static final String ADDRESS_KEY = "addressKey";

    // Service binder
    private BtleService.LocalBinder serviceBinder;

    // MetaWear board
    private MetaWearBoard board = null;
    private DataProcessor dataProcessor;

    // Volume adjusting
    private Handler handler;
    private int musicVolume;
    private boolean backToNormal;
    private boolean isConsistence;
    private boolean allowSwitch;

    private SensorManager mSensorManager;
    private Sensor mLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActivityBackgroundColor(ContextCompat.getColor(this, R.color.colorBackgroundLight));

        // Initialize Handler and DataProcessor
        handler = new Handler();
        dataProcessor = new DataProcessor();

        // Set booleans
        backToNormal = true;
        isConsistence = true;
        allowSwitch = true;

        // Create action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable bluetooth scanning
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
        BluetoothAdapter bluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);
        }

        // Bind BLE service
        getApplicationContext().bindService(new Intent(this, BtleService.class), this, BIND_AUTO_CREATE);

        ImageButton phoneButton = (ImageButton)findViewById(R.id.phoneButton);
        ImageButton spotifyButton = (ImageButton)findViewById(R.id.spotifyButton);
        phoneButton.setOnClickListener(this);
        spotifyButton.setOnClickListener(this);

        /**LightSensor implementation*/
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

    }

    @Override
    protected void onDestroy() {
        if (board != null && board.isConnected()) disconnectDevice();
        getApplicationContext().unbindService(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    /**
     * Button method
     */
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

                } catch (ActivityNotFoundException e) {
                    Log.d("ONCLICK", e.toString());
                }
                break;

        }
        startActivity(intent);
    }

    /**
     * Action bar methods
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Scan for menu item's id
        switch (item.getItemId()) {

            // Auto connect to previous address
            case R.id.action_connect:

                // Retrieve address from SharedPreference, or get default address if no data available
                SharedPreferences data = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
                String address = data.getString(ADDRESS_KEY, DEFAULT_MAC_ADDRESS);

                retrieveMetaWearDevice(address);
                break;

            // Manually enter device address to connect
            case R.id.action_address:

                // Create dialog to input address
                AddressDialogFragment dialog = new AddressDialogFragment();
                dialog.show(getSupportFragmentManager(), "manual_address_dialog");
                break;

            // Connect to default device (hardcoded)
            case R.id.action_default:
                retrieveMetaWearDevice(DEFAULT_MAC_ADDRESS);
                break;

            // Disconnect from device
            case R.id.action_disconnect:
                if (board != null && board.isConnected()) {
                    disconnectDevice();
                }
                break;
        }

        return true;
    }

    /**
     Dialog methods
    */
    @Override
    public void onDialogConfirm(String address) {
        // Connect to device
        retrieveMetaWearDevice(address);
    }

    /**
     Service binder methods
    */
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        serviceBinder = (BtleService.LocalBinder) iBinder;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    /**
     BLE and MetaWear connection methods
    */
    private void retrieveMetaWearDevice(String address) {
        Log.i("MainActivity", getString(R.string.toast_connecting) + " to " + address);

        // Save device address to SharedPreference
        SharedPreferences data = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor dataOutput = data.edit();
        dataOutput.putString(ADDRESS_KEY, address).apply();

        // Connect to device
        final BluetoothDevice remoteDevice = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE))
                .getAdapter().getRemoteDevice(address);
        board = serviceBinder.getMetaWearBoard(remoteDevice);
        board.connectAsync().continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                if (task.isFaulted()) {
                    Log.i("MainActivity", getString(R.string.toast_failed_to_connect));
                } else {
                    deviceConnected();
                }
                return null;
            }
        });
    }

    private void deviceConnected() {
        Log.i("MainActivity", getString(R.string.toast_connected));

        // Try to reconnect 3 times if device disconnects unexpectedly
        board.onUnexpectedDisconnect(new MetaWearBoard.UnexpectedDisconnectHandler() {
            @Override
            public void disconnected(int status) {
                attemptToReconnect(3);
            }
        });

        // Show some signals on connected device
        Led led = board.getModule(Led.class);
        if (led != null) {
            led.editPattern(Led.Color.GREEN, Led.PatternPreset.BLINK)
                    .repeatCount((byte) 5)
                    .commit();
            led.play();
        }
        onMetaWearConnected();
    }

    private void attemptToReconnect(int tries) {
        Log.i("MainActivity", "Attempt to reconnect: " + tries);
        if (tries-- == 0) {
            Log.i("MainActivity", getString(R.string.toast_unable_to_reconnect));
            return;
        }
        final int finalTries = tries;
        board.connectAsync().continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                if (task.isFaulted()) {
                    attemptToReconnect(finalTries);
                } else {
                    Log.i("MainActivity", getString(R.string.toast_reconnected));
                }
                return null;
            }
        });
    }

    private void disconnectDevice() {
        // Show signal on device before disconnecting
        Led led = board.getModule(Led.class);
        if (led != null) {
            led.editPattern(Led.Color.RED, Led.PatternPreset.BLINK).repeatCount((byte) 5).commit();
            led.play();
        }
        board.disconnectAsync().continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                Log.i("MainActivity", getString(R.string.toast_disconnected));
                return null;
            }
        });
    }

    /**
     * MetaWear data processing methods
     */
    private void onMetaWearConnected() {

        // Retrieve and configure accelerometer
        final Accelerometer acc = board.getModule(Accelerometer.class);
        if (acc != null) {
            acc.configure()
                    .odr(5f)        // Frequency = 5Hz
                    .range(4f)      // Range = 4g
                    .commit();
            acc.acceleration().addRouteAsync(new RouteBuilder() {
                @Override
                public void configure(RouteComponent source) {
                    source.stream(new Subscriber() {
                        @Override
                        public void apply(Data data, Object... env) {

                            // Get acceleration value
                            double acceleration = dataProcessor.newData(data.value(Acceleration.class));
                            Log.i("MainActivity", "Acceleration: " + acceleration);

                            // React if acceleration value exceed threshold
                            if (acceleration > 0.1) {
                                // Turn down the volume if not already done so
                                volumeDown();
                                // Signal that car speed is not stable for 7.5 seconds
                                isConsistence = false;
                            }
                            // If acceleration goes back below threshold
                            else {
                                // If volume has been altered
                                if (!backToNormal) {
                                    // Prevent running multiple handlers
                                    if (allowSwitch) {
                                        allowSwitch = false;
                                        // Car speed is stable
                                        isConsistence = true;
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // If car speed stays stable for 7.5 seconds
                                                // turn volume up to normal
                                                if (!backToNormal && isConsistence) volumeUp();
                                                // Enable calling handler
                                                allowSwitch = true;
                                            }
                                        }, 7500);
                                    }
                                }
                            }
                        }
                    });
                }
            }).continueWith(new Continuation<Route, Void>() {
                @Override
                public Void then(Task<Route> task) throws Exception {
                    acc.acceleration().start();
                    acc.start();
                    return null;
                }
            });
        } else Log.i("MainActivity", "No accelerator detected");
    }


    /**
     * Volume altering methods
     */
    private void volumeDown() {
        // Prevent turning volume down multiple times
        if (backToNormal) {
            backToNormal = false;

            // Get current volume value and turn down to 60%
            AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            musicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) Math.round(musicVolume * 0.6), AudioManager.FLAG_SHOW_UI);
        }
    }

    private void volumeUp() {
        // Resetting volume value and enable turning volume down again
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, musicVolume, AudioManager.FLAG_SHOW_UI);
        backToNormal = true;
    }

    /**
     Sensor methods
    */
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if(sensor.getType() == Sensor.TYPE_LIGHT){
            Log.i("Sensor Changed", "Accuracy :" + accuracy);
        }
    }

    public void onSensorChanged(SensorEvent event) {

        if( event.sensor.getType() == Sensor.TYPE_LIGHT){
            if (event.values[0] < 400) {
                setActivityBackgroundColor(ContextCompat.getColor(this, R.color.colorBackgroundDark));

            } else {
                setActivityBackgroundColor(ContextCompat.getColor(this, R.color.colorBackgroundLight));
            }
        }
    }

    public void setActivityBackgroundColor(int color) {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(color);

    }

}
