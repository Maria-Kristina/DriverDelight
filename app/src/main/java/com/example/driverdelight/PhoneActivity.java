package com.example.driverdelight;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by User1 on 22.9.2017.
 */

public class PhoneActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);
        if (savedInstanceState == null){
            getFragmentManager().beginTransaction().add(R.id.fragmentHolder, new FragmentList()).commit();
        }
    }
}
