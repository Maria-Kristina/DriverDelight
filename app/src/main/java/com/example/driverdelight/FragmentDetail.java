package com.example.driverdelight;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class FragmentDetail extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor mProximity;

    private TextView nameView, numberView;
    Activity activity;
    private static final int PERMISSIONS_REQUEST_CALL_PHONE = 500;
    private ImageButton imgBtn;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment, container, false);

        // Get system service to interact with sensors
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);

        // Find default proximity sensor
        mProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        nameView = view.findViewById(R.id.textView);
        numberView = view.findViewById(R.id.numberView);
        imgBtn = view.findViewById(R.id.callButton);

        try {
            nameView.setText((((OnItemSelectedListener) activity).getItemSelected()).getName());
            numberView.setText((((OnItemSelectedListener) activity).getItemSelected()).getPhoneNumber());
        } catch (ClassCastException e) {
            Log.d("DetailFragmentEXCEPTION", e.toString());
        }

        // If button is pressed a call will be made
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && activity.checkSelfPermission(Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(
                            new String[]{Manifest.permission.CALL_PHONE},
                            PERMISSIONS_REQUEST_CALL_PHONE);
                } else {

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    callIntent.setData(Uri.parse("tel:" + numberView.getText()));
                    getActivity().startActivity(callIntent);

                }
            }
        });

        return view;
    }

    // Starts to follow the sensors data
    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, mProximity,
                sensorManager.SENSOR_DELAY_NORMAL);
    }

    // Stops following the sensors data
    @Override
    public void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this);
    }

    // Measures the amount of light
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() == Sensor.TYPE_LIGHT) {
        }
    }

    // If the proximity sensor gets a change it will call
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {

            if (event.values[0] < mProximity.getMaximumRange()) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && activity.checkSelfPermission(Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(
                            new String[]{Manifest.permission.CALL_PHONE},
                            PERMISSIONS_REQUEST_CALL_PHONE);
                } else {

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    callIntent.setData(Uri.parse("tel:" + numberView.getText()));
                    getActivity().startActivity(callIntent);
                }
            }
        }
    }
}

