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
import android.widget.TextView;

public class FragmentDetail extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor mPoximity;

    private TextView nameView, numberView;
    Activity activity;
    private static final int PERMISSIONS_REQUEST_CALL_PHONE = 500;


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

        /** Get system service to interact with sensors */
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);

        /** Find default proximity sensor */
        mPoximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        nameView = view.findViewById(R.id.textView);
        numberView = view.findViewById(R.id.numberView);

        try {
            nameView.setText((((OnItemSelectedListener) activity).getItemSelected()).getName());
            numberView.setText((((OnItemSelectedListener) activity).getItemSelected()).getPhoneNumber());
        } catch (ClassCastException e) {
            Log.d("DetailFragmentEXCEPTION", e.toString());
        }

        numberView.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() == Sensor.TYPE_LIGHT) {
            Log.i("Sensor Changed", "Accuracy :" + accuracy);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {

            int val = (int) event.values[0];

        }

    }
}
