package com.example.driverdelight;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

public class PhoneActivity extends Activity implements SensorEventListener, OnItemSelectedListener {

    Contact mContact;
    private SensorManager mSensorManager;
    private Sensor mLight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);
        setActivityBackgroundColor(ContextCompat.getColor(this, R.color.colorBackgroundLight));
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.fragmentHolder, new FragmentList()).commit();
        }
        /**LightSensor implementation*/
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    public void itemSelected(Contact contact) {
        this.mContact = contact;
        Log.d("ONITEMSELECTED", contact.getName());

    }

    @Override
    public Contact getItemSelected() {
        return mContact;
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

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() == Sensor.TYPE_LIGHT) {
            Log.i("Sensor Changed", "Accuracy :" + accuracy);
        }
    }


    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {

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

